package cue.edu.co.inventariopruebas.domain.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, String value) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, value));
    }
}
