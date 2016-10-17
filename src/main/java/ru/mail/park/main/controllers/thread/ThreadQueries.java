package ru.mail.park.main.controllers.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.thread.ThreadCreationRequest;
import ru.mail.park.main.requests.thread.ThreadSubscriptionRequest;
import ru.mail.park.main.requests.thread.ThreadUpdateRequest;
import ru.mail.park.main.requests.thread.ThreadVoteRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farid on 12.10.16.
 */
public class ThreadQueries {

    public static String createThreadQuery(ThreadCreationRequest threadCreationRequest, int userId, int forumId) {
        return "INSERT INTO threads (forumID, userID, title, isClosed, creationDate, message, slug, isDeleted) VALUES ("+
                forumId + ", " +
                userId + ", '" +
                threadCreationRequest.getTitle() + "', " +
                (threadCreationRequest.isClosed() ? 1 : 0) + ", '" +
                threadCreationRequest.getDate() + "', '" +
                threadCreationRequest.getMessage() + "', '" +
                threadCreationRequest.getSlug() + "', " +
                (threadCreationRequest.isDeleted() ? 1 : 0) +
                ')';
    }

    private static void fillThreadFromTable(ResultSet result, ObjectNode threadInfo,
                                           Map<String, Integer> ids) throws SQLException {
        threadInfo.put("date", result.getString("creationDate").replace(".0", ""));
        threadInfo.put("dislikes", result.getInt("dislikes"));
        threadInfo.put("id", result.getInt("threadID"));
        threadInfo.put("isClosed", result.getBoolean("isClosed"));
        threadInfo.put("isDeleted", result.getBoolean("isDeleted"));
        threadInfo.put("likes", result.getInt("likes"));
        threadInfo.put("message", result.getString("message"));
        threadInfo.put("points", threadInfo.get("likes").asInt() -
                threadInfo.get("dislikes").asInt());
        threadInfo.put("posts", result.getInt("postCount"));
        threadInfo.put("slug", result.getString("slug"));
        threadInfo.put("title", result.getString("title"));

        ids.put("forumID", result.getInt("forumID"));
        ids.put("userID", result.getInt("userID"));
    }

    public static ObjectNode getThreadInfoById(int threadId) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ObjectNode threadInfo = mapper.createObjectNode();

        final Map<String, Integer> ids = new HashMap<String, Integer>();

        Database.select("SELECT * FROM threads WHERE threadID=" + threadId,
                result -> {
                    result.next();
                    fillThreadFromTable(result, threadInfo, ids);
                });

        threadInfo.put("user", UserQueries.getEmailByUserId(ids.get("userID")));
        threadInfo.put("forum", ForumQueries.getShortNameByForumId(ids.get("forumID")));

        return threadInfo;
    }

    private static void insertRelatedStuff(ObjectNode threadInfo,
                                           ArrayList<String> related) throws SQLException {
        if (related.contains("user")) {
            threadInfo.set("user", UserQueries.getUserInfoByEmail(threadInfo.get("user").asText()));
        }

        if (related.contains("forum")) {
            threadInfo.set("forum", ForumQueries.getForumInfoByShortName(threadInfo.get("forum").asText(), false));
        }
    }

    public static ArrayNode getThreadList(Map<String, String> threadSource, Integer limit, String startDate,
                                          String order, ArrayList<String> related) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ArrayNode threadList = mapper.createArrayNode();

        final StringBuilder query = new StringBuilder();
        query.append("SELECT threads.* FROM threads INNER JOIN ");

        String filterStatement = null;

        if (threadSource.containsKey("forum")) {
            query.append("forums ON threads.forumID=forums.forumID ");
            filterStatement = "threads.forumID=" +
                    ForumQueries.getForumIdByShortName(threadSource.get("forum")) + ' ';
        } else {
            query.append("users ON threads.userID=users.userID ");
            filterStatement = "threads.userID=" +
                    UserQueries.getUserIdByEmail(threadSource.get("user")) + ' ';
        }

        query.append("WHERE ");
        if (startDate != null && !startDate.isEmpty()) {
            query.append("threads.creationDate>='").append(startDate).append("' AND ");
        }

        query.append(filterStatement);
        query.append("ORDER BY threads.creationDate ").append(order).append(' ');
        if (limit != null) query.append("LIMIT ").append(limit);

        final Map<String, Integer> ids = new HashMap<String, Integer>();

        Database.select(query.toString(), result -> {
            while(result.next()) {
                final ObjectNode threadInfo = mapper.createObjectNode();

                ThreadQueries.fillThreadFromTable(result, threadInfo, ids);
                //FIXME: SLOW!!!
                threadInfo.put("user", UserQueries.getEmailByUserId(ids.get("userID")));
                threadInfo.put("forum", ForumQueries.getShortNameByForumId(ids.get("forumID")));

                if(related != null) {
                    ThreadQueries.insertRelatedStuff(threadInfo, related);
                }

                threadList.add(threadInfo);
            }
        });

        return threadList;
    }

    public static String createThreadRemovalQuery(int threadId) {
        return "UPDATE threads SET isDeleted=TRUE WHERE threadID=" + threadId;
    }

    public static String createRelatedPostsRemovalQuery(int threadId) {
        return "UPDATE posts SET isDeleted=TRUE WHERE threadID=" + threadId;
    }

    public static String createThreadCloseQuery(int threadId) {
        return "UPDATE threads SET isClosed=TRUE WHERE threadID=" + threadId;
    }

    public static String createThreadOpenQuery(int threadId) {
        return "UPDATE threads SET isClosed=FALSE WHERE threadID=" + threadId;
    }

    public static String createThreadRestoreQuery(int threadId) {
        return "UPDATE threads SET isDeleted=False WHERE threadID=" + threadId;
    }

    public static String createRelatedPostsRestoreQuery(int threadId) {
        return "UPDATE posts SET isDeleted=FALSE WHERE threadID=" + threadId;
    }

    public static String createSubscriptionQuery(ThreadSubscriptionRequest
                                                         threadSubscriptionRequest) throws SQLException {
        return "INSERT INTO subscriptions (threadID, userID) VALUES (" + threadSubscriptionRequest.getThread() +
                ", " + UserQueries.getUserIdByEmail(threadSubscriptionRequest.getUser()) + ')';
    }

    public static String createUnsubscriptionQuery(ThreadSubscriptionRequest
                                                           threadSubscriptionRequest) throws SQLException {
        return "DELETE FROM subscriptions WHERE threadID=" +
                threadSubscriptionRequest.getThread() + " AND userID=" +
                UserQueries.getUserIdByEmail(threadSubscriptionRequest.getUser());
    }

    public static String createThreadVoteQuery(ThreadVoteRequest threadVoteRequest) throws SQLException {
        return "UPDATE threads SET " +
                ((threadVoteRequest.getVote() == 1) ? "likes=likes+1, points=points+1 " :
                        "dislikes=dislikes+1, points=points-1 ") +
                "WHERE threadID=" + threadVoteRequest.getThread();
    }

    public static String createThreadUpdateQuery(ThreadUpdateRequest threadUpdateRequest) {
        return "UPDATE threads SET message='" + threadUpdateRequest.getMessage() + "', " +
                "slug='" + threadUpdateRequest.getSlug() + "' " +
                "WHERE threadID=" + threadUpdateRequest.getThread();
    }

}
