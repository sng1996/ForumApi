package ru.mail.park.main.controllers.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.thread.ThreadQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.post.PostCreationRequest;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("OverlyComplexBooleanExpression")
@RestController
public class PostController extends Controller {

    @RequestMapping(path = "/db/api/post/create", method = RequestMethod.POST)
    public ResponseEntity createPost(@RequestBody PostCreationRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        final Integer forumId = ForumQueries.getForumIdByShortName(body.getForum());
        final Integer userId = UserQueries.getUserIdByEmailQuery(body.getUser());
        final Integer postId = PostQueries.getPostById(body.getParent());
        final Integer threadId = ThreadQueries.getThreadById(body.getThread());

        if (forumId == null    ||
                userId == null ||
                postId == null ||
                threadId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        try {
            Database.update(PostQueries.createPostQuery(body, threadId, userId, forumId, postId));
            return ResponseEntity.ok().body(body.responsify());
        } catch (DbException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR)
            );
        }
    }
}
