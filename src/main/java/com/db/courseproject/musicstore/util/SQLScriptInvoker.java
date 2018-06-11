package com.db.courseproject.musicstore.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class for working with SQL scripts.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
public class SQLScriptInvoker {
    /**
     * Executes SQL script.
     *
     * @param connection    connection to database.
     * @param file          SQL script file.
     * @throws SQLException
     */
    public static void executeScript(Connection connection, String file) throws SQLException {
        try {
            String script = readScript(file);
            try (PreparedStatement statement = connection.prepareStatement(script)) {
                statement.execute();
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Reads SQL script file and returns its string representation.
     *
     * @param file  SQL script file.
     * @return      string with SQL script.
     * @throws IOException
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String readScript(String file) throws IOException {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(file)) {
            byte[] script = new byte[inputStream.available()];
            inputStream.read(script);
            return new String(script);
        } catch (IOException e) {
            throw new IOException(String.format("Error reading script file: %s", file));
        }
    }
}
