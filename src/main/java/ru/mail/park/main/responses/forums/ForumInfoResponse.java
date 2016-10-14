package ru.mail.park.main.responses.forums;

import ru.mail.park.main.responses.Response;
import ru.mail.park.main.responses.user.UserInfoResponse;

/**
 * Created by farid on 14.10.16.
 */
public class ForumInfoResponse extends Response {
    private int id;
    private String name;
    private String short_name;
    private UserInfoResponse user;

    public ForumInfoResponse() {
    }

    public ForumInfoResponse(int id, String name, String short_name, UserInfoResponse user) {
        this.id = id;
        this.name = name;
        this.short_name = short_name;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setId(int id) {
        this.id = id - 1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }
}
