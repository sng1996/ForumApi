package ru.mail.park.main;

/**
 * Created by farid on 10.10.16.
 */
public class ErrorCodes {
    public static final int OK = 0;
    public static final int OBJECT_NOT_FOUND    = 1;
    public static final int INVALID_REQUEST     = 2;
    public static final int INCORRECT_REQUEST   = 3;
    public static final int UNKNOWN_ERROR       = 4;
    public static final int USER_ALREADY_EXISTS = 5;

    public static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case 0: return "Success";
            case 1: return "Object not found";
            case 2: return "Invalid request";
            case 3: return "Incorrect request";
            case 4: return "Unknown error";
            case 5: return "User Already exists";
            default: return "Unknown error code";
        }
    }

    public static String codeToJson(int errorCode) {
        return "{\"code\":\"" + errorCode + "\",\"response\":\"" + getErrorMessage(errorCode) + "\"}";
    }
}
