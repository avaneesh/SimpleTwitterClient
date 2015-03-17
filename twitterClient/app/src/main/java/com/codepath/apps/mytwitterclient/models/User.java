package com.codepath.apps.mytwitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by avkadam on 3/7/15.
 */
@Table (name = "users")
public class User extends Model {

    @Column (name = "username")
    private String username;

    @Column (name = "profile_image_url")
    private String profile_image_url;

    private String profile_header_url;

    private String tagLine;
    private int followingCount;
    private int followersCount;
    private String screenName;


    public User() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfile_header_url() {
        return profile_header_url;
    }

    public void setProfile_header_url(String profile_header_url) {
        this.profile_header_url = profile_header_url;
    }

    /*
                Username - user -> name
                User profile - user -> profile_image_url
             */
    public static User fromJSON(JSONObject jsonObject, TweetModelResponse tweetModelResponse){
        User user = new User();

        try {
            user.username = jsonObject.getString("name");
            user.profile_image_url = jsonObject.getString("profile_image_url");
            user.followersCount = jsonObject.getInt("followers_count");
            user.followingCount = jsonObject.getInt("friends_count");
            user.tagLine = jsonObject.getString("description");
            user.screenName = jsonObject.getString("screen_name");
            user.profile_header_url = jsonObject.getString("profile_banner_url");


            // todo: ideally we should save only when we are in hometimeline
            if(tweetModelResponse.isRequireLocalCache()) {
                user.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
