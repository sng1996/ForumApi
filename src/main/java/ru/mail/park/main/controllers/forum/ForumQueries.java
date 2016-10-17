package ru.mail.park.main.controllers.forum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.forum.ForumCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("Duplicates")
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

    public static ArrayNode getUserList (String forumShortName,
                                          String order, Integer limit, Integer startId) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder query = new StringBuilder();

        query.append("SELECT users.* FROM users INNER JOIN forums ON " +
                "users.userID=forums.userID WHERE forums.short_name='");
        query.append(forumShortName).append("' ");

        if (startId != null) query.append("AND users.userID>=").append(startId).append(' ');

        if (order != null) query.append("ORDER BY users.name ").append(order).append(' ');

        if (limit != null) query.append("LIMIT ").append(limit);

        ArrayNode userList = mapper.createArrayNode();

        Database.select(query.toString(),
                result -> {
                    while (result.next()) {
                        final ObjectNode userInfoResponse = mapper.createObjectNode();
                        userInfoResponse.put("id", result.getInt("userID"));
                        userInfoResponse.put("about", result.getString("about"));
                        userInfoResponse.put("isAnonymous", result.getBoolean("isAnonymous"));
                        userInfoResponse.put("name", result.getString("name"));
                        userInfoResponse.put("username", result.getString("username"));
                        userInfoResponse.put("email", result.getString("email"));

                        //getting followers
                        final ArrayNode followers = mapper.createArrayNode();

                        Database.select("SELECT email FROM users " +
                                        "INNER JOIN followers " +
                                        " ON followers.followeeID=" + userInfoResponse.get("id").asInt(),
                                res -> {
                                    while (res.next()) {
                                        followers.add(res.getString("email"));
                                    }
                                });

                        userInfoResponse.set("followers", followers);

                        //getting followees
                        final ArrayNode followees = mapper.createArrayNode();

                        Database.select("SELECT email FROM users " +
                                        "INNER JOIN followers " +
                                        " ON followers.followerID=" + userInfoResponse.get("id").asInt(),
                                res -> {
                                    while (res.next()) {
                                        followees.add(res.getString("email"));
                                    }
                                });

                        userInfoResponse.set("following", followees);

                        //getting subscriptions
                        final ArrayNode subscriptions = mapper.createArrayNode();

                        Database.select("SELECT subscriptions.threadID FROM subscriptions " +
                                        "WHERE subscriptions.userID=" + userInfoResponse.get("id").asInt(),
                                res -> {
                                    while (res.next()) {
                                        subscriptions.add(res.getInt("threadID"));
                                    }
                                });

                        userInfoResponse.set("subscriptions", subscriptions);

                        userList.add(userInfoResponse);
                    }

                });
        return userList;
    }
}
