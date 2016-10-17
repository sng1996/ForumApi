package ru.mail.park.main.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.user.UserCreationRequest;

import java.sql.ResultSet;
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

        Database.select("SELECT email FROM users " +
                "INNER JOIN followers " +
                " ON followers.followeeID=" + userInfoResponse.get("id").asInt(),
                result -> {
                    while (result.next()) {
                        followers.add(result.getString("email"));
                    }
                });

        userInfoResponse.set("followers", followers);

        //getting followees
        final ArrayNode followees = mapper.createArrayNode();

        Database.select("SELECT email FROM users " +
                "INNER JOIN followers " +
                " ON followers.followerID=" + userInfoResponse.get("id").asInt(),
                result -> {
                    while (result.next()) {
                        followees.add(result.getString("email"));
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
}
