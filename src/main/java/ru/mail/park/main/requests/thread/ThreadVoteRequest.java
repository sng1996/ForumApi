package ru.mail.park.main.requests.thread;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 17.10.16.
 */
public class ThreadVoteRequest extends Request {
    @NotNull
    Integer vote;

    @NotNull
    Integer thread;


    public ThreadVoteRequest() {
    }

    public ThreadVoteRequest(Integer vote, Integer thread) {
        this.vote = vote;
        this.thread = thread;
    }

    public Integer getVote() {
        return vote;
    }

    public Integer getThread() {
        return thread;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }
}
