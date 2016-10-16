package ru.mail.park.main.controllers.forum;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.forum.ForumCreationRequest;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by farid on 10.10.16.
 */

@RestController
public class ForumController extends Controller {

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody ForumCreationRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            final Integer userID = UserQueries.getUserIdByEmail(body.getUser());

            if (userID == null) {
                return ResponseEntity.ok().body(
                        ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
            }

            body.setId(Database.update(ForumQueries.createForumQuery(body, userID)));

            //System.out.println(body.responsify());
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "db/api/forum/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam(value = "related", required = false) String related,
                                      @RequestParam("forum") String shortName) {

        if (shortName.isEmpty()) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        try {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode forumInfo;

            if (related == null || !related.equals("user"))
                forumInfo = ForumQueries.getForumInfoByShortName(shortName, false);
            else
                forumInfo = ForumQueries.getForumInfoByShortName(shortName, true);

            ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", forumInfo);

            //System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }
}
