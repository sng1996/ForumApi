package ru.mail.park.main.controllers.thread;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.thread.ThreadCreationRequest;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by farid on 12.10.16.
 */
@RestController
public class ThreadController extends Controller {

    @RequestMapping(path = "/db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity createThread(@RequestBody ThreadCreationRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            final Integer forumId = ForumQueries.getForumIdByShortName(body.getForum());
            final Integer userId = UserQueries.getUserIdByEmail(body.getUser());

            Database.update(ThreadQueries.createThreadQuery(body, userId, forumId));
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "db/api/user/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam("related") ArrayList<String> related,
                                      @RequestParam("forum") String shortName) {
        if (shortName.isEmpty()) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        try {
            ForumInfoResponse response = null;

            if (!related.equals("user"))
                response = ForumQueries.getForumInfoByShortName(shortName, false);
            else
                response = ForumQueries.getForumInfoByShortName(shortName, true);

            if (response == null) {
                return ResponseEntity.ok().body(
                        ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
            }

            System.out.println(response.responsify());

            return ResponseEntity.ok().body(response.responsify());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }
}
