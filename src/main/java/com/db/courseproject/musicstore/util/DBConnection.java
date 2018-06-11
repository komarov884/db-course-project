package com.db.courseproject.musicstore.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection provider.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class DBConnection {
    private static final Logger LOGGER = LogManager.getLogger(DBConnection.class);

    private static final String DB_PROPERTIES_FILE = "db/db.properties";

    private static final String DRIVER_CLASS_NAME_PROPERTY = "jdbc.driverClassName";
    private static final String URL_PROPERTY = "jdbc.url";
    private static final String USER_PROPERTY = "jdbc.user";
    private static final String PASSWORD_PROPERTY = "jdbc.password";

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            initConnection();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Reads connection properties and registers JDBC driver.
     *
     * @throws SQLException
     */
    private static void initConnection() throws SQLException {
        Properties dbProperties = new Properties();

        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            dbProperties.load(inputStream);
            LOGGER.info("Properties file was read");

            driver = dbProperties.getProperty(DRIVER_CLASS_NAME_PROPERTY);
            url = dbProperties.getProperty(URL_PROPERTY);
            user = dbProperties.getProperty(USER_PROPERTY);
            password = dbProperties.getProperty(PASSWORD_PROPERTY);
            LOGGER.info("Database properties were set");

            Class.forName(driver);
            LOGGER.info("JDBC driver was registered");
        } catch (IOException e) {
            throw new SQLException(String.format("Error reading database properties file: %s", DB_PROPERTIES_FILE), e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error initializing JDBC driver", e);
        }
    }

    /**
     * Provides connection to database.
     *
     * @return instance of Connection.
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
