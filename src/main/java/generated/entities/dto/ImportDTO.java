package generated.entities.dto;

import lombok.Data;

import java.util.List;

/**
 * Класс DTO для импорта товаров и категорий
 * */

@Data
public class ImportDTO {

    List<ItemDTO> items;
    String updateDate;
}
