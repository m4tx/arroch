package crawlers.messenger;

public class MessengerSessionFieldNotFoundException extends MessengerInitializationException {
    private String fieldName;

    public MessengerSessionFieldNotFoundException(String fieldName) {
        super("Messenger Session field not found: " + fieldName);
        this.fieldName = fieldName;
    }
}
