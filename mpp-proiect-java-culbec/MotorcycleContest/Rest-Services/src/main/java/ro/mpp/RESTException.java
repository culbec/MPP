package ro.mpp;

public class RESTException extends Exception {
    public RESTException(String message) {
        super(message);
    }

    public RESTException(String message, Throwable cause) {
        super(message, cause);
    }
}