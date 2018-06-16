package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.exception.ServiceException;

import java.util.List;

/**
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public interface Service<Domain> {
    /**
     *
     * @param entity
     * @throws ServiceException
     */
    Domain create(Domain entity) throws ServiceException;

    /**
     *
     * @param id
     * @return
     * @throws ServiceException
     */
    Domain findById(Long id) throws ServiceException;

    /**
     *
     * @param entity
     * @throws ServiceException
     */
    Domain update(Domain entity) throws ServiceException;

    /**
     *
     * @param id
     * @throws ServiceException
     */
    void delete(Long id) throws ServiceException;

    /**
     *
     * @return
     * @throws ServiceException
     */
    List<Domain> findAll() throws ServiceException;
}
