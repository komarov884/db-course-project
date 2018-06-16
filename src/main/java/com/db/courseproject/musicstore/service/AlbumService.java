package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.AlbumDAO;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.Album;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public class AlbumService implements Service<Album> {
    private final AlbumDAO albumDAO;

    @Override
    public Album create(Album entity) throws ServiceException {
        Long generatedId = albumDAO.create(entity);
        return albumDAO.findById(generatedId);
    }

    @Override
    public Album findById(Long id) throws ServiceException {
        return albumDAO.findById(id);
    }

    @Override
    public Album update(Album entity) throws ServiceException {
        albumDAO.update(entity);
        return albumDAO.findById(entity.getId());
    }

    @Override
    public void delete(Long id) throws ServiceException {
        albumDAO.delete(id);
    }

    @Override
    public List<Album> findAll() throws ServiceException {
        return albumDAO.findAll();
    }

    public List<Album> findAllByTitle(String title) throws ServiceException {
        return albumDAO.findAllByTitle(title);
    }
}
