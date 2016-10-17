package ru.mail.park.main.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.user.UserCreationRequest;
import ru.mail.park.main.requests.user.UserFollowRequest;
import ru.mail.park.main.requests.user.UserUpdateRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("Duplicates")
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

    public static Integer getUserIdByEmail (String email) throws SQLException {
        return Database.select("SELECT userID FROM users WHERE email='" + email + '\'',
                result -> {
                    result.next();
                    return result.getInt("userID");
                });
    }

    public static String getEmailByUserId (int userID) throws SQLException {
        return Database.select("SELECT email FROM users WHERE userID=" + userID,
                result -> {
                    result.next();
                    return result.getString("email");
                });
    }

    public static ObjectNode getUserInfoByEmail (String email) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        final ObjectNode userInfoResponse = mapper.createObjectNode();

        Database.select("SELECT userID, about, isAnonymous, name, username, email " +
                "FROM users WHERE email='" + email + '\'',
                result -> {
                    result.next();
                    userInfoResponse.put("id", result.getInt("userID"));
                    userInfoResponse.put("about", result.getString("about"));
                    userInfoResponse.put("isAnonymous", result.getBoolean("isAnonymous"));
                    userInfoResponse.put("name", result.getString("name"));
                    userInfoResponse.put("username", result.getString("username"));
                    userInfoResponse.put("email", result.getString("email"));

                });

        //getting followers
        final ArrayNode followers = mapper.createArrayNode();

        Database.select("SELECT followerID FROM followers " +
                "WHERE followers.followeeID=" + userInfoResponse.get("id").asInt(),
                result -> {
                    String userEmail;
                    while (result.next()) {
                        userEmail = UserQueries.getEmailByUserId(result.getInt("followerID"));
                        followers.add(userEmail);
                    }
                });

        userInfoResponse.set("followers", followers);

        //getting followees
        final ArrayNode followees = mapper.createArrayNode();

        Database.select("SELECT followeeID FROM followers " +
                "WHERE followers.followerID=" + userInfoResponse.get("id").asInt(),
                result -> {
                    String userEmail;
                    while (result.next()) {
                        userEmail = UserQueries.getEmailByUserId(result.getInt("followeeID"));
                        followees.add(userEmail);
                    }
                });

        userInfoResponse.set("following", followees);

        //getting subscriptions
        final ArrayNode subscriptions = mapper.createArrayNode();

        Database.select("SELECT subscriptions.threadID FROM subscriptions " +
                "WHERE subscriptions.userID=" + userInfoResponse.get("id").asInt(),
                result -> {
                    while (result.next()) {
                        subscriptions.add(result.getInt("threadID"));
                    }
                });

        userInfoResponse.set("subscriptions", subscriptions);
        return userInfoResponse;
    }

    public static String createFollowQuery(UserFollowRequest userFollowRequest) throws SQLException {
        Integer followerId = getUserIdByEmail(userFollowRequest.getFollower());
        Integer followeeId = getUserIdByEmail(userFollowRequest.getFollowee());

        return "INSERT INTO followers (followerID, followeeID) VALUES(" + followerId + ',' + followeeId + ')';
    }

    private static void getPostedUsersIds(StringBuilder userIds, Map<String, String> userSource) throws SQLException {

        String query = "";
        if (userSource.containsKey("forum")) {
            Integer forumId = ForumQueries.getForumIdByShortName(userSource.get("forum"));
            query = "SELECT DISTINCT userID FROM posts WHERE posts.forumID=" + forumId;
        }

        if (userSource.containsKey("followers")) {
            Integer userId = UserQueries.getUserIdByEmail(userSource.get("followers"));
            query = "SELECT DISTINCT followeeID AS userID FROM followers WHERE followers.followerID=" + userId;
        }

        if (userSource.containsKey("followees")) {
            Integer userId = UserQueries.getUserIdByEmail(userSource.get("followees"));
            query = "SELECT DISTINCT followerID AS userID FROM followers WHERE followers.followeeID=" + userId;
        }

        Database.select(query, result -> {
            while (result.next()) {
                userIds.append(result.getInt("userID")).append(',');
            }
            if(userIds.length() > 0) userIds.deleteCharAt(userIds.length() - 1);
        });
    }

    public static ArrayNode getUserList (Map<String, String> userSource,
                                         String order, Integer limit, Integer startId) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder query = new StringBuilder();

        StringBuilder userIds = new StringBuilder();
        UserQueries.getPostedUsersIds(userIds, userSource);

        ArrayNode userList = mapper.createArrayNode();

        if(userIds.length() == 0) return userList;

        query.append("SELECT users.* FROM users WHERE userID IN (").append(userIds).append(") ");

        if (startId != null) query.append("AND users.userID>=").append(startId).append(' ');

        if (order != null) query.append("ORDER BY users.name ").append(order).append(' ');

        if (limit != null) query.append("LIMIT ").append(limit);

        System.out.println(query.toString());
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

                        Database.select("SELECT followerID FROM followers " +
                                        "WHERE followers.followeeID=" + userInfoResponse.get("id").asInt(),
                                res -> {
                                    String userEmail;
                                    while (res.next()) {
                                        userEmail = UserQueries.getEmailByUserId(res.getInt("followerID"));
                                        followers.add(userEmail);
                                    }
                                });

                        userInfoResponse.set("followers", followers);

                        //getting followees
                        final ArrayNode followees = mapper.createArrayNode();

                        Database.select("SELECT followeeID FROM followers " +
                                        "WHERE followers.followerID=" + userInfoResponse.get("id").asInt(),
                                res -> {
                                    String userEmail;
                                    while (res.next()) {
                                        userEmail = UserQueries.getEmailByUserId(res.getInt("followeeID"));
                                        followees.add(userEmail);
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

    public static String createUnfollowQuery(UserFollowRequest userFollowRequest) throws SQLException {
        Integer followerId = getUserIdByEmail(userFollowRequest.getFollower());
        Integer followeeId = getUserIdByEmail(userFollowRequest.getFollowee());

        return "DELETE FROM followers WHERE followerId=" + followerId + " AND followeeId=" + followeeId;
    }

    public static String createUpdateQuery(UserUpdateRequest userUpateRequest) throws SQLException {
        return "UPDATE users SET name='" + userUpateRequest.getName() + "', about='"
                + userUpateRequest.getAbout() + "' WHERE email='" + userUpateRequest.getUser() + '\'';
    }
}
