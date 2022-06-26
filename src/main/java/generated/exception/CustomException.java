package generated.exception;

public class CustomException extends Exception {
    String customMessage;
    public CustomException(String message){
        super(message);
        customMessage = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
