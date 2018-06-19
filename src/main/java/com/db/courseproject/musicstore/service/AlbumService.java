package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.AlbumDAO;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.Album;

import java.util.List;

/**
 * Service for working with {@link Album}.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class AlbumService extends AbstractService<Album> {
    private final AlbumDAO albumDAO;

    public AlbumService(AlbumDAO albumDAO) {
        super(albumDAO);
        this.albumDAO = albumDAO;
    }

    public List<Album> findAllByTitle(String title) throws ServiceException {
        return albumDAO.findAllByTitle(title);
    }
}
