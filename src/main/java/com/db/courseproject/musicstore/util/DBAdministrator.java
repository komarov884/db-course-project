package com.db.courseproject.musicstore.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class for database administration.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class DBAdministrator {
    private static final Logger LOGGER = Logger.getLogger(DBAdministrator.class);

    private static final String CREATE_TABLES_SCRIPT = "db/01_create_tables.sql";
    private static final String INSERT_INITIAL_DATA_SCRIPT = "db/02_insert_initial_data.sql";
    private static final String DROP_TABLES_SCRIPT = "db/03_drop_tables.sql";

    /**
     * Creates tables in database.
     *
     * @throws SQLException
     */
    public static void createTables() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            SQLScriptInvoker.executeScript(connection, CREATE_TABLES_SCRIPT);
            LOGGER.info("Database tables were created");
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Fills database tables with initial data.
     *
     * @throws SQLException
     */
    public static void insertData() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            SQLScriptInvoker.executeScript(connection, INSERT_INITIAL_DATA_SCRIPT);
            LOGGER.info("Database tables were filled with initial data");
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Drops database tables.
     *
     * @throws SQLException
     */
    public static void dropTables() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            SQLScriptInvoker.executeScript(connection, DROP_TABLES_SCRIPT);
            LOGGER.info("Database tables were dropped");
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
