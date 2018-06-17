package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.AuthorType;
import com.db.courseproject.musicstore.model.FullName;
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
import java.util.Iterator;
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
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_AUTHOR_TYPE;

/**
 * <p>
 * Created on 6/15/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public class SongDAO implements DAO<Song> {
    private static final Logger LOGGER = Logger.getLogger(SongDAO.class);

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
                    return parseSong(resultSet, connection);
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

    private List<Long> findAuthorIdsBySongId(Long songId, Connection connection) throws SQLException {
        final String sql = String.format("SELECT %s FROM %s.%s WHERE %s = ?",
                SONG_AUTHOR_AUTHOR_ID, SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    private List<Author> findAllAuthorsByIds(List<Long> authorIds, Connection connection) {
        if (authorIds == null || authorIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        final String condition = buildInCondition(authorIds);
        final String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s WHERE %s IN %s",
                ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                SCHEMA, AUTHORS_TABLE, ID, condition);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Author> authors = new ArrayList<>();
                while (resultSet.next()) {
                    authors.add(parseAuthor(resultSet));
                }
                return authors;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private Song parseSong(ResultSet resultSet, Connection connection) throws SQLException {
        Song song = new Song().setId(resultSet.getLong(ID))
                .setOrderNumber(resultSet.getInt(SONGS_ORDER_NUMBER))
                .setTitle(resultSet.getString(SONGS_TITLE));
        List<Long> authorIds = findAuthorIdsBySongId(song.getId(), connection);
        List<Author> authors = findAllAuthorsByIds(authorIds, connection);
        song.setAuthors(authors);
        return song;
    }

    private Author parseAuthor(ResultSet resultSet) throws SQLException {
        Author author = (Author) new Author().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(AUTHORS_FIRST_NAME))
                        .setLastName(resultSet.getString(AUTHORS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(AUTHORS_BIRTH_DATE));
        author.setAuthorType(AuthorType.valueOf(resultSet.getString(AUTHORS_AUTHOR_TYPE)));
        return author;
    }

    private String buildInCondition(List<Long> ids) {
        StringBuilder builder = new StringBuilder("(");
        Iterator<Long> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
