package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;

import java.util.List;

/**
 * Data access object for working with domain entities.
 * <p>
 * Created on 6/11/2018.
 *
 * @param <Domain> Class of domain entity.
 * @author Vasilii Komarov
 */
public interface DAO<Domain> {
    /**
     * Saves the entity into database.
     *
     * @param entity domain entity.
     * @return id of saved entity.
     * @throws DAOException
     */
    Long create(Domain entity) throws DAOException;

    /**
     * Finds the entity by id.
     *
     * @param id id of domain entity.
     * @return domain entity.
     * @throws DAOException
     */
    Domain findById(Long id) throws DAOException;

    /**
     * Updates the entity.
     *
     * @param entity domain entity.
     * @throws DAOException
     */
    void update(Domain entity) throws DAOException;

    /**
     * Deletes the entity by id.
     *
     * @param id id of domain entity.
     * @throws DAOException
     */
    void delete(Long id) throws DAOException;

    /**
     * Finds all entities.
     *
     * @return list of domain entities.
     * @throws DAOException
     */
    List<Domain> findAll() throws DAOException;
}
