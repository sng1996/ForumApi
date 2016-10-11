package ru.mail.park.main.database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by farid on 11.10.16.
 */
public class Database {

    /*private static DataSource dataSource;

    static {
        try {
            Context context = new InitialContext();
            dataSource = (DataSource) context.lookup(Credentials.HOST);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }*/

    public static ResultSet select(String query) {
        try (Connection connection = DriverManager.getConnection(Credentials.HOST, Credentials.USER, Credentials.PASSWORD);
             Statement statement = connection.createStatement()) {
            final ResultSet result = statement.executeQuery(query);
            connection.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //update allows UPDATE, DELETE and INSERT queries
    public static int update(String query) {
        try(Connection connection = DriverManager.getConnection(Credentials.HOST, Credentials.USER, Credentials.PASSWORD);
            Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            connection.close();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
