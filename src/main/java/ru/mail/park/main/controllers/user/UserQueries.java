package ru.mail.park.main.controllers.user;

import ru.mail.park.main.requests.user.UserCreationRequest;

/**
 * Created by farid on 12.10.16.
 */
public class UserQueries {

    public static String createUserQuery(UserCreationRequest userCreationRequest) {
        return "INSERT INTO users (username, about, name, email, isAnonymous) VALUES ('" +
                userCreationRequest.getUsername() + "', '" +
                userCreationRequest.getAbout() + "', '" +
                userCreationRequest.getName() + "', '" +
                userCreationRequest.getEmail() + "', '" +
                (userCreationRequest.isAnonymous() ? 1 : 0) +
                "')";
    }
}
