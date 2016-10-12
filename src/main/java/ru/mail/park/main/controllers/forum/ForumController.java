package ru.mail.park.main.controllers.forum;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.database.DbException;
import ru.mail.park.main.requests.forum.ForumCreationRequest;

import java.sql.ResultSet;

/**
 * Created by farid on 10.10.16.
 */

@RestController
public class ForumController {

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody ForumCreationRequest body) {
        try {
            ResultSet resultSet = Database.select("SELECT userID FROM users WHERE users.email=" + body.getUser());
            Database.update("INSERT INTO forums (name, short_name, userId) VALUES('ass', 'butt', 2)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        } catch (DbException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }
}
