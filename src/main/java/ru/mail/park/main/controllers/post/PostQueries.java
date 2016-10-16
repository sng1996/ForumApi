package ru.mail.park.main.controllers.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.thread.ThreadQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.post.PostCreationRequest;
import ru.mail.park.main.requests.post.PostUpdateRequest;
import ru.mail.park.main.requests.post.PostVoteRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("Duplicates")
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
        final ObjectMapper mapper = new ObjectMapper();

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

    private static void getRootPostPaths(int threadId,
                                       int limit, StringBuilder rootPostPaths, String order) throws SQLException {
        Database.select("SELECT DISTINCT SUBSTRING_INDEX(posts.path,'/',2) AS root FROM posts " +
                        "WHERE threadID=" + threadId + " ORDER BY root " + order + ' ' +
                        "LIMIT " + limit,
                result -> {
                    while(result.next()) {
                        rootPostPaths.append('\'').append(result.getString("root")).append("',");
                    }
                    rootPostPaths.deleteCharAt(rootPostPaths.length() - 1);
                });
    }

    //checks which values are not null by itself (except for threadId)
    public static ArrayNode getPostList(int threadId, Integer limit,
                                         String startDate, String order, String sortType) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ArrayNode postList = mapper.createArrayNode();
        final Map<String, Integer> ids = new HashMap<String, Integer>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT SUBSTRING_INDEX(posts.path,'/',2) AS root, posts.* FROM posts ");
        query.append("WHERE ");

        if (startDate != null && !startDate.isEmpty()) {
            query.append("posts.creationDate >='").append(startDate).append("' AND ");
        }

        query.append("posts.threadID=").append(threadId).append(' ');

        if (sortType == null || sortType.equals("plain")) {
            query.append("ORDER BY posts.creationDate ").append(order).append(' ');
            if (limit != null)  query.append("LIMIT ").append(limit).append(' ');query.append(' ');
        }

        if (sortType != null && sortType.equals("tree")) {
            query.append("ORDER BY root ").append(order).append(", ");
            query.append("path ASC ");
            if (limit != null) query.append("LIMIT ").append(limit).append(' ');query.append(' ');
        }

        if (limit != null) {
            if(sortType != null && sortType.equals("parent_tree")) {
                final StringBuilder rootPostPaths = new StringBuilder();

                getRootPostPaths(threadId, limit, rootPostPaths, order);
                query.append("HAVING root IN (").append(rootPostPaths.toString()).append(") ");
                query.append("ORDER BY root ").append(order).append(", ");
                query.append("path ASC");
            }
        }

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
        query.append("SELECT posts.* FROM posts INNER JOIN forums ");
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

    public static String createPostRemovalQuery(int postId) {
        return "UPDATE posts SET isDeleted=TRUE WHERE postID=" + postId;
    }

    public static String createPostRestoreQuery(int postId) {
        return "UPDATE posts SET isDeleted=FALSE WHERE postID=" + postId;
    }

    public static String createPostUpdateQuery(PostUpdateRequest postUpdateRequest) {
        return "UPDATE posts SET message='" + postUpdateRequest.getMessage() + "' " +
                "WHERE postID=" + postUpdateRequest.getPost();
    }

    public static String createPostVoteQuery(PostVoteRequest postVoteRequest) {
        return "UPDATE posts SET " +
                ((postVoteRequest.getVote() == 1) ? "likes=likes+1, points=points+1 " :
                        "dislikes=dislikes+1, points=points-1 ") +
                "WHERE postID=" + postVoteRequest.getPost();
    }
}
