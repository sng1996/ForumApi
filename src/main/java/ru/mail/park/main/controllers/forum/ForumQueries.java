package ru.mail.park.main.controllers.forum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.forum.ForumCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
public class ForumQueries {

    public static String createForumQuery(ForumCreationRequest forumCreationRequest, int userID) {
        return "INSERT INTO forums (name, short_name, userID) VALUES ('" +
                forumCreationRequest.getName() + "', '" +
                forumCreationRequest.getShort_name() + "', " +
                userID + ')';
    }

    public static Integer getForumIdByShortName(String shortName) throws SQLException {
        return Database.select("SELECT forumID FROM forums WHERE short_name='" + shortName + '\'',
                result -> {
                    result.next();
                    return result.getInt("forumID");
                });
    }

    public static String getShortNameByForumId (int id) throws SQLException {
        return Database.select("SELECT short_name FROM forums WHERE forumID=" + id,
                result -> {
                    result.next();
                    return result.getString("short_name");
                });
    }

    public static ObjectNode getForumInfoByShortName (String shortName, boolean getUser) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        final ObjectNode forumInfo = mapper.createObjectNode();

        final int userID = Database.select("SELECT forumID, name, short_name, userID " +
                        "FROM forums WHERE short_name='" + shortName + '\'',
                result -> {
                    result.next();

                    forumInfo.put("id",result.getInt("forumID"));
                    forumInfo.put("name", result.getString("name"));
                    forumInfo.put("short_name", result.getString("short_name"));
                    return result.getInt("userID");
                });

        if (getUser) {
            forumInfo.set("user", UserQueries.getUserInfoByEmail(UserQueries.getEmailByUserId(userID)));
        } else {
            forumInfo.put("user", UserQueries.getEmailByUserId(userID));
        }

        return forumInfo;
    }
}
