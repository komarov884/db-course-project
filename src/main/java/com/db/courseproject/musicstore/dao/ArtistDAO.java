package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.util.DBConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_ARTIST_ID;

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
        final String insertArtistsSql =
                String.format("INSERT INTO %s.%s (%s, %s, %s) VALUES (?, ?, ?)",
                        SCHEMA, ARTISTS_TABLE, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertArtistStatement =
                     connection.prepareStatement(insertArtistsSql, Statement.RETURN_GENERATED_KEYS)) {
            insertArtistStatement.setString(1, entity.getName().getFirstName());
            insertArtistStatement.setString(2, entity.getName().getLastName());
            if (entity.getBirthDate() == null) {
                insertArtistStatement.setNull(3, Types.DATE);
            } else {
                insertArtistStatement.setObject(3, new Date(entity.getBirthDate().getTime()));
            }
            insertArtistStatement.executeUpdate();
            try (ResultSet resultSet = insertArtistStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                } else {
                    throw new DAOException("Error artist inserting");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Artist findById(Long id) throws DAOException {
        final String selectArtistsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE, SCHEMA, ARTISTS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectArtistsStatement = connection.prepareStatement(selectArtistsSql)) {
            selectArtistsStatement.setLong(1, id);
            try (ResultSet resultSet = selectArtistsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseArtist(resultSet);
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
        final String updateArtistSql =
                String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                        SCHEMA, ARTISTS_TABLE, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateArtistsStatement =
                     connection.prepareStatement(updateArtistSql)) {
            updateArtistsStatement.setString(1, entity.getName().getFirstName());
            updateArtistsStatement.setString(2, entity.getName().getLastName());
            if (entity.getBirthDate() == null) {
                updateArtistsStatement.setNull(3, Types.DATE);
            } else {
                updateArtistsStatement.setObject(3, new Date(entity.getBirthDate().getTime()));
            }
            updateArtistsStatement.setLong(4, entity.getId());
            updateArtistsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        final String deleteArtistsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, ARTISTS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteArtistsStatement = connection.prepareStatement(deleteArtistsSql)) {
            checkForeignRelations(id, connection);
            deleteArtistsStatement.setLong(1, id);
            deleteArtistsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Artist> findAll() throws DAOException {
        final String selectArtistSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s",
                ID, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE, SCHEMA, ARTISTS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectArtistsStatement = connection.prepareStatement(selectArtistSql)) {
            try (ResultSet resultSet = selectArtistsStatement.executeQuery()) {
                List<Artist> artists = new ArrayList<>();
                while (resultSet.next()) {
                    artists.add(parseArtist(resultSet));
                }
                return artists;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Artist> findAllByName(String name) throws DAOException {
        final String selectArtistsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ? OR %s LIKE ?",
                ID, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE, SCHEMA,
                ARTISTS_TABLE, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectArtistsStatement = connection.prepareStatement(selectArtistsSql)) {
            selectArtistsStatement.setString(1, String.format("%%%s%%", name));
            selectArtistsStatement.setString(2, String.format("%%%s%%", name));
            try (ResultSet resultSet = selectArtistsStatement.executeQuery()) {
                List<Artist> artists = new ArrayList<>();
                while (resultSet.next()) {
                    artists.add(parseArtist(resultSet));
                }
                return artists;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private int findAlbumsCountByArtistId(Long artistId, Connection connection) {
        final String countAlias = "total";
        final String selectAlbumsSql = String.format("SELECT COUNT(*) AS %s FROM %s.%s WHERE %s = ?",
                countAlias, SCHEMA, ALBUMS_TABLE, ALBUMS_ARTIST_ID);
        try (PreparedStatement selectAlbumsStatement = connection.prepareStatement(selectAlbumsSql)) {
            selectAlbumsStatement.setLong(1, artistId);
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
        if (findAlbumsCountByArtistId(id, connection) > 0) {
            throw new ForeignKeyViolationException(
                    String.format("The %s table has still contained records with %s = %s",
                            ALBUMS_TABLE, ALBUMS_ARTIST_ID, id)
            );
        }
    }

    private Artist parseArtist(ResultSet resultSet) throws SQLException {
        return (Artist) new Artist().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(ARTISTS_FIRST_NAME))
                        .setLastName(resultSet.getString(ARTISTS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(ARTISTS_BIRTH_DATE));
    }
}
