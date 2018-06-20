package com.db.courseproject.musicstore.exception;

/**
 * The exception that throws when entity not found.
 * <p>
 * Created on 6/20/2018.
 *
 * @author Vasilii Komarov
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
