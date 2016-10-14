package ru.mail.park.main.database;

import sun.plugin2.main.server.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by farid on 14.10.16.
 */
public interface TResultCallback<T> {
    T call(ResultSet result) throws SQLException;
}
