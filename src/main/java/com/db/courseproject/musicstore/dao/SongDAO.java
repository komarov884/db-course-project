package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.EntityNotFoundException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.AuthorType;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.model.Song;
import com.db.courseproject.musicstore.model.ExtendedSong;
import com.db.courseproject.musicstore.util.DBConnection;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * Data access object for working with {@link Song}.
 * <p>
 * Created on 6/15/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public class SongDAO implements DAO<Song> {
    @Override
    public Long create(Song entity) throws DAOException {
        ExtendedSong extendedSong = (ExtendedSong) entity;
        final String insertSongsSql = String.format("INSERT INTO %s.%s (%s, %s, %s) VALUES (?, ?, ?)",
                        SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertSongsStatement =
                     connection.prepareStatement(insertSongsSql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            insertSongsStatement.setLong(1, extendedSong.getAlbumId());
            insertSongsStatement.setInt(2, extendedSong.getOrderNumber());
            insertSongsStatement.setString(3, extendedSong.getTitle());
            insertSongsStatement.executeUpdate();
            try (ResultSet resultSet = insertSongsStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong(1);
                    if (extendedSong.getAuthors() != null && !extendedSong.getAuthors().isEmpty()) {
                        final String insertSongAuthorSql = String.format("INSERT INTO %s.%s (%s, %s) VALUES (?, ?)",
                                SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID, SONG_AUTHOR_AUTHOR_ID);
                        try (PreparedStatement insertSongAuthorStatement =
                                     connection.prepareStatement(insertSongAuthorSql)) {
                            for (Author author : extendedSong.getAuthors()) {
                                insertSongAuthorStatement.setLong(1, generatedId);
                                insertSongAuthorStatement.setLong(2, author.getId());
                                insertSongAuthorStatement.executeUpdate();
                            }
                            connection.commit();
                        } catch (SQLException e) {
                            throw new DAOException(e);
                        }
                    }
                    return generatedId;
                } else {
                    throw new DAOException("Error song inserting");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Song findById(Long id) throws DAOException {
        final String selectSongsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE, SCHEMA, SONGS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectSongsStatement = connection.prepareStatement(selectSongsSql)) {
            selectSongsStatement.setLong(1, id);
            try (ResultSet resultSet = selectSongsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseSong(resultSet, connection);
                } else {
                    throw new EntityNotFoundException(String.format("Song with id = %d not found", id));
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
        ExtendedSong extendedSong = (ExtendedSong) entity;
        final String updateSongsSql = String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                        SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateSongsStatement = connection.prepareStatement(updateSongsSql)) {
            connection.setAutoCommit(false);
            updateSongsStatement.setLong(1, extendedSong.getAlbumId());
            updateSongsStatement.setInt(2, extendedSong.getOrderNumber());
            updateSongsStatement.setString(3, extendedSong.getTitle());
            updateSongsStatement.setLong(4, extendedSong.getId());
            updateSongsStatement.executeUpdate();

            final String deleteSongAuthorSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                    SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID);
            try (PreparedStatement deleteSongAuthorStatement = connection.prepareStatement(deleteSongAuthorSql)) {
                deleteSongAuthorStatement.setLong(1, extendedSong.getId());
                deleteSongAuthorStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }

            if (extendedSong.getAuthors() != null && !extendedSong.getAuthors().isEmpty()) {
                final String insertSongAuthorSql = String.format("INSERT INTO %s.%s (%s, %s) VALUES (?, ?)",
                        SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID, SONG_AUTHOR_AUTHOR_ID);
                try (PreparedStatement insertSongAuthorStatement =
                             connection.prepareStatement(insertSongAuthorSql)) {
                    for (Author author : extendedSong.getAuthors()) {
                        insertSongAuthorStatement.setLong(1, extendedSong.getId());
                        insertSongAuthorStatement.setLong(2, author.getId());
                        insertSongAuthorStatement.executeUpdate();
                    }
                    connection.commit();
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        final String deleteSongAuthorSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID);
        final String deleteSongsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, SONGS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteSongAuthorStatement = connection.prepareStatement(deleteSongAuthorSql);
             PreparedStatement deleteSongsStatement = connection.prepareStatement(deleteSongsSql)) {
            connection.setAutoCommit(false);
            deleteSongAuthorStatement.setLong(1, id);
            deleteSongAuthorStatement.executeUpdate();
            deleteSongsStatement.setLong(1, id);
            deleteSongsStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Song> findAll() throws DAOException {
        final String selectSongsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s",
                ID, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE, SCHEMA, SONGS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectSongsStatement = connection.prepareStatement(selectSongsSql)) {
            try (ResultSet resultSet = selectSongsStatement.executeQuery()) {
                List<Song> songs = new ArrayList<>();
                while (resultSet.next()) {
                    songs.add(parseSong(resultSet, connection));
                }
                return songs;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Song> findAllByTitle(String title) throws DAOException {
        final String selectSongsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ?",
                        ID, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE,
                        SCHEMA, SONGS_TABLE, SONGS_TITLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectSongsStatement = connection.prepareStatement(selectSongsSql)) {
            selectSongsStatement.setString(1, String.format("%%%s%%", title));
            try (ResultSet resultSet = selectSongsStatement.executeQuery()) {
                List<Song> songs = new ArrayList<>();
                while (resultSet.next()) {
                    songs.add(parseSong(resultSet, connection));
                }
                return songs;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private List<Long> findAuthorIdsBySongId(Long songId, Connection connection) throws SQLException {
        final String selectSongAuthorSql = String.format("SELECT %s FROM %s.%s WHERE %s = ?",
                SONG_AUTHOR_AUTHOR_ID, SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID);
        try (PreparedStatement selectSongAuthorStatement = connection.prepareStatement(selectSongAuthorSql)) {
            selectSongAuthorStatement.setLong(1, songId);
            try (ResultSet resultSet = selectSongAuthorStatement.executeQuery()) {
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
        final String selectAuthorsSql = String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s WHERE %s IN %s",
                ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                SCHEMA, AUTHORS_TABLE, ID, condition);
        try (PreparedStatement selectAuthorsStatement = connection.prepareStatement(selectAuthorsSql)) {
            try (ResultSet resultSet = selectAuthorsStatement.executeQuery()) {
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
