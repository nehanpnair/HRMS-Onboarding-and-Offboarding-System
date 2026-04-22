package service;

/**
 * Exception thrown when account access/provisioning fails.
 */
public class AccessProvisionException extends Exception {
    public AccessProvisionException(String message) {
        super(message);
    }

    public AccessProvisionException(String message, Throwable cause) {
        super(message, cause);
    }
}
