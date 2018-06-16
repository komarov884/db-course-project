package com.db.courseproject.musicstore.dao;

import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.model.Author;
import com.db.courseproject.musicstore.model.AuthorType;
import com.db.courseproject.musicstore.model.FullName;
import com.db.courseproject.musicstore.util.DBConnection;
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
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_TABLE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_FIRST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_LAST_NAME;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_BIRTH_DATE;
import static com.db.courseproject.musicstore.util.DBConstants.AUTHORS_AUTHOR_TYPE;

/**
 * <p>
 * Created on 6/16/2018.
 *
 * @author Vasilii Komarov
 */
public class AuthorDAO implements DAO<Author> {
    private static final Logger LOGGER = Logger.getLogger(AuthorDAO.class);

    @Override
    public Long create(Author entity) throws DAOException {
        return 0L;
    }

    @Override
    public Author findById(Long id) throws DAOException {
        final String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s WHERE %s = ?",
                ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                SCHEMA, AUTHORS_TABLE, ID);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return parse(resultSet);
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

    }

    @Override
    public void delete(Long id) throws DAOException {

    }

    @Override
    public List<Author> findAll() throws DAOException {
        return null;
    }

    public List<Author> findAllByIds(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        final String condition = buildCondition(authorIds);
        final String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s.%s WHERE %s IN %s",
                ID, AUTHORS_FIRST_NAME, AUTHORS_LAST_NAME, AUTHORS_BIRTH_DATE, AUTHORS_AUTHOR_TYPE,
                SCHEMA, AUTHORS_TABLE, ID, condition);
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Author> authors = new ArrayList<>();
                while (resultSet.next()) {
                    authors.add(parse(resultSet));
                }
                return authors;
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private Author parse(ResultSet resultSet) throws SQLException {
        Author author = (Author) new Author().setId(resultSet.getLong(ID))
                .setName(new FullName()
                        .setFirstName(resultSet.getString(AUTHORS_FIRST_NAME))
                        .setLastName(resultSet.getString(AUTHORS_LAST_NAME)))
                .setBirthDate(resultSet.getDate(AUTHORS_BIRTH_DATE));
        author.setAuthorType(AuthorType.valueOf(resultSet.getString(AUTHORS_AUTHOR_TYPE)));
        return author;
    }

    private String buildCondition(List<Long> authorIds) {
        StringBuilder builder = new StringBuilder("(");
        Iterator<Long> iterator = authorIds.iterator();
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
