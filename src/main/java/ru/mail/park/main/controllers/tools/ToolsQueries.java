package ru.mail.park.main.controllers.tools;

import ru.mail.park.main.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by farid on 13.10.16.
 */
public class ToolsQueries {

    public static Integer getCurrentCount(String table) {
        try {
            return Database.select("SELECT count FROM counters WHERE counterName='" + table + '\'',
                    result -> {
                        result.next();
                        return result.getInt("count");
                    });
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
