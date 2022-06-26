package shop.entities.dto;

import shop.entities.Item;

import java.util.List;

public class ItemListDTO {
    List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
