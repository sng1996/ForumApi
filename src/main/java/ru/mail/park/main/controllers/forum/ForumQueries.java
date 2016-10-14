package ru.mail.park.main.controllers.forum;

import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.forum.ForumCreationRequest;
import ru.mail.park.main.responses.forums.ForumInfoResponse;

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

    public static Integer getForumIdByShortName(String shortName) throws SQLException {
        return Database.select("SELECT forumID FROM forums WHERE short_name='" + shortName + '\'',
                result -> {
                    result.next();
                    return result.getInt("forumID");
                });
    }

    public static ForumInfoResponse getForumInfoByShortName (String shortName, boolean getUser) throws SQLException {
        final ForumInfoResponse forumInfoResponse = new ForumInfoResponse();

            final int userID = Database.select("SELECT forumID, name, short_name, userID" +
                            "FROM forums WHERE short_name='" + shortName + '\'',
                    result -> {
                        result.next();

                        forumInfoResponse.setId(result.getInt("forumID"));
                        forumInfoResponse.setName(result.getString("name"));
                        forumInfoResponse.setShort_name(result.getString("short_name"));
                        return result.getInt("userID");
                    });

        if (getUser) {
            forumInfoResponse.setUser(UserQueries.getUserInfoByEmail(UserQueries.getEmailByUserId(userID)));
        }

        return forumInfoResponse;
    }
}
