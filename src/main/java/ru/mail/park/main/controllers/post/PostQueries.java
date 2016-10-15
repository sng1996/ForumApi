package ru.mail.park.main.controllers.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.thread.ThreadQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.post.PostCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farid on 12.10.16.
 */
public class PostQueries {

    public static String createPostQuery(PostCreationRequest postCreationRequest, int threadId,
                                         int userId, int forumId, Integer parentId) {
        return "INSERT INTO posts (creationDate, threadID, message, userID, forumID, parentPostID, " +
                "isApproved, isHighlighted, isEdited, isSpam, isDeleted) VALUES('" +
                postCreationRequest.getDate() + "', " +
                threadId +  ", '" +
                postCreationRequest.getMessage() + "', " +
                userId + ", " +
                forumId + ", " +
                ((parentId == null) ? "NULL" : parentId) + ", " +
                (postCreationRequest.isApproved() ? 1 : 0) + ", " +
                (postCreationRequest.isHighlighted() ? 1 : 0) + ", " +
                (postCreationRequest.isEdited() ? 1 : 0) + ", " +
                (postCreationRequest.isSpam() ? 1 : 0) + ", " +
                (postCreationRequest.isDeleted() ? 1 : 0) +
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

    private static void fillPostFromTable(ResultSet result, ObjectNode postInfo,
                                          Map<String, Integer> ids) throws SQLException {
        postInfo.put("date", result.getString("creationDate").replace(".0", ""));
        postInfo.put("dislikes", result.getInt("dislikes"));
        postInfo.put("id", result.getInt("postID"));
        postInfo.put("isApproved", result.getBoolean("isApproved"));
        postInfo.put("isDeleted", result.getBoolean("isDeleted"));
        postInfo.put("isEdited", result.getBoolean("isEdited"));
        postInfo.put("isHighlighted", result.getBoolean("isHighlighted"));
        postInfo.put("isSpam", result.getBoolean("isSpam"));
        postInfo.put("likes", result.getInt("likes"));
        postInfo.put("message", result.getString("message"));

        Integer parentId = result.getInt("parentPostID");
        if(result.wasNull()) parentId = null;

        postInfo.put("parent", parentId);
        postInfo.put("points", postInfo.get("likes").asInt() -
                postInfo.get("dislikes").asInt());
        postInfo.put("thread", result.getInt("threadID"));

        ids.put("forumID", result.getInt("forumID"));
        ids.put("userID", result.getInt("userID"));
    }

    public static ObjectNode getPostInfoById(int postId) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ObjectNode postInfo = mapper.createObjectNode();

        final Map<String, Integer> ids = new HashMap<String, Integer>();

        Database.select("SELECT * FROM posts WHERE postID=" + postId,
                result -> {
                    result.next();
                    PostQueries.fillPostFromTable(result, postInfo, ids);
                });

        postInfo.put("user", UserQueries.getEmailByUserId(ids.get("userID")));
        postInfo.put("forum", ForumQueries.getShortNameByForumId(ids.get("forumID")));

        return postInfo;
    }

    public static ArrayNode getPostList(int threadId, Integer limit,
                                         String startDate, String order) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ArrayNode postList = mapper.createArrayNode();
        final Map<String, Integer> ids = new HashMap<String, Integer>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT posts.* FROM posts INNER JOIN threads ");
        query.append("ON posts.threadID=threads.threadID ");
        query.append("WHERE ");

        if (startDate != null && !startDate.isEmpty()) {
            query.append("posts.creationDate >='").append(startDate).append("' AND ");
        }

        query.append("posts.threadID=").append(threadId).append(' ');
        query.append("ORDER BY posts.creationDate ").append(order).append(' ');
        if (limit != null) query.append("LIMIT ").append(limit).append(' ');

        Database.select(query.toString(), result -> {
                    while(result.next()) {
                        final ObjectNode postInfo = mapper.createObjectNode();

                        PostQueries.fillPostFromTable(result, postInfo, ids);
                        //FIXME: SLOW!!!
                        postInfo.put("user", UserQueries.getEmailByUserId(ids.get("userID")));
                        postInfo.put("forum", ForumQueries.getShortNameByForumId(ids.get("forumID")));
                        postList.add(postInfo);
                    }
                });

        return postList;
    }

    public static ArrayNode getPostList(String forumShortName, Integer limit,
                                        String startDate, String order) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ArrayNode postList = mapper.createArrayNode();

        final Map<String, Integer> ids = new HashMap<String, Integer>();

        Integer forumId = ForumQueries.getForumIdByShortName(forumShortName);

        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT posts.* FROM posts INNER JOIN forums ");
        query.append("ON posts.forumID=forums.forumID ");
        query.append("WHERE ");
        if (startDate != null && !startDate.isEmpty()) {
            query.append("posts.creationDate>='").append(startDate).append("' AND ");
        }

        query.append("posts.forumID=").append(forumId).append(' ');
        query.append("ORDER BY posts.creationDate ").append(order).append(' ');
        if (limit != null) query.append("LIMIT ").append(limit);

        Database.select(query.toString(), result -> {
            while(result.next()) {
                final ObjectNode postInfo = mapper.createObjectNode();

                PostQueries.fillPostFromTable(result, postInfo, ids);
                //FIXME: SLOW!!!
                postInfo.put("user", UserQueries.getEmailByUserId(ids.get("userID")));
                postInfo.put("forum", ForumQueries.getShortNameByForumId(ids.get("forumID")));
                postList.add(postInfo);
            }
        });

        return postList;
    }
}
