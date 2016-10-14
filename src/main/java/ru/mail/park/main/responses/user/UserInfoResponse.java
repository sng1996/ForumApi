package ru.mail.park.main.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.mail.park.main.requests.Request;
import ru.mail.park.main.responses.Response;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by farid on 12.10.16.
 */
public class UserInfoResponse extends Response {

    private String username;
    private String about;
    private String name;
    private String email;

    @JsonProperty(value = "isAnonymous")
    private boolean isAnonymous = false;
    private int id = 0;
    private List<String> followers;
    private List<String> following;
    private List<Integer> subscriptions;

    public UserInfoResponse() {
        followers = new ArrayList<>();
        following = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public UserInfoResponse(String username, String about, String name, String email, boolean isAnonymous,
                            int id, List<String> followers, List<String> following, List<Integer> subscriptions) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.isAnonymous = isAnonymous;
        this.id = id;
        this.followers = followers;
        this.following = following;
        this.subscriptions = subscriptions;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public int getId() {
        return id;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public List<Integer> getSubscriptions() {
        return subscriptions;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public void setId(int id) {
        this.id = id - 1;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void setSubscriptions(List<Integer> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void addFollower (String follower) {
        followers.add(follower);
    }

    public void addFollowee (String followee) {
        following.add(followee);
    }

    public void addSubscription (Integer subscription) {
        subscriptions.add(subscription);
    }
}
