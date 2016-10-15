package ru.mail.park.main.controllers.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.thread.ThreadCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static ObjectNode getThreadInfoById(int threadId) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        final ObjectNode threadInfo = mapper.createObjectNode();

        final Map<String, Integer> ids = new HashMap<String, Integer>();

        Database.select("SELECT * FROM threads WHERE threadID=" + threadId,
                result -> {
                    result.next();
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
                });

        threadInfo.put("user", UserQueries.getEmailByUserId(ids.get("userID")));
        threadInfo.put("forum", ForumQueries.getShortNameByForumId(ids.get("forumID")));

        return threadInfo;
    }

}
