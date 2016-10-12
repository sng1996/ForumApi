package ru.mail.park.main.controllers.user;

import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.user.UserCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
public class UserQueries {

    public static String createUserQuery(UserCreationRequest userCreationRequest) {
        return "INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('" +
                userCreationRequest.getUsername() + "', '" +
                userCreationRequest.getAbout() + "', '" +
                userCreationRequest.getName() + "', '" +
                userCreationRequest.getEmail() + "', '" +
                (userCreationRequest.isAnonymous() ? 1 : 0) +
                "')";
    }

    public static Integer getUserIdByEmailQuery (String email) {
        try {
            ResultSet result = Database.select("SELECT userID FROM users WHERE email=" + email);
            return result.getInt("userID");
        } catch (DbException ex) {
            System.out.println(ex.getMessage());
            return null;
        }  catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
