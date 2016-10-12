package ru.mail.park.main.requests.forum;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.requests.Request;

/**
 * Created by farid on 10.10.16.
 */
public class ForumCreationRequest extends Request {
    private String name;
    private String short_name;
    private String user;
    private int id;

    public ForumCreationRequest() {
    }

    public ForumCreationRequest(String name, String short_name, String user, int id) {
        this.name = name;
        this.short_name = short_name;
        this.user = user;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getUser() {
        return user;
    }

    public int getId() {
        return id;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setId(String short_name) {
        this.short_name = short_name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setId(int id) {
        this.id = id;
    }
}
