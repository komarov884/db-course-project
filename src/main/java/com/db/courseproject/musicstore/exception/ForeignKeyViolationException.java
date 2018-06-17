package com.db.courseproject.musicstore.exception;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class ForeignKeyViolationException extends RuntimeException {
    public ForeignKeyViolationException(String message) {
        super(message);
    }

    public ForeignKeyViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForeignKeyViolationException(Throwable cause) {
        super(cause);
    }
}
