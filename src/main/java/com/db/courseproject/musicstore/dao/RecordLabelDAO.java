package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.RecordLabel;
import com.db.courseproject.musicstore.util.DBConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_COUNTRY;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_FOUNDATION_YEAR;

/**
 * <p>
 * Created on 6/14/2018.
 *
 * @author Vasilii Komarov
 */
public class RecordLabelDAO implements DAO<RecordLabel> {
    private static final Logger LOGGER = Logger.getLogger(RecordLabelDAO.class);

    @Override
    public Long create(RecordLabel entity) throws DAOException {
        return 0L;
    }

    @Override
    public RecordLabel findById(Long id) throws DAOException {
        final String sql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY, RECORD_LABELS_FOUNDATION_YEAR, SCHEMA,
                RECORD_LABELS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parse(resultSet);
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

    }

    @Override
    public void delete(Long id) throws DAOException {

    }

    @Override
    public List<RecordLabel> findAll() throws DAOException {
        return null;
    }

    private RecordLabel parse(ResultSet resultSet) throws SQLException {
        return new RecordLabel().setId(resultSet.getLong(ID))
                .setName(resultSet.getString(RECORD_LABELS_NAME))
                .setCountry(resultSet.getString(RECORD_LABELS_COUNTRY))
                .setFoundationYear(resultSet.getInt(RECORD_LABELS_FOUNDATION_YEAR));
    }
}
