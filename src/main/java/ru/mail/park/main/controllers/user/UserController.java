package ru.mail.park.main.controllers.user;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.user.UserCreationRequest;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Created by farid on 12.10.16.
 */
@RestController
public class UserController extends Controller {

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity createUser (@RequestBody UserCreationRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            Database.update(UserQueries.createUserQuery(body));
            return ResponseEntity.ok().body(body.toJson());
        } catch (DbException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }
}
