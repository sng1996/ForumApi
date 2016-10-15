package ru.mail.park.main.controllers.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

            body.setId(Database.update(ThreadQueries.createThreadQuery(body, userId, forumId)));

            //System.out.println(body.responsify());

            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "db/api/thread/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam("related") ArrayList<String> related,
                                      @RequestParam("thread") Integer threadId) {

        if (threadId == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        try {
            final ObjectMapper mapper = new ObjectMapper();

            final ObjectNode threadInfo = ThreadQueries.getThreadInfoById(threadId);

            if (threadInfo == null) {
                return ResponseEntity.ok().body(
                        ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
            }

            if (related.contains("user")) threadInfo.set("user",
                    UserQueries.getUserInfoByEmail(threadInfo.get("user").asText()));

            //FIXME: false stands for not including user into forum (bug-prone)
            if (related.contains("forum")) threadInfo.set("forum",
                    ForumQueries.getForumInfoByShortName(threadInfo.get("forum").asText(), false));

            final ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", threadInfo);
            //System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }
}
