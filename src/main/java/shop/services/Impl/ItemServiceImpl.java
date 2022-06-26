package shop.services.Impl;

import shop.entities.Item;
import shop.entities.dto.ImportDTO;
import shop.entities.dto.ItemDTO;
import shop.entities.dto.ItemListDTO;
import shop.entities.enums.ItemType;
import shop.exception.InvalidParentException;
import shop.exception.NoSuchItemException;
import shop.repositories.ItemRepository;
import shop.services.ItemService;
import shop.services.ThrowingConsumer;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
  private final ItemRepository itemRepository;

  final AuditReader auditReader;

  public ItemServiceImpl(ItemRepository itemRepository, AuditReader auditReader) {
    this.itemRepository = itemRepository;
    this.auditReader = auditReader;
  }


  @Override
  public Optional<Item> getItem(UUID id) throws NoSuchItemException {
    Optional<Item> itemWrapper = itemRepository.findById(id);
    if (itemWrapper.isEmpty())
      throw new NoSuchItemException();
    else
      return itemRepository.findById(id);
  }

  @Override
  public void importItems(final ImportDTO importDTO) {

    String date = importDTO.getUpdateDate();

    importDTO.getItems().forEach(throwingConsumerWrapper(itemDTO -> {
      Item newItem = null;
      Optional<Item> oldItemWrapper = itemRepository.findById(itemDTO.getId());
      if (oldItemWrapper.isEmpty()) {
        newItem = new Item(itemDTO);
        newItem.setDate(date);
      }
      else {
        newItem = new Item(itemDTO);
        newItem.setDate(date);
        newItem.setChildren(oldItemWrapper.get().getChildren());
        if (oldItemWrapper.get().getWholePrice() != null)
          newItem.setPrice(oldItemWrapper.get().getWholePrice());
        if (oldItemWrapper.get().getChildOfferAmount() != null)
          newItem.setChildOfferAmount(oldItemWrapper.get().getChildOfferAmount());
      }
      setAssociation(newItem, itemDTO);
      updateBranch(newItem);
      itemRepository.save(newItem);
    }));
  }

  @Override
  public void deleteItem(UUID id) throws EmptyResultDataAccessException, NoSuchItemException {
    Optional<Item> itemToDeleteWrapper = itemRepository.findById(id);
    if (itemToDeleteWrapper.isEmpty()) throw new NoSuchItemException();
    else {
      decreaseOldBranch(itemToDeleteWrapper.get(), null, itemToDeleteWrapper.get().getParent());
      itemRepository.deleteById(id);
    }
  }

  @Override
  public ItemListDTO getRevisions(UUID id, String dateStart, String dateEnd) {
    ItemListDTO dto = new ItemListDTO();

    if(itemRepository.existsById(id)) {
      AuditQuery auditQuery = null;

      auditQuery = auditReader.createQuery()
              .forRevisionsOfEntity(Item.class, true, false);
      auditQuery.add(AuditEntity.id().eq(id));
      List<Item> queryResult = auditQuery.getResultList();

      dto.setItems(queryResult.stream().filter(item -> {
        OffsetDateTime itemData = OffsetDateTime.parse(item.getDate());
        OffsetDateTime startDateOffset = OffsetDateTime.parse(dateStart);
        OffsetDateTime endDateOffset = OffsetDateTime.parse(dateEnd);
        return itemData.isAfter(startDateOffset) || itemData.isEqual(startDateOffset)
                && itemData.isBefore(endDateOffset);
      }).collect(Collectors.toList()));

//      dto.setItems(queryResult);
    }
    else dto.setItems(new ArrayList<>());

    return dto;
  }

  @Override
  public ItemListDTO getSales(String date) {
    List<Item> offers = itemRepository.getAllOffers();
    ItemListDTO dto = new ItemListDTO();

//    OffsetDateTime now = OffsetDateTime
//            .parse(OffsetDateTime.now(ZoneId.of("Z"))
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));


    dto.setItems(offers.stream().filter(item -> {
      OffsetDateTime itemData = OffsetDateTime.parse(item.getDate());
      OffsetDateTime queryDate = OffsetDateTime.parse(date);
      return itemData.isAfter(queryDate.minusHours(24)) || itemData.isEqual(queryDate.minusHours(24))
              && itemData.isBefore(queryDate);
    }).collect(Collectors.toList()));
