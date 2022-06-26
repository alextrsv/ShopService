package shop.services;

import shop.entities.Item;
import shop.entities.dto.ImportDTO;
import shop.entities.dto.ItemListDTO;
import shop.exception.InvalidParentException;
import shop.exception.NoSuchItemException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;
import java.util.UUID;

public interface ItemService {
//  Optional<List<Item>> getAll();
//
  Optional<Item> getItem(final UUID id) throws NoSuchItemException;
  
  void importItems(final ImportDTO importDTO) throws InvalidParentException, NoSuchItemException;


  void deleteItem(UUID id) throws EmptyResultDataAccessException, NoSuchItemException;

  ItemListDTO getRevisions(UUID id, String dateStart, String dateEnd);

  ItemListDTO getSales(String date);
}
