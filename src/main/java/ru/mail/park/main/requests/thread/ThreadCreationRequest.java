package ru.mail.park.main.requests.thread;

import ru.mail.park.main.requests.Request;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("unused")
public class ThreadCreationRequest extends Request {

    private String forum;
    private String title;
    private boolean isClosed;
    private String user;
    private String date;
    private String message;
    private String slug;
    private boolean isDeleted = false;

    public ThreadCreationRequest(String forum, String title, boolean isClosed, String user,
                                 String date, String message, String slug, boolean isDeleted) {
        this.forum = forum;
        this.title = title;
        this.isClosed = isClosed;
        this.user = user;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.isDeleted = isDeleted;
    }

    public ThreadCreationRequest() {

    }

    public String getForum() {
        return forum;
    }

    public String getTitle() {
        return title;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
