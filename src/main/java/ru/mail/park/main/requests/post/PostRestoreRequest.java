package ru.mail.park.main.requests.post;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 16.10.16.
 */
public class PostRestoreRequest extends Request{
    @NotNull
    Integer post;

    public PostRestoreRequest(Integer post) {
        this.post = post;
    }

    public PostRestoreRequest() {
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }
}
