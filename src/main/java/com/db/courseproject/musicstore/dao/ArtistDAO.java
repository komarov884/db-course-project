package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.util.DBConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_BIRTH_DATE;

/**
 * <p>
 * Created on 6/14/2018.
 *
 * @author Vasilii Komarov
 */
public class ArtistDAO implements DAO<Artist> {
    private static final Logger LOGGER = Logger.getLogger(ArtistDAO.class);

    @Override
    public Long create(Artist entity) throws DAOException {
        return 0L;
    }

    @Override
    public Artist findById(Long id) throws DAOException {
        final String sql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE, SCHEMA, ARTISTS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parse(resultSet);
                } else {
                    LOGGER.info(String.format("Artist with id = %d not found", id));
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
    public void update(Artist entity) throws DAOException {

    }

    @Override
    public void delete(Long id) throws DAOException {

    }

    @Override
    public List<Artist> findAll() throws DAOException {
        return null;
    }

    private Artist parse(ResultSet resultSet) throws SQLException {
        return (Artist) new Artist().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(ARTISTS_FIRST_NAME))
                        .setLastName(resultSet.getString(ARTISTS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(ARTISTS_BIRTH_DATE));
    }
}
