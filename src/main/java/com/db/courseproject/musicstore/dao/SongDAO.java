package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.Song;
import com.db.courseproject.musicstore.util.DBConnection;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_ALBUM_ID;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_ORDER_NUMBER;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_TITLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_SONG_ID;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_AUTHOR_ID;

/**
 * <p>
 * Created on 6/15/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public class SongDAO implements DAO<Song> {
    private static final Logger LOGGER = Logger.getLogger(SongDAO.class);

    private final AuthorDAO authorDAO;

    @Override
    public Long create(Song entity) throws DAOException {
        return 0L;
    }

    @Override
    public Song findById(Long id) throws DAOException {
        final String sql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE, SCHEMA, SONGS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parse(resultSet);
                } else {
                    LOGGER.info(String.format("Song with id = %d not found", id));
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
    public void update(Song entity) throws DAOException {

    }

    @Override
    public void delete(Long id) throws DAOException {

    }

    @Override
    public List<Song> findAll() throws DAOException {
        return null;
    }

    public List<Song> findAllByAlbumId(Long albumId) throws DAOException {
        if (albumId == 0) {
            return Collections.EMPTY_LIST;
        }
        final String sql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE,
                SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, albumId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Song> songs = new ArrayList<>();
                while (resultSet.next()) {
                    songs.add(parse(resultSet));
                }
                return songs;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private Song parse(ResultSet resultSet) throws SQLException {
        Song song = new Song().setId(resultSet.getLong(ID))
                .setOrderNumber(resultSet.getInt(SONGS_ORDER_NUMBER))
                .setTitle(resultSet.getString(SONGS_TITLE));
        List<Long> authorIds = findAuthorIdsBySongId(song.getId());
        List<Author> authors = authorDAO.findAllByIds(authorIds);
        song.setAuthors(authors);
        return song;
    }

    private List<Long> findAuthorIdsBySongId(Long songId) throws SQLException {
        final String sql = String.format("SELECT %s FROM %s.%s WHERE %s = ?",
                SONG_AUTHOR_AUTHOR_ID, SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, songId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Long> authorIds = new ArrayList<>();
                while (resultSet.next()) {
                    authorIds.add(resultSet.getLong(SONG_AUTHOR_AUTHOR_ID));
                }
                return authorIds;
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
