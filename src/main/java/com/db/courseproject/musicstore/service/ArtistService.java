package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.ArtistDAO;
import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.Artist;

import java.util.List;

/**
 * Service for working with {@link Artist}.
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class ArtistService extends AbstractService<Artist> {
    private final ArtistDAO artistDAO;

    public ArtistService(ArtistDAO artistDAO) {
        super(artistDAO);
        this.artistDAO = artistDAO;
    }

    public List<Artist> findAllByName(String name) throws ServiceException {
        try {
            return artistDAO.findAllByName(name);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
