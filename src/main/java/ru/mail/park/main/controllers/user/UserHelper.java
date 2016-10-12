package ru.mail.park.main.controllers.user;

import ru.mail.park.main.requests.user.UserCreationRequest;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("OverlyComplexBooleanExpression")
public class UserHelper {

    public static boolean nullCheck(UserCreationRequest userCreationRequest) {
        return (userCreationRequest.getAbout() != "null" &&
                userCreationRequest.getEmail() != "null" &&
                userCreationRequest.getName()  != "null" &&
                userCreationRequest.getUsername() != "null");

    }
}
