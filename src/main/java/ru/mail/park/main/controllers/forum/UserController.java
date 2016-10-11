package ru.mail.park.main.controllers.forum;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.requests.user.UserCreationRequest;

/**
 * Created by farid on 12.10.16.
 */
@RestController
public class UserController {

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public void createUser (@RequestBody UserCreationRequest body) {

    }
}