//    dto.setItems(offers);
    return dto;
  }


  private void setAssociation(Item item, ItemDTO itemDTO) throws InvalidParentException, NoSuchItemException {
    if (itemDTO.getParentId() != null) { // если есть родитель
      Optional<Item> parent = itemRepository.findById(itemDTO.getParentId()); // получаем родителя
      if (parent.isPresent()) { // если такой родитель есть в базе данных
        if (parent.get().getType().equals(ItemType.CATEGORY)) {// если родитель это категория
          item.setParent(parent.get());
          item.setParentId(itemDTO.getParentId());
        }
        else throw new InvalidParentException("item \"" + parent.get().getName()
                + "\" (id: " + parent.get().getId() + ") is OFFER");
      } else throw new NoSuchItemException();
    }
    else item.setParent(null);
  }


  /**
   * ! Вставка обычная - while(parent != null) - обновление новой ветки: добавляем цену новой ветке
   * ! Обновление элемента - вставка из одной ветки в другую - while(parent != null) для старой и новой веток - из старой ветки вычитаем, новой - добавляем
   * ! Обновление элемента - перемещение в пределах ветки ВВЕРХ - уже сделано - while(parent != null) - у старого родителя и его ветки вычитаем цену,
   * пока не достигнем нового родителя. Новому и всем его родителям - прибавляем
   * ! Обновление элемента - перемещение в пределах ветки ВНИЗ - у старого родителя ничего не меняем, у нового и всех его родителей до СТАРОГО меням цену
   * while (parent !=null && parent.getId != oldParentID)
   *
   *
   * То есть, есть операции:
   *  1. Уменьшить старую ветку (всю или до встреи с элементом)
   *  2. Увеличить новую ветку (всю или до встречи с элементом)
   * */

  private void updateBranch(Item item) {
    //1. Если новый родитель - потомок старого, то это вставка ВНИЗ -> увеличиваем новую ветку до родителя. Вычитать ничего не нужно
    //2. Иначе вычитаю из строй ветки пока:
    //   а) не достигну нового родителя - это значит, что старый родитель - потомок нового, вставка ВВЕРХ
    //   б) пока не пройду всю ветку - это значит, что старый и новый родители - в разных ветках.

    Optional<Item> oldItemWrapper = itemRepository.findById(item.getId());
    Item newParent = item.getParent();
    Item oldParent = null;

    if (oldItemWrapper.isPresent() && oldItemWrapper.get().getParent() != null) // если у элемента есть старый и новый родители
      oldParent = oldItemWrapper.get().getParent();

    if (item.getParent() != null && oldParent != null) {

      if (isNewParentChildOfOldParent(oldParent, newParent)) { // то это вставка ВНИЗ -> увеличиваем новую ветку до старого родителя. Вычитать ничего не нужно
        increaseNewBranch(item, newParent, oldParent.getId());
      } else if (!decreaseOldBranch(oldItemWrapper.get(), item, oldParent, newParent.getId())) { // если не достиг нового родителя - вставка в разные ветки,нужно увеличить новую
        increaseNewBranch(item, newParent, newParent.getId());
      }
      // если достиг нового родителя - вставка ВВЕРХ, увеличивать новую ветку не нужно
    }
    else if (oldParent != null) // нового родителя нет, нужно вычесть старую ветку
      decreaseOldBranch(oldItemWrapper.get(), item, oldParent);
    else if (newParent != null) // старого родителя нет, есть новый, нужно обновить новую ветку
      increaseNewBranch(item, newParent);
  }


  private void increaseNewBranch(Item item, Item parent){
    increaseNewBranch(item, parent, null);
  }

  private void increaseNewBranch(Item item, Item parent, UUID stopId){
    while (parent != null){
      if (stopId != null && parent.getId().equals(stopId)) break; // если достигнут определенный id - выходим

      if (parent.getChildOfferAmount() == null) parent.setChildOfferAmount(0);
      if (parent.getWholePrice() == null) parent.setPrice(0);

      //увеличение у нового родителя кол-ва детей-товаров
      if (item.getType().equals(ItemType.CATEGORY)  && item.getChildOfferAmount() != null)
        parent.setChildOfferAmount(parent.getChildOfferAmount() + item.getChildOfferAmount());
      else if (item.getType().equals(ItemType.OFFER))
        parent.setChildOfferAmount(parent.getChildOfferAmount() + 1);

      //увеличение у нового родителя общей цены
      if (item.getWholePrice() != null)
        parent.setPrice(parent.getWholePrice() + item.getWholePrice());

      //обновление времени
      parent.setDate(item.getDate());

      parent = parent.getParent();
    }
  }


  private void decreaseOldBranch(Item oldItem, Item item, Item parent) {
    decreaseOldBranch(oldItem, item, parent, null);
  }
  private boolean decreaseOldBranch(Item oldItem, Item item, Item parent, UUID stopId) {

    //для обновления ВВЕРХ: от старого до нового у всех вычесть,  parent =

    while (parent != null) {
      if (stopId != null && parent.getId().equals(stopId)) return true; // если достигнут определенный id - выходим

      parent.setPrice(parent.getWholePrice() - oldItem.getWholePrice());
      if (oldItem.getType().equals(ItemType.CATEGORY) && oldItem.getChildOfferAmount() != null)
        parent.setChildOfferAmount(oldItem.getParent().getChildOfferAmount() - oldItem.getChildOfferAmount());
      else parent.setChildOfferAmount(oldItem.getParent().getChildOfferAmount() - 1);

      //обновление времени
      if (item != null)
        parent.setDate(item.getDate());

      parent = parent.getParent();
    }
    return false;
  }



  private boolean isNewParentChildOfOldParent(Item oldParent, Item newParent){
    while(newParent != null){
      if (newParent.getParentId() == oldParent.getParentId()){
        return true;
      }
      newParent = newParent.getParent();
    }
    return false;
  }


  static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
    return i -> {
      try {
        throwingConsumer.accept(i);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    };
  }
}
