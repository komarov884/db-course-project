package com.db.courseproject.musicstore.exception;

/**
 * The exception that throws when working with DAO layer.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class DAOException extends RuntimeException {
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }
}
