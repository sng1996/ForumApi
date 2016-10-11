package ru.mail.park.main.controllers.forum;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.requests.forum.ForumCreationRequest;

/**
 * Created by farid on 10.10.16.
 */

@RestController
public class ForumController {

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody ForumCreationRequest body) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"" + "\"}");
    }
}
