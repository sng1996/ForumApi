package ru.mail.park.main.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.tools.ToolsQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.user.UserCreationRequest;
import ru.mail.park.main.responses.user.UserInfoResponse;

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
            final Integer currentId = ToolsQueries.getCurrentCount("users");

            if (currentId == null) return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));

            body.setId(currentId);
            Database.update(UserQueries.createUserQuery(body));
            System.out.println(body.responsify());

            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "db/api/user/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam("user") String email) {
        if (email.isEmpty()) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        try {
            final UserInfoResponse response = UserQueries.getUserInfoByEmail(email);

            if (response== null) {
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
