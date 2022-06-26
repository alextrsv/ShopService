package generated.controllers;

import generated.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Контроллер обработки исключений
 * */

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler({InvalidParentException.class,  FieldConstraintException.class})
    public ResponseEntity<ErrorDTO> handleInvalidParent(CustomException ex) {
        return new ResponseEntity<>(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), "Validation Failed"), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoSuchItemException.class)
    public ResponseEntity<ErrorDTO> handleItemNotFound(NoSuchItemException ex) {
        return new ResponseEntity<>(new ErrorDTO(HttpStatus.NOT_FOUND.value(), "Item not found"), HttpStatus.NOT_FOUND);
    }

}
