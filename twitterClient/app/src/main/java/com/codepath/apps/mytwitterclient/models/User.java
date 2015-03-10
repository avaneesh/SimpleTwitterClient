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

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    /*
        Username - user -> name
        User profile - user -> profile_image_url
     */
    public static User fromJSON(JSONObject jsonObject){
        User user = new User();

        try {
            user.username = jsonObject.getString("name");
            user.profile_image_url = jsonObject.getString("profile_image_url");

            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
