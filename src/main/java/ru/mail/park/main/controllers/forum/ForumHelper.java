package ru.mail.park.main.controllers.forum;

import ru.mail.park.main.requests.forum.ForumCreationRequest;

/**
 * Created by farid on 12.10.16.
 */
public class ForumHelper {
    public static boolean nullCheck(ForumCreationRequest forumCreationRequest) {
        return (!forumCreationRequest.getUser().equals("null") &&
                !forumCreationRequest.getShort_name().equals("null") &&
                !forumCreationRequest.getName().equals("null"));
    }
}
