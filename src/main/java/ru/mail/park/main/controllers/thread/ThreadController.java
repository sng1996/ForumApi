package ru.mail.park.main.controllers.thread;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.thread.ThreadCreationRequest;

import java.sql.SQLDataException;
import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
@RestController
public class ThreadController extends Controller {

    @RequestMapping(path = "/db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity createThread(@RequestBody ThreadCreationRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        final Integer forumId = ForumQueries.getForumIdByShortName(body.getForum());
        final Integer userId = UserQueries.getUserIdByEmailQuery(body.getUser());

        if (forumId == null ||
                userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            Database.update(ThreadQueries.createThreadQuery(body, userId, forumId));
            return ResponseEntity.ok().body(body.responsify());
        } catch (DbException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }
}
