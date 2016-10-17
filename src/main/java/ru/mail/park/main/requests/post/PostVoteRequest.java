package ru.mail.park.main.requests.post;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 16.10.16.
 */
public class PostVoteRequest extends Request{

    @NotNull
    Integer post;

    @NotNull
    Integer vote;

    public PostVoteRequest(Integer post, Integer vote) {
        this.post = post;
        this.vote = vote;
    }

    public PostVoteRequest() {
    }

    public Integer getPost() {
        return post;
    }

    public Integer getVote() {
        return vote;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
