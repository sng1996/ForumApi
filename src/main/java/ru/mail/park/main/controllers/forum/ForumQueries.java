package ru.mail.park.main.controllers.forum;

import ru.mail.park.main.requests.forum.ForumCreationRequest;

/**
 * Created by farid on 12.10.16.
 */
public class ForumQueries {

    public static String createForumQuery(ForumCreationRequest forumCreationRequest, int userID) {
        return "INSERT INTO forums (name, short_name, user_id) VALUES ('" +
                forumCreationRequest.getName() + "', '" +
                forumCreationRequest.getShort_name() + "', '" +
                userID + ')';
    }
}
