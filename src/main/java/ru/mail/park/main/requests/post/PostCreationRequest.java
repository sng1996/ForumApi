package ru.mail.park.main.requests.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("unused")
public class PostCreationRequest extends Request {

    @NotNull
    private String forum;

    @NotNull
    private String user;

    @NotNull
    private String date;

    @NotNull
    private String message;

    @NotNull
    private Integer thread;

    private Integer parent;

    @JsonProperty(value = "isApproved")
    private boolean isApproved = false;

    @JsonProperty(value = "isHighlighted")
    private boolean isHighlighted = false;

    @JsonProperty(value = "isEdited")
    private boolean isEdited = false;

    @JsonProperty(value = "isSpam")
    private boolean isSpam = false;

    @JsonProperty(value = "isDeleted")
    private boolean isDeleted = false;

    private int id;

    public PostCreationRequest() {

    }

    public PostCreationRequest(String forum, String user, String date, String message, Integer thread, Integer parent, boolean isApproved,
                               boolean isHighlighted, boolean isEdited, boolean isSpam, boolean isDeleted, int id) {
        this.forum = forum;
        this.user = user;
        this.date = date;
        this.message = message;
        this.thread = thread;
        this.parent = parent;
        this.isApproved = isApproved;
        this.isHighlighted = isHighlighted;
        this.isEdited = isEdited;
        this.isSpam = isSpam;
        this.isDeleted = isDeleted;
        this.id = id;
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

    public Integer getThread() {
        return thread;
    }

    public Integer getParent() {
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

    public int getId() {
        return id;
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

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public void setParent(Integer parent) {
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

    public void setId(int id) {
        this.id = id;
    }
}
