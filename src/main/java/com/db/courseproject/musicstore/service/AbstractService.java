package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.DAO;
import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ServiceException;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Abstract parent for services.
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public abstract class AbstractService<Domain> implements Service<Domain> {
    private final DAO<Domain> dao;

    @Override
    public Domain create(Domain entity) throws ServiceException {
        try {
            Long generatedId = dao.create(entity);
            return dao.findById(generatedId);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Domain findById(Long id) throws ServiceException {
        try {
            return dao.findById(id);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Domain update(Domain entity, Long id) throws ServiceException {
        try {
            dao.update(entity);
            return dao.findById(id);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void delete(Long id) throws ServiceException {
        try {
            dao.delete(id);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Domain> findAll() throws ServiceException {
        try {
            return dao.findAll();
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
