package ru.mail.park.main.requests.thread;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 17.10.16.
 */
public class ThreadUpdateRequest extends Request {
    @NotNull
    String message;

    @NotNull
    String slug;

    @NotNull
    Integer thread;


    public ThreadUpdateRequest() {
    }

    public ThreadUpdateRequest(String message, String slug, Integer thread) {
        this.message = message;
        this.slug = slug;
        this.thread = thread;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getThread() {
        return thread;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }
}
