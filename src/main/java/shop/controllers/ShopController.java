package shop.controllers;

import shop.entities.Item;
import shop.entities.dto.ImportDTO;
import shop.entities.dto.ItemListDTO;
import shop.exception.ErrorDTO;
import shop.exception.InvalidParentException;
import shop.exception.NoSuchItemException;
import shop.services.ItemService;
import shop.validators.ImportValidator;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер, реализующий API к разрабатываемому сервису
 * */

@RestController
public class ShopController {

  private final ItemService itemService;

  private final ImportValidator importValidator;

  public ShopController(ItemService itemService, ImportValidator importValidator) {
    this.itemService = itemService;
    this.importValidator = importValidator;
  }

  @GetMapping("nodes/{id}")
  public ResponseEntity<Item> getItem(@PathVariable(name = "id") UUID id) throws NoSuchItemException {
    return itemService.getItem(id).map(ResponseEntity::ok)
            .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PostMapping("/imports")
  public ResponseEntity<ImportDTO> addNewItem(@RequestBody final ImportDTO importDTO, BindingResult bindingResult) throws InvalidParentException, NoSuchItemException {
    importValidator.validate(importDTO, bindingResult);
    itemService.importItems(importDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @DeleteMapping("delete/{id}")
  public ResponseEntity deleteStudent(@PathVariable("id") UUID id) throws NoSuchItemException {
    try {
      itemService.deleteItem(id);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (EmptyResultDataAccessException exception) {
      return new ResponseEntity<>(new ErrorDTO(HttpStatus.NOT_FOUND.value(), "Item not found"), HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping(path = "/node/{id}/statistic", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ItemListDTO> getRevisions(@PathVariable(name = "id") UUID customerId,
                                                  @RequestParam(name = "dateStart") String dateStart,
                                                  @RequestParam(name = "dateEnd") String dateEnd) {
    return ResponseEntity.ok(itemService.getRevisions(customerId, dateStart, dateEnd));
  }

  @GetMapping(path = "/sales", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ItemListDTO> getRevisions(@RequestParam(name = "date") String date) {
    return ResponseEntity.ok(itemService.getSales(date));
  }
}
