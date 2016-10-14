package ru.mail.park.main.database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

/**
 * Created by farid on 11.10.16.
 */
@SuppressWarnings("Duplicates")
public class Database {

    private static BasicDataSource dataSource;

    static {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(Credentials.HOST);
        dataSource.setPassword(Credentials.PASSWORD);
        dataSource.setUsername(Credentials.USER);
    }

    public static <T> T select(String query, TResultCallback<T> callback) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                try (ResultSet result = statement.getResultSet()) {
                    return callback.call(result);
                }
            }
        }
    }

    public static void select(String query, ResultCallback callback) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                try (ResultSet result = statement.getResultSet()) {
                    callback.call(result);
                }
            }
        }
    }

    //update allows UPDATE, DELETE and INSERT queries
    public static int update(String query) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);

                return 0;
            }
        }
    }
}
