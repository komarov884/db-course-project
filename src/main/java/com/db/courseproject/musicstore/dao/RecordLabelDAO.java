package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.RecordLabel;
import com.db.courseproject.musicstore.util.DBConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_COUNTRY;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_FOUNDATION_YEAR;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_RECORD_LABEL_ID;

/**
 * Data access object for working with {@link RecordLabel}.
 * <p>
 * Created on 6/14/2018.
 *
 * @author Vasilii Komarov
 */
public class RecordLabelDAO implements DAO<RecordLabel> {
    private static final Logger LOGGER = Logger.getLogger(RecordLabelDAO.class);

    @Override
    public Long create(RecordLabel entity) throws DAOException {
        final String insertRecordLabelsSql =
                String.format("INSERT INTO %s.%s (%s, %s, %s) VALUES (?, ?, ?)",
                        SCHEMA, RECORD_LABELS_TABLE, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY,
                        RECORD_LABELS_FOUNDATION_YEAR);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertRecordLabelsStatement =
                     connection.prepareStatement(insertRecordLabelsSql, Statement.RETURN_GENERATED_KEYS)) {
            insertRecordLabelsStatement.setString(1, entity.getName());
            if (entity.getCountry() == null || entity.getCountry().isEmpty()) {
                insertRecordLabelsStatement.setNull(2, Types.VARCHAR);
            } else {
                insertRecordLabelsStatement.setString(2, entity.getCountry());
            }
            if (entity.getFoundationYear() == null || entity.getFoundationYear() == 0) {
                insertRecordLabelsStatement.setNull(3, Types.INTEGER);
            } else {
                insertRecordLabelsStatement.setInt(3, entity.getFoundationYear());
            }
            insertRecordLabelsStatement.executeUpdate();
            try (ResultSet resultSet = insertRecordLabelsStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                } else {
                    throw new DAOException("Error record label inserting");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public RecordLabel findById(Long id) throws DAOException {
        final String selectRecordLabelsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY, RECORD_LABELS_FOUNDATION_YEAR, SCHEMA,
                RECORD_LABELS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectRecordLabelsStatement = connection.prepareStatement(selectRecordLabelsSql)) {
            selectRecordLabelsStatement.setLong(1, id);
            try (ResultSet resultSet = selectRecordLabelsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseRecordLabel(resultSet);
                } else {
                    LOGGER.info(String.format("Record label with id = %d not found", id));
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
    public void update(RecordLabel entity) throws DAOException {
        final String updateRecordLabelsSql =
                String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                        SCHEMA, RECORD_LABELS_TABLE, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY,
                        RECORD_LABELS_FOUNDATION_YEAR, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateRecordLabelsStatement =
                     connection.prepareStatement(updateRecordLabelsSql)) {
            updateRecordLabelsStatement.setString(1, entity.getName());
            if (entity.getCountry() == null || entity.getCountry().isEmpty()) {
                updateRecordLabelsStatement.setNull(2, Types.VARCHAR);
            } else {
                updateRecordLabelsStatement.setString(2, entity.getCountry());
            }
            if (entity.getFoundationYear() == null || entity.getFoundationYear() == 0) {
                updateRecordLabelsStatement.setNull(3, Types.INTEGER);
            } else {
                updateRecordLabelsStatement.setInt(3, entity.getFoundationYear());
            }
            updateRecordLabelsStatement.setLong(4, entity.getId());
            updateRecordLabelsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        final String deleteRecordLabelsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, RECORD_LABELS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteRecordLabelsStatement = connection.prepareStatement(deleteRecordLabelsSql)) {
            checkForeignRelations(id, connection);
            deleteRecordLabelsStatement.setLong(1, id);
            deleteRecordLabelsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<RecordLabel> findAll() throws DAOException {
        final String selectRecordLabelsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s",
                ID, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY, RECORD_LABELS_FOUNDATION_YEAR, SCHEMA,
                RECORD_LABELS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectRecordLabelsStatement = connection.prepareStatement(selectRecordLabelsSql)) {
            try (ResultSet resultSet = selectRecordLabelsStatement.executeQuery()) {
                List<RecordLabel> recordLabels = new ArrayList<>();
                while (resultSet.next()) {
                    recordLabels.add(parseRecordLabel(resultSet));
                }
                return recordLabels;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<RecordLabel> findAllByName(String name) throws DAOException {
        final String selectRecordLabelsSql =
                String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ?",
                        ID, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY, RECORD_LABELS_FOUNDATION_YEAR,
                        SCHEMA, RECORD_LABELS_TABLE, RECORD_LABELS_NAME);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectRecordLabelsStatement = connection.prepareStatement(selectRecordLabelsSql)) {
            selectRecordLabelsStatement.setString(1, String.format("%%%s%%", name));
            try (ResultSet resultSet = selectRecordLabelsStatement.executeQuery()) {
                List<RecordLabel> recordLabels = new ArrayList<>();
                while (resultSet.next()) {
                    recordLabels.add(parseRecordLabel(resultSet));
                }
                return recordLabels;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private int findAlbumsCountByRecordLabelId(Long recordLabelId, Connection connection) {
        final String countAlias = "total";
        final String selectAlbumsSql = String.format("SELECT COUNT(*) AS %s FROM %s.%s WHERE %s = ?",
                countAlias, SCHEMA, ALBUMS_TABLE, ALBUMS_RECORD_LABEL_ID);
        try (PreparedStatement selectAlbumsStatement = connection.prepareStatement(selectAlbumsSql)) {
            selectAlbumsStatement.setLong(1, recordLabelId);
            try (ResultSet resultSet = selectAlbumsStatement.executeQuery()) {
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
        if (findAlbumsCountByRecordLabelId(id, connection) > 0) {
            throw new ForeignKeyViolationException(
                    String.format("The %s table has still contained records with %s = %s",
                            ALBUMS_TABLE, ALBUMS_RECORD_LABEL_ID, id)
            );
        }
    }

    private RecordLabel parseRecordLabel(ResultSet resultSet) throws SQLException {
        return new RecordLabel().setId(resultSet.getLong(ID))
                .setName(resultSet.getString(RECORD_LABELS_NAME))
                .setCountry(resultSet.getString(RECORD_LABELS_COUNTRY))
                .setFoundationYear(resultSet.getInt(RECORD_LABELS_FOUNDATION_YEAR));
    }
}
