package shop.exception;

public class NoSuchItemException extends CustomException {
    public NoSuchItemException() {
        super("Item not found");
    }
}
