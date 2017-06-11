package crawlers.messenger;

public class MessengerInitializationException extends Exception {
    public MessengerInitializationException() {
    }

    public MessengerInitializationException(String message) {
        super(message);
    }

    public MessengerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessengerInitializationException(Throwable cause) {
        super(cause);
    }

    public MessengerInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
