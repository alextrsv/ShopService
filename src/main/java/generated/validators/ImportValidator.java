package generated.validators;


import generated.entities.dto.ImportDTO;
import generated.entities.dto.ItemDTO;
import generated.entities.enums.ItemType;
import generated.exception.FieldConstraintException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class ImportValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ImportDTO.class.equals(clazz);
    }

    @SneakyThrows
    @Override
    public void validate(Object target, Errors errors) {
        ImportDTO importDTO = (ImportDTO) target;

        if (!isValidISODateTime(importDTO.getUpdateDate()))
            throw new FieldConstraintException("date must be formatted according ISO 8601");

        for (ItemDTO itemDTO : importDTO.getItems()) {
            if (itemDTO.getName() == null || itemDTO.getName().equals(""))
                throw new FieldConstraintException("field 'name' must not be null or empty");
            if (ItemType.valueOf(itemDTO.getType()).equals(ItemType.CATEGORY) && itemDTO.getPrice() != null)
                throw new FieldConstraintException("category must not have price");
            else if (ItemType.valueOf(itemDTO.getType()).equals(ItemType.OFFER) && itemDTO.getPrice() == null)
                throw new FieldConstraintException("offer must have price");
        }
    }

    public boolean isValidISODateTime(String date) {
        try {
            DateTimeFormatter.ISO_DATE_TIME.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
