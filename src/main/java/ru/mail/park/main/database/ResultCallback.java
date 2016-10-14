package ru.mail.park.main.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by farid on 14.10.16.
 */
public interface ResultCallback {
    void call(ResultSet result) throws SQLException;
}
