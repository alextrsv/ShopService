package shop.entities.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ItemTypeConverter implements AttributeConverter<ItemType, String> {
 
    @Override
    public String convertToDatabaseColumn(ItemType type) {
        if (type == null) {
            return null;
        }
        return type.getName();
    }

    @Override
    public ItemType convertToEntityAttribute(String name) {
        if (name == null) {
            return null;
        }

        return Stream.of(ItemType.values())
          .filter(c -> c.getName().equals(name))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}