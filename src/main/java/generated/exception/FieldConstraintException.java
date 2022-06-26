package generated.exception;

public class FieldConstraintException extends CustomException {
    public FieldConstraintException(String description) {
        super("invalid field format! " + description);
    }
}
