package ru.mail.park.main.controllers.thread;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.ErrorCodes;

/**
 * Created by farid on 12.10.16.
 */
@RestController
public class ThreadController {

    @RequestMapping(path = "/db/api/thread/create")
    public ResponseEntity createThread() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
    }
}
