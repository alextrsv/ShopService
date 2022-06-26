package generated.exception;

public class InvalidParentException extends CustomException {
    public InvalidParentException(String description) {
        super("invalid parent! " + description);
    }
}
