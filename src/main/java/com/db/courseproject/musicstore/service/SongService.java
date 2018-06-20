package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.SongDAO;
import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.Song;

import java.util.List;

/**
 * Service for working with {@link Song}.
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class SongService extends AbstractService<Song> {
    private final SongDAO songDAO;

    public SongService(SongDAO songDAO) {
        super(songDAO);
        this.songDAO = songDAO;
    }

    public List<Song> findAllByTitle(String title) throws ServiceException {
        try {
            return songDAO.findAllByTitle(title);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
