package ru.mail.park.main.requests.user;

import ru.mail.park.main.requests.Request;

import javax.validation.constraints.NotNull;

/**
 * Created by farid on 17.10.16.
 */
public class UserFollowRequest extends Request {
    @NotNull
    String follower;

    @NotNull
    String followee;

    public UserFollowRequest() {
    }

    public UserFollowRequest(String follower, String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public String getFollower() {
        return follower;
    }

    public String getFollowee() {
        return followee;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public void setFollowee(String followee) {
        this.followee = followee;
    }
}
