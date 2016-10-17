package ru.mail.park.main.requests.thread;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 17.10.16.
 */
public class ThreadSubscriptionRequest extends Request {
    @NotNull
    String user;

    @NotNull
    Integer thread;

    public ThreadSubscriptionRequest() {
    }

    public ThreadSubscriptionRequest(String user, Integer thread) {
        this.user = user;
        this.thread = thread;
    }

    public String getUser() {
        return user;
    }

    public Integer getThread() {
        return thread;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }
}
