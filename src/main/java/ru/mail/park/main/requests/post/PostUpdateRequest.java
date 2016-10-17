package ru.mail.park.main.requests.post;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 16.10.16.
 */
public class PostUpdateRequest extends Request{
    @NotNull
    Integer post;

    @NotNull
    String message;

    public PostUpdateRequest(Integer post, String message) {
        this.post = post;
        this.message = message;
    }

    public PostUpdateRequest() {
    }

    public Integer getPost() {
        return post;
    }

    public String getMessage() {
        return message;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
