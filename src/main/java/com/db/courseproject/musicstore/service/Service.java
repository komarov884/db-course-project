package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.exception.ServiceException;

import java.util.List;

/**
 * Service for working with domain entities.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public interface Service<Domain> {
    /**
     * Saves the entity into database.
     *
     * @param entity domain entity.
     * @return saved entity.
     * @throws ServiceException
     */
    Domain create(Domain entity) throws ServiceException;

    /**
     * Finds the entity by id.
     *
     * @param id id of domain entity.
     * @return domain entity.
     * @throws ServiceException
     */
    Domain findById(Long id) throws ServiceException;

    /**
     * Updates the entity.
     *
     * @param entity domain entity.
     * @param id     id of domain entity.
     * @return updated entity.
     * @throws ServiceException
     */
    Domain update(Domain entity, Long id) throws ServiceException;

    /**
     * Deletes the entity by id.
     *
     * @param id id of domain entity.
     * @throws ServiceException
     */
    void delete(Long id) throws ServiceException;

    /**
     * Finds all entities.
     *
     * @return list of domain entities.
     * @throws ServiceException
     */
    List<Domain> findAll() throws ServiceException;
}
