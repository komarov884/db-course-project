package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;

import java.util.List;

/**
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public interface DAO<Domain> {
    /**
     *
     * @param entity
     * @throws DAOException
     */
    Long create(Domain entity) throws DAOException;

    /**
     *
     * @param id
     * @return
     * @throws DAOException
     */
    Domain findById(Long id) throws DAOException;

    /**
     *
     * @param entity
     * @throws DAOException
     */
    void update(Domain entity) throws DAOException;

    /**
     *
     * @param id
     * @throws DAOException
     */
    void delete(Long id) throws DAOException;

    /**
     *
     * @return
     * @throws DAOException
     */
    List<Domain> findAll() throws DAOException;
}
