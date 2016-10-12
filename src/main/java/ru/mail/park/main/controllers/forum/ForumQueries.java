package ru.mail.park.main.controllers.forum;

import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.forum.ForumCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
public class ForumQueries {

    public static String createForumQuery(ForumCreationRequest forumCreationRequest, int userID) {
        return "INSERT INTO forums (name, short_name, user_id) VALUES ('" +
                forumCreationRequest.getName() + "', '" +
                forumCreationRequest.getShort_name() + "', '" +
                userID + ')';
    }

    public static Integer getForumIdByShortName(String short_name) {
        try {
            ResultSet result = Database.select("SELECT forumID FROM forums WHERE short_name=" + short_name);
            return result.getInt("forumID");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        } catch (DbException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
