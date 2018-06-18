package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Album;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.RecordLabel;
import com.db.courseproject.musicstore.model.Song;
import com.db.courseproject.musicstore.model.Producer;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.AuthorType;
import com.db.courseproject.musicstore.util.DBConnection;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUM_PRODUCER_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUM_PRODUCER_ALBUM_ID;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUM_PRODUCER_PRODUCER_ID;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_TITLE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_ISSUE_YEAR;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_PRICE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_GENRE;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_ARTIST_ID;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_RECORD_LABEL_ID;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_SONG_ID;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_AUTHOR_ID;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_ALBUM_ID;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_ORDER_NUMBER;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_TITLE;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.ARTISTS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_COUNTRY;
import static com.db.courseproject.musicstore.util.DBConstants.RECORD_LABELS_FOUNDATION_YEAR;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_AUTHOR_TYPE;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.PRODUCERS_BIRTH_DATE;

/**
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public class AlbumDAO implements DAO<Album> {
    private static final Logger LOGGER = Logger.getLogger(AlbumDAO.class);

    @Override
    public Long create(Album entity) throws DAOException {
        final String insertAlbumsSql =
                String.format("INSERT INTO %s.%s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                        SCHEMA, ALBUMS_TABLE, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE,
                        ALBUMS_ARTIST_ID, ALBUMS_RECORD_LABEL_ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertAlbumsStatement =
                     connection.prepareStatement(insertAlbumsSql, Statement.RETURN_GENERATED_KEYS)) {
            insertAlbumsStatement.setString(1, entity.getTitle());
            if (entity.getIssueYear() == null) {
                insertAlbumsStatement.setNull(2, Types.INTEGER);
            } else {
                insertAlbumsStatement.setInt(2, entity.getIssueYear());
            }
            insertAlbumsStatement.setInt(3, entity.getPrice());
            if (entity.getGenre() == null) {
                insertAlbumsStatement.setNull(4, Types.VARCHAR);
            } else {
                insertAlbumsStatement.setString(4, entity.getGenre());
            }
            insertAlbumsStatement.setLong(5, entity.getArtist().getId());
            if (entity.getRecordLabel() == null) {
                insertAlbumsStatement.setNull(6, Types.BIGINT);
            } else {
                insertAlbumsStatement.setLong(6, entity.getRecordLabel().getId());
            }
            insertAlbumsStatement.executeUpdate();
            try (ResultSet resultSet = insertAlbumsStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong(1);
                    if (entity.getProducers() != null && !entity.getProducers().isEmpty()) {
                        final String insertAlbumProducerSql = String.format("INSERT INTO %s.%s (%s, %s) VALUES (?, ?)",
                                SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID, ALBUM_PRODUCER_PRODUCER_ID);
                        try (PreparedStatement insertAlbumProducerStatement =
                                     connection.prepareStatement(insertAlbumProducerSql)) {
                            for (Producer producer : entity.getProducers()) {
                                insertAlbumProducerStatement.setLong(1, generatedId);
                                insertAlbumProducerStatement.setLong(2, producer.getId());
                                insertAlbumProducerStatement.executeUpdate();
                            }
                        } catch (SQLException e) {
                            throw new DAOException(e);
                        }
                    }
                    return generatedId;
                } else {
                    throw new DAOException("Error album inserting");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Album findById(Long id) throws DAOException {
        final String selectAlbumsSql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE, ALBUMS_ARTIST_ID,
                ALBUMS_RECORD_LABEL_ID, SCHEMA, ALBUMS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAlbumsStatement = connection.prepareStatement(selectAlbumsSql)) {
            selectAlbumsStatement.setLong(1, id);
            try (ResultSet resultSet = selectAlbumsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseAlbum(resultSet, connection);
                } else {
                    LOGGER.info(String.format("Album with id = %d not found", id));
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
    public void update(Album entity) throws DAOException {
        final String updateAlbumsSql =
                String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                        SCHEMA, ALBUMS_TABLE, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE,
                        ALBUMS_ARTIST_ID, ALBUMS_RECORD_LABEL_ID, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateAlbumsStatement =
                     connection.prepareStatement(updateAlbumsSql)) {
            updateAlbumsStatement.setString(1, entity.getTitle());
            if (entity.getIssueYear() == null) {
                updateAlbumsStatement.setNull(2, Types.INTEGER);
            } else {
                updateAlbumsStatement.setInt(2, entity.getIssueYear());
            }
            updateAlbumsStatement.setInt(3, entity.getPrice());
            if (entity.getGenre() == null) {
                updateAlbumsStatement.setNull(4, Types.VARCHAR);
            } else {
                updateAlbumsStatement.setString(4, entity.getGenre());
            }
            updateAlbumsStatement.setLong(5, entity.getArtist().getId());
            if (entity.getRecordLabel() == null) {
                updateAlbumsStatement.setNull(6, Types.BIGINT);
            } else {
                updateAlbumsStatement.setLong(6, entity.getRecordLabel().getId());
            }
            updateAlbumsStatement.setLong(7, entity.getId());
            updateAlbumsStatement.executeUpdate();

            final String deleteAlbumProducerSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                    SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID);
            try (PreparedStatement deleteAlbumProducerStatement = connection.prepareStatement(deleteAlbumProducerSql)) {
                deleteAlbumProducerStatement.setLong(1, entity.getId());
                deleteAlbumProducerStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException(e);
            }

            if (entity.getProducers() != null && !entity.getProducers().isEmpty()) {
                final String insertAlbumProducerSql = String.format("INSERT INTO %s.%s (%s, %s) VALUES (?, ?)",
                        SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID, ALBUM_PRODUCER_PRODUCER_ID);
                try (PreparedStatement insertAlbumProducerStatement =
                             connection.prepareStatement(insertAlbumProducerSql)) {
                    for (Producer producer : entity.getProducers()) {
                        insertAlbumProducerStatement.setLong(1, entity.getId());
                        insertAlbumProducerStatement.setLong(2, producer.getId());
                        insertAlbumProducerStatement.executeUpdate();
                    }
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
        final String deleteAlbumProducerSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID);
        final String deleteAlbumsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, ALBUMS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteAlbumProducerStatement = connection.prepareStatement(deleteAlbumProducerSql);
             PreparedStatement deleteAlbumsStatement = connection.prepareStatement(deleteAlbumsSql)) {
            checkForeignRelations(id, connection);
            deleteAlbumProducerStatement.setLong(1, id);
            deleteAlbumProducerStatement.executeUpdate();
            deleteAlbumsStatement.setLong(1, id);
            deleteAlbumsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Album> findAll() throws DAOException {
        final String selectAlbumsSql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s.%s",
                ID, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE, ALBUMS_ARTIST_ID,
                ALBUMS_RECORD_LABEL_ID, SCHEMA, ALBUMS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAlbumsStatement = connection.prepareStatement(selectAlbumsSql)) {
            try (ResultSet resultSet = selectAlbumsStatement.executeQuery()) {
                List<Album> albums = new ArrayList<>();
                while (resultSet.next()) {
                    albums.add(parseAlbum(resultSet, connection));
                }
                return albums;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<Album> findAllByTitle(String title) throws DAOException {
        final String selectAlbumsSql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ?",
                ID, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE, ALBUMS_ARTIST_ID,
                ALBUMS_RECORD_LABEL_ID, SCHEMA, ALBUMS_TABLE, ALBUMS_TITLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAlbumsStatement = connection.prepareStatement(selectAlbumsSql)) {
            selectAlbumsStatement.setString(1, String.format("%%%s%%", title));
            try (ResultSet resultSet = selectAlbumsStatement.executeQuery()) {
                List<Album> albums = new ArrayList<>();
                while (resultSet.next()) {
                    albums.add(parseAlbum(resultSet, connection));
                }
                return albums;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private List<Long> findProducerIdsByAlbumId(long albumId) throws SQLException {
        final String selectAlbumProducerSql = String.format("SELECT %s FROM %s.%s WHERE %s = ?",
                ALBUM_PRODUCER_PRODUCER_ID, SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAlbumProducerStatement = connection.prepareStatement(selectAlbumProducerSql)) {
            selectAlbumProducerStatement.setLong(1, albumId);
            try (ResultSet resultSet = selectAlbumProducerStatement.executeQuery()) {
                List<Long> producerIds = new ArrayList<>();
                while (resultSet.next()) {
                    producerIds.add(resultSet.getLong(ALBUM_PRODUCER_PRODUCER_ID));
                }
                return producerIds;
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private Artist findArtistById(Long artistId, Connection connection) {
        final String selectArtistsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, ARTISTS_FIRST_NAME, ARTISTS_LAST_NAME, ARTISTS_BIRTH_DATE, SCHEMA, ARTISTS_TABLE, ID);
        try (PreparedStatement selectArtistsStatement = connection.prepareStatement(selectArtistsSql)) {
            selectArtistsStatement.setLong(1, artistId);
            try (ResultSet resultSet = selectArtistsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseArtist(resultSet);
                } else {
                    LOGGER.info(String.format("Artist with id = %d not found", artistId));
                    return null;
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private List<Song> findAllSongsByAlbumId(Long albumId, Connection connection) {
        if (albumId == 0) {
            return Collections.EMPTY_LIST;
        }
        final String selectSongsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, SONGS_ALBUM_ID, SONGS_ORDER_NUMBER, SONGS_TITLE,
                SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID);
        try (PreparedStatement selectSongsStatement = connection.prepareStatement(selectSongsSql)) {
            selectSongsStatement.setLong(1, albumId);
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

    private RecordLabel findRecordLabelById(Long recordLabelId, Connection connection) {
        final String selectRecordLabelsSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, RECORD_LABELS_NAME, RECORD_LABELS_COUNTRY, RECORD_LABELS_FOUNDATION_YEAR, SCHEMA,
                RECORD_LABELS_TABLE, ID);
        try (PreparedStatement selectRecordLabelsStatement = connection.prepareStatement(selectRecordLabelsSql)) {
            selectRecordLabelsStatement.setLong(1, recordLabelId);
            try (ResultSet resultSet = selectRecordLabelsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseRecordLabel(resultSet);
                } else {
                    LOGGER.info(String.format("Record label with id = %d not found", recordLabelId));
                    return null;
                }
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

    private List<Producer> findAllProducersByIds(List<Long> producerIds, Connection connection) {
        if (producerIds == null || producerIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        final String condition = buildInCondition(producerIds);
        final String selectProducersSql = String.format("SELECT %s, %s, %s, %s FROM %s.%s WHERE %s IN %s",
                ID, PRODUCERS_FIRST_NAME, PRODUCERS_LAST_NAME, PRODUCERS_BIRTH_DATE,
                SCHEMA, PRODUCERS_TABLE, ID, condition);
        try (PreparedStatement selectProducersStatement = connection.prepareStatement(selectProducersSql)) {
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

    private int findSongsCountByAlbumId(Long albumId, Connection connection) {
        final String countAlias = "total";
        final String selectSongsSql = String.format("SELECT COUNT(*) AS %s FROM %s.%s WHERE %s = ?",
                countAlias, SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID);
        try (PreparedStatement selectSongsStatement = connection.prepareStatement(selectSongsSql)) {
            selectSongsStatement.setLong(1, albumId);
            try (ResultSet resultSet = selectSongsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(countAlias);
                } else {
                    throw new DAOException("Error getting count of songs");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private void checkForeignRelations(Long id, Connection connection) {
        if (findSongsCountByAlbumId(id, connection) > 0) {
            throw new ForeignKeyViolationException(
                    String.format("The %s table has still contained records with %s = %s",
                            SONGS_TABLE, SONGS_ALBUM_ID, id)
            );
        }
    }

    private Album parseAlbum(ResultSet resultSet, Connection connection) throws SQLException {
        Album album = new Album()
                .setId(resultSet.getLong(ID))
                .setTitle(resultSet.getString(ALBUMS_TITLE))
                .setIssueYear(resultSet.getInt(ALBUMS_ISSUE_YEAR))
                .setPrice(resultSet.getInt(ALBUMS_PRICE))
                .setGenre(resultSet.getString(ALBUMS_GENRE));

        Long artistId = resultSet.getLong(ALBUMS_ARTIST_ID);
        Long recordLabelId = resultSet.getLong(ALBUMS_RECORD_LABEL_ID);
        List<Long> producerIds = findProducerIdsByAlbumId(album.getId());

        Artist artist = findArtistById(artistId, connection);
        album.setArtist(artist);

        if (!recordLabelId.equals(0L)) {
            RecordLabel recordLabel = findRecordLabelById(recordLabelId, connection);
            album.setRecordLabel(recordLabel);
        }

        List<Song> songs = findAllSongsByAlbumId(album.getId(), connection);
        album.setSongs(songs);

        if (producerIds != null && !producerIds.isEmpty()) {
            List<Producer> producers = findAllProducersByIds(producerIds, connection);
            album.setProducers(producers);
        }

        return album;
    }

    private Artist parseArtist(ResultSet resultSet) throws SQLException {
        return (Artist) new Artist().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(ARTISTS_FIRST_NAME))
                        .setLastName(resultSet.getString(ARTISTS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(ARTISTS_BIRTH_DATE));
    }

    private RecordLabel parseRecordLabel(ResultSet resultSet) throws SQLException {
        return new RecordLabel().setId(resultSet.getLong(ID))
                .setName(resultSet.getString(RECORD_LABELS_NAME))
                .setCountry(resultSet.getString(RECORD_LABELS_COUNTRY))
                .setFoundationYear(resultSet.getInt(RECORD_LABELS_FOUNDATION_YEAR));
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

    private Producer parseProducer(ResultSet resultSet) throws SQLException {
        return (Producer) new Producer().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(PRODUCERS_FIRST_NAME))
                        .setLastName(resultSet.getString(PRODUCERS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(PRODUCERS_BIRTH_DATE));
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
