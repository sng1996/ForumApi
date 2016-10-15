package ru.mail.park.main.controllers.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.tools.ToolsQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.user.UserCreationRequest;

import java.sql.SQLException;

/**
 * Created by farid on 12.10.16.
 */
@RestController
public class UserController extends Controller {

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity createUser (@RequestBody UserCreationRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            body.setId(Database.update(UserQueries.createUserQuery(body)));

            //System.out.println(body.responsify());

            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "db/api/user/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam("user") String email) {

        ObjectMapper mapper = new ObjectMapper();

        if (email.isEmpty()) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        try {
            final ObjectNode userInfo = UserQueries.getUserInfoByEmail(email);

            ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userInfo);

            //System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }
}
