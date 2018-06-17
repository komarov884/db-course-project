package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.model.Producer;
import com.db.courseproject.musicstore.util.DBConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_BIRTH_DATE;

/**
 * <p>
 * Created on 6/15/2018.
 *
 * @author Vasilii Komarov
 */
public class ProducerDAO implements DAO<Producer> {
    private static final Logger LOGGER = Logger.getLogger(ProducerDAO.class);

    @Override
    public Long create(Producer entity) throws DAOException {
        return 0L;
    }

    @Override
    public Producer findById(Long id) throws DAOException {
        final String sql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME, PRODUCERS_BIRTH_DATE, SCHEMA, PRODUCERS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parse(resultSet);
                } else {
                    LOGGER.info(String.format("Producer with id = %d not found", id));
                    return null;
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void update(Producer entity) throws DAOException {

    }

    @Override
    public void delete(Long id) throws DAOException {

    }

    @Override
    public List<Producer> findAll() throws DAOException {
        return null;
    }

    private Producer parse(ResultSet resultSet) throws SQLException {
        return (Producer) new Producer().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(PRODUCERS_FIRST_NAME))
                        .setLastName(resultSet.getString(PRODUCERS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(PRODUCERS_BIRTH_DATE));
    }
}
