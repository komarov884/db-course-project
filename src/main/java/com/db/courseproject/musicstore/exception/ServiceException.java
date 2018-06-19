package com.db.courseproject.musicstore.exception;

/**
 * The exception that throws when working with service layer.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
