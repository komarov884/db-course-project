package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.AuthorType;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.util.DBConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ID;
import static com.db.courseproject.musicstore.util.DBConstants.SCHEMA;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_AUTHOR_TYPE;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.SONG_AUTHOR_AUTHOR_ID;

/**
 * Data access object for working with {@link Author}.
 * <p>
 * Created on 6/16/2018.
 *
 * @author Vasilii Komarov
 */
public class AuthorDAO implements DAO<Author> {
    private static final Logger LOGGER = Logger.getLogger(AuthorDAO.class);

    @Override
    public Long create(Author entity) throws DAOException {
        final String insertAuthorsSql =
                String.format("INSERT INTO %s.%s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                        SCHEMA, AUTHORS_TABLE, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME,
                        AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertAuthorsStatement =
                     connection.prepareStatement(insertAuthorsSql, Statement.RETURN_GENERATED_KEYS)) {
            insertAuthorsStatement.setString(1, entity.getName().getFirstName());
            insertAuthorsStatement.setString(2, entity.getName().getLastName());
            if (entity.getBirthDate() == null) {
                insertAuthorsStatement.setNull(3, Types.DATE);
            } else {
                insertAuthorsStatement.setObject(3, new Date(entity.getBirthDate().getTime()));
            }
            insertAuthorsStatement.setString(4, entity.getAuthorType().toString());
            insertAuthorsStatement.executeUpdate();
            try (ResultSet resultSet = insertAuthorsStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                } else {
                    throw new DAOException("Error author inserting");
                }
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Author findById(Long id) throws DAOException {
        final String selectAuthorsSql = String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                SCHEMA, AUTHORS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAuthorsStatement = connection.prepareStatement(selectAuthorsSql)) {
            selectAuthorsStatement.setLong(1, id);
            try (ResultSet resultSet = selectAuthorsStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseAuthor(resultSet);
                } else {
                    LOGGER.info(String.format("Author with id = %d not found", id));
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
    public void update(Author entity) throws DAOException {
        final String updateAuthorsSql =
                String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                        SCHEMA, AUTHORS_TABLE, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE,
                        AUTHORS_AUTHOR_TYPE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateAuthorsStatement =
                     connection.prepareStatement(updateAuthorsSql)) {
            updateAuthorsStatement.setString(1, entity.getName().getFirstName());
            updateAuthorsStatement.setString(2, entity.getName().getLastName());
            if (entity.getBirthDate() == null) {
                updateAuthorsStatement.setNull(3, Types.DATE);
            } else {
                updateAuthorsStatement.setObject(3, new Date(entity.getBirthDate().getTime()));
            }
            updateAuthorsStatement.setString(4, entity.getAuthorType().toString());
            updateAuthorsStatement.setLong(5, entity.getId());
            updateAuthorsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void delete(Long id) throws DAOException {
        final String deleteAuthorsSql = String.format("DELETE FROM %s.%s WHERE %s = ?",
                SCHEMA, AUTHORS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement deleteAuthorsStatement = connection.prepareStatement(deleteAuthorsSql)) {
            checkForeignRelations(id, connection);
            deleteAuthorsStatement.setLong(1, id);
            deleteAuthorsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Author> findAll() throws DAOException {
        final String selectAuthorsSql = String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s",
                ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                SCHEMA, AUTHORS_TABLE);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAuthorsStatement = connection.prepareStatement(selectAuthorsSql)) {
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

    public List<Author> findAllByName(String name) throws DAOException {
        final String selectAuthorsSql =
                String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s WHERE %s LIKE ? OR %s LIKE ?",
                        ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                        SCHEMA, AUTHORS_TABLE, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectAuthorsStatement = connection.prepareStatement(selectAuthorsSql)) {
            selectAuthorsStatement.setString(1, String.format("%%%s%%", name));
            selectAuthorsStatement.setString(2, String.format("%%%s%%", name));
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

    private int findSongsCountByAuthorId(Long authorId, Connection connection) {
        final String countAlias = "total";
        final String selectSongAuthorSql = String.format("SELECT COUNT(*) AS %s FROM %s.%s WHERE %s = ?",
                countAlias, SCHEMA, SONG_AUTHOR_TABLE, SONG_AUTHOR_AUTHOR_ID);
        try (PreparedStatement selectSongAuthorStatement =
                     connection.prepareStatement(selectSongAuthorSql)) {
            selectSongAuthorStatement.setLong(1, authorId);
            try (ResultSet resultSet = selectSongAuthorStatement.executeQuery()) {
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
        if (findSongsCountByAuthorId(id, connection) > 0) {
            throw new ForeignKeyViolationException(
                    String.format("The %s table has still contained records with %s = %s",
                            SONG_AUTHOR_TABLE, SONG_AUTHOR_AUTHOR_ID, id)
            );
        }
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
}
