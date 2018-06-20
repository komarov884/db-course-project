package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.AuthorDAO;
import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.Author;

import java.util.List;

/**
 * Service for working with {@link Author}.
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class AuthorService extends AbstractService<Author> {
    private final AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        super(authorDAO);
        this.authorDAO = authorDAO;
    }

    public List<Author> findAllByName(String name) throws ServiceException {
        try {
            return authorDAO.findAllByName(name);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
