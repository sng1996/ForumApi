package ru.mail.park.main.requests.post;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 16.10.16.
 */
public class PostRemovalRequest extends Request {
    @NotNull
    int post;

    public PostRemovalRequest(int post) {
        this.post = post;
    }

    public PostRemovalRequest() {
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }
}
