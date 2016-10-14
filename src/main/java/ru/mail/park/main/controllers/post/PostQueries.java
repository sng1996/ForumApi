package ru.mail.park.main.controllers.post;

import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.post.PostCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
public class PostQueries {

    public static String createPostQuery(PostCreationRequest postCreationRequest, int threadId,
                                         int userId, int forumId, int postId) {
        return "INSERT INTO posts (creationDate, threadID, message, userID, forumID, parentPostID, " +
                "isApproved, isHighlighted, isEdited, isSpam, isDeleted) VALUES('" +
                postCreationRequest.getDate() + "', " +
                threadId +  ", '" +
                postCreationRequest.getMessage() + "', " +
                userId + ", " +
                forumId + ", " +
                postId + ", " +
                (postCreationRequest.isApproved() ? 1 : 0) + ", " +
                (postCreationRequest.isHighlighted() ? 1 : 0) + ", " +
                (postCreationRequest.isEdited() ? 1 : 0) + ", " +
                (postCreationRequest.isSpam() ? 1 : 0) + ", " +
                (postCreationRequest.isDeleted() ? 1 : 0) + ", " +
                ')';

    };

    public static Integer getPostById(int postId) {
        try {
            return Database.select("SELECT postID FROM posts WHERE postID='" + postId + '\'',
                    result -> {
                        result.next();
                        return result.getInt("postID");
                    });
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
