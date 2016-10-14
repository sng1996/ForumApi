package ru.mail.park.main.controllers.user;

import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.user.UserCreationRequest;
import ru.mail.park.main.responses.user.UserInfoResponse;

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

    public static UserInfoResponse getUserInfoByEmail (String email) throws SQLException {
        final UserInfoResponse userInfoResponse = new UserInfoResponse();

        Database.select("SELECT userID, about, isAnonymous, name, username, email " +
                "FROM users WHERE email='" + email + '\'',
                result -> {
                    result.next();
                    userInfoResponse.setId(result.getInt("userID"));
                    userInfoResponse.setAbout(result.getString("about"));
                    userInfoResponse.setIsAnonymous(result.getBoolean("isAnonymous"));
                    userInfoResponse.setName(result.getString("name"));
                    userInfoResponse.setUsername(result.getString("username"));
                    userInfoResponse.setEmail(result.getString("email"));

                });

        //getting followers
        Database.select("SELECT email FROM users " +
                "INNER JOIN followers " +
                " ON followers.followeeID=" + userInfoResponse.getId(),
                result -> {
                    while (result.next()) {
                        userInfoResponse.addFollowee(result.getString("email"));
                    }
                });

        //getting followees

        Database.select("SELECT email FROM users " +
                "INNER JOIN followers " +
                " ON followers.followerID=" + userInfoResponse.getId(),
                result -> {
                    while (result.next()) {
                        userInfoResponse.addFollowee(result.getString("email"));
                    }
                });

        //getting subscriptions
        Database.select("SELECT subscriptions.postID FROM subscriptions " +
                "INNER JOIN posts " +
                " ON subscriptions.userID=" + userInfoResponse.getId(),
                result -> {
                    while (result.next()) {
                        userInfoResponse.addSubscription(result.getInt("postID"));
                    }
                });

        return userInfoResponse;
    }
}
