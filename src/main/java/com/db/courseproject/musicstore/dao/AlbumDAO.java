package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.Album;
import com.db.courseproject.musicstore.model.Artist;
import com.db.courseproject.musicstore.model.RecordLabel;
import com.db.courseproject.musicstore.model.Song;
import com.db.courseproject.musicstore.model.Producer;
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
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONGS_ALBUM_ID;

/**
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
@RequiredArgsConstructor
public class AlbumDAO implements DAO<Album> {
    private static final Logger LOGGER = Logger.getLogger(AlbumDAO.class);

    private final ArtistDAO artistDAO;
    private final RecordLabelDAO recordLabelDAO;
    private final SongDAO songDAO;
    private final ProducerDAO producerDAO;

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
        final String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE, ALBUMS_ARTIST_ID,
                ALBUMS_RECORD_LABEL_ID, SCHEMA, ALBUMS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parse(resultSet);
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
             PreparedStatement insertAlbumsStatement =
                     connection.prepareStatement(updateAlbumsSql, Statement.RETURN_GENERATED_KEYS)) {
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
            insertAlbumsStatement.setLong(7, entity.getId());
            insertAlbumsStatement.executeUpdate();

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
        final String deleteAlbumsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, ALBUMS_TABLE, ID);
        final String deleteAlbumProducerSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID);
        final String deleteSongsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID);
        final String deleteSongAuthorSql =
                String.format("DELETE FROM %s.%s WHERE %s IN (SELECT %s FROM %s.%s WHERE %s = ?)",
                        SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_SONG_ID, ID, SCHEMA, SONGS_TABLE, SONGS_ALBUM_ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement albumsStatement = connection.prepareStatement(deleteAlbumsSql);
             PreparedStatement albumProducerStatement = connection.prepareStatement(deleteAlbumProducerSql);
             PreparedStatement songsStatement = connection.prepareStatement(deleteSongsSql);
             PreparedStatement songAuthorStatement = connection.prepareStatement(deleteSongAuthorSql)) {
            songAuthorStatement.setLong(1, id);
            songAuthorStatement.executeUpdate();

            songsStatement.setLong(1, id);
            songsStatement.executeUpdate();

            albumProducerStatement.setLong(1, id);
            albumProducerStatement.executeUpdate();

            albumsStatement.setLong(1, id);
            albumsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Album> findAll() throws DAOException {
        final String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s.%s",
                ID, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE, ALBUMS_ARTIST_ID,
                ALBUMS_RECORD_LABEL_ID, SCHEMA, ALBUMS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Album> albums = new ArrayList<>();
                while (resultSet.next()) {
                    albums.add(parse(resultSet));
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
        final String sql = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ?",
                ID, ALBUMS_TITLE, ALBUMS_ISSUE_YEAR, ALBUMS_PRICE, ALBUMS_GENRE, ALBUMS_ARTIST_ID,
                ALBUMS_RECORD_LABEL_ID, SCHEMA, ALBUMS_TABLE, ALBUMS_TITLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.format("%%%s%%", title));
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Album> albums = new ArrayList<>();
                while (resultSet.next()) {
                    albums.add(parse(resultSet));
                }
                return albums;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private Album parse(ResultSet resultSet) throws SQLException {
        Album album = new Album()
                .setId(resultSet.getLong(ID))
                .setTitle(resultSet.getString(ALBUMS_TITLE))
                .setIssueYear(resultSet.getInt(ALBUMS_ISSUE_YEAR))
                .setPrice(resultSet.getInt(ALBUMS_PRICE))
                .setGenre(resultSet.getString(ALBUMS_GENRE));

        Long artistId = resultSet.getLong(ALBUMS_ARTIST_ID);
        Long recordLabelId = resultSet.getLong(ALBUMS_RECORD_LABEL_ID);
        List<Long> producerIds = findProducerIdsByAlbumId(album.getId());

        Artist artist = artistDAO.findById(artistId);
        album.setArtist(artist);

        if (!recordLabelId.equals(0L)) {
            RecordLabel recordLabel = recordLabelDAO.findById(recordLabelId);
            album.setRecordLabel(recordLabel);
        }

        List<Song> songs = songDAO.findAllByAlbumId(album.getId());
        album.setSongs(songs);

        if (producerIds != null && !producerIds.isEmpty()) {
            List<Producer> producers = producerDAO.findAllByIds(producerIds);
            album.setProducers(producers);
        }

        return album;
    }

    private List<Long> findProducerIdsByAlbumId(long albumId) throws SQLException {
        final String sql = String.format("SELECT %s FROM %s.%s WHERE %s = ?",
                ALBUM_PRODUCER_PRODUCER_ID, SCHEMA, ALBUM_PRODUCER_TABLE, ALBUM_PRODUCER_ALBUM_ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, albumId);
            try (ResultSet resultSet = statement.executeQuery()) {
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
}
