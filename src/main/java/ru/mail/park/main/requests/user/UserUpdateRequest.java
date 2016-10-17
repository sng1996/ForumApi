package ru.mail.park.main.requests.user;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 17.10.16.
 */
public class UserUpdateRequest {
    @NotNull
    String about;

    @NotNull
    String user;

    @NotNull
    String name;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String about, String user, String name) {
        this.about = about;
        this.user = user;
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }
}
