package crawlers.facebook;

public class FacebookAuthException extends RuntimeException {
    public FacebookAuthException(String s) {
        super(s);
    }

    public FacebookAuthException() {
        super();
    }
}
