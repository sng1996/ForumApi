package ru.mail.park.main.requests.user;

import ru.mail.park.main.requests.Request;

/**
 * Created by farid on 12.10.16.
 */
public class UserCreationRequest extends Request {

    private String username;
    private String about;
    private String name;
    private String email;
    private boolean isAnonymous = false;
    private int id = 0;

    public UserCreationRequest() {

    }

    public UserCreationRequest(String username, String about, String name, String email, boolean isAnonymous, int id) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.isAnonymous = isAnonymous;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public void setId(int id) {
        this.id = id;
    }
}
