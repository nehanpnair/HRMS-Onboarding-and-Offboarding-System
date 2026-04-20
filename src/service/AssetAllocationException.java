package service;

/**
 * Exception thrown when asset allocation fails.
 */
public class AssetAllocationException extends Exception {
    public AssetAllocationException(String message) {
        super(message);
    }

    public AssetAllocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
