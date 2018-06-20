package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.EntityNotFoundException;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.model.Producer;
import com.db.courseproject.musicstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUM_PRODUCER_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUM_PRODUCER_PRODUCER_ID;

/**
 * Data access object for working with {@link Producer}.
 * <p>
 * Created on 6/15/2018.
 *
 * @author Vasilii Komarov
 */
public class ProducerDAO implements DAO<Producer> {
    @Override
    public Long create(Producer entity) throws DAOException {
        final String insertProducersSql =
                String.format("INSERT INTO %s.%s (%s, %s, %s) VALUES (?, ?, ?)",
                        SCHEMA, PRODUCERS_TABLE, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME,
                        PRODUCERS_BIRTH_DATE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertProducersStatement =
                     connection.prepareStatement(insertProducersSql, Statement.RETURN_GENERATED_KEYS)) {
            insertProducersStatement.setString(1, entity.getName().getFirstName());
            insertProducersStatement.setString(2, entity.getName().getLastName());
            if (entity.getBirthDate() == null) {
                insertProducersStatement.setNull(3, Types.DATE);
            } else {
                insertProducersStatement.setObject(3, new Date(entity.getBirthDate().getTime()));
            }
            insertProducersStatement.executeUpdate();
            try (ResultSet resultSet = insertProducersStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                } else {
                    throw new DAOException("Error producer inserting");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Producer findById(Long id) throws DAOException {
        final String selectProducersSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME, PRODUCERS_BIRTH_DATE, SCHEMA, PRODUCERS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectProducersStatement = connection.prepareStatement(selectProducersSql)) {
            selectProducersStatement.setLong(1, id);
            try (ResultSet resultSet = selectProducersStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseProducer(resultSet);
                } else {
                    throw new EntityNotFoundException(String.format("Producer with id = %d not found", id));
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
        final String updateProducersSql =
                String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                        SCHEMA, PRODUCERS_TABLE, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME, PRODUCERS_BIRTH_DATE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateProducersStatement =
                     connection.prepareStatement(updateProducersSql)) {
            updateProducersStatement.setString(1, entity.getName().getFirstName());
            updateProducersStatement.setString(2, entity.getName().getLastName());
            if (entity.getBirthDate() == null) {
                updateProducersStatement.setNull(3, Types.DATE);
            } else {
                updateProducersStatement.setObject(3, new Date(entity.getBirthDate().getTime()));
            }
            updateProducersStatement.setLong(4, entity.getId());
            updateProducersStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        final String deleteProducersSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, PRODUCERS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteProducersStatement = connection.prepareStatement(deleteProducersSql)) {
            connection.setAutoCommit(false);
            checkForeignRelations(id, connection);
            deleteProducersStatement.setLong(1, id);
            deleteProducersStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Producer> findAll() throws DAOException {
        final String selectProducersSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s",
                ID, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME, PRODUCERS_BIRTH_DATE, SCHEMA, PRODUCERS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectProducersStatement = connection.prepareStatement(selectProducersSql)) {
            try (ResultSet resultSet = selectProducersStatement.executeQuery()) {
                List<Producer> producers = new ArrayList<>();
                while (resultSet.next()) {
                    producers.add(parseProducer(resultSet));
                }
                return producers;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Producer> findAllByName(String name) throws DAOException {
        final String selectProducersSql =
                String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ? OR %s LIKE ?",
                        ID, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME, PRODUCERS_BIRTH_DATE,
                        SCHEMA, PRODUCERS_TABLE, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectProducersStatement = connection.prepareStatement(selectProducersSql)) {
            selectProducersStatement.setString(1, String.format("%%%s%%", name));
            selectProducersStatement.setString(2, String.format("%%%s%%", name));
            try (ResultSet resultSet = selectProducersStatement.executeQuery()) {
                List<Producer> producers = new ArrayList<>();
                while (resultSet.next()) {
                    producers.add(parseProducer(resultSet));
                }
                return producers;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private int findAlbumsCountByProducerId(Long producerId, Connection connection) {
        final String countAlias = "total";
        final String selectAlbumProducerSql = String.format("SELECT COUNT(*) AS %s FROM %s.%s WHERE %s = ?",
                countAlias, SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_PRODUCER_ID);
        try (PreparedStatement selectAlbumProducerStatement =
                     connection.prepareStatement(selectAlbumProducerSql)) {
            selectAlbumProducerStatement.setLong(1, producerId);
            try (ResultSet resultSet = selectAlbumProducerStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(countAlias);
                } else {
                    throw new DAOException("Error getting count of albums");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private void checkForeignRelations(Long id, Connection connection) {
        if (findAlbumsCountByProducerId(id, connection) > 0) {
            throw new ForeignKeyViolationException(
                    String.format("The %s table has still contained records with %s = %s",
                            ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_PRODUCER_ID, id)
            );
        }
    }

    private Producer parseProducer(ResultSet resultSet) throws SQLException {
        return (Producer) new Producer().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(PRODUCERS_FIRST_NAME))
                        .setLastName(resultSet.getString(PRODUCERS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(PRODUCERS_BIRTH_DATE));
    }
}
