package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.AuthorDAO;
import com.db.courseproject.musicstore.model.Author;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class AuthorService extends AbstractService<Author> {
    public AuthorService(AuthorDAO authorDAO) {
        super(authorDAO);
    }
}
