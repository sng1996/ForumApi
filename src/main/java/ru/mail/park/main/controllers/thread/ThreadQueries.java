package ru.mail.park.main.controllers.thread;

import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.thread.ThreadCreationRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
public class ThreadQueries {

    public static String createThreadQuery(ThreadCreationRequest threadCreationRequest, int userId, int forumId) {
        return "INSERT INTO thread (forumID, userID, isClosed, creationDate, message, slug, isDeleted) VALUES ('"+
                forumId + ", " +
                userId + ", " +
                (threadCreationRequest.isClosed() ? 1 : 0) + ", '" +
                threadCreationRequest.getDate() + "', '" +
                threadCreationRequest.getMessage() + "', '" +
                threadCreationRequest.getSlug() + "', " +
                (threadCreationRequest.isDeleted() ? 1 : 0) +
                ')';
    }

    public static Integer getThreadById(int threadId) {
        try {
            ResultSet result = Database.select("SELECT threadID FROM threads WHERE postID=" + threadId);
            return result.getInt("threadID");
        } catch (DbException ex) {
            System.out.println(ex.getMessage());
            return null;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
