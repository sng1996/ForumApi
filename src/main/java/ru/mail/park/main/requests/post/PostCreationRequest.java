package ru.mail.park.main.requests.post;

import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.requests.Request;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("unused")
public class PostCreationRequest extends Request {

    private String forum;
    private String user;
    private String date;
    private String message;
    private int parent;
    private boolean isApproved = false;
    private boolean isHighlighted = false;
    private boolean isEdited = false;
    private boolean isSpam = false;
    private boolean isDeleted = false;

    public PostCreationRequest() {

    }

    public PostCreationRequest(String forum, String user, String date, String message, int parent, boolean isApproved,
                               boolean isHighlighted, boolean isEdited, boolean isSpam, boolean isDeleted) {
        this.forum = forum;
        this.user = user;
        this.date = date;
        this.message = message;
        this.parent = parent;
        this.isApproved = isApproved;
        this.isHighlighted = isHighlighted;
        this.isEdited = isEdited;
        this.isSpam = isSpam;
        this.isDeleted = isDeleted;
    }

    public String getForum() {
        return forum;
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

    public int getParent() {
        return parent;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setForum(String forum) {
        this.forum = forum;
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

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
