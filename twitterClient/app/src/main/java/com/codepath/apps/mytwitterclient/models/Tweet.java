package com.codepath.apps.mytwitterclient.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avkadam on 3/7/15.
 */
@Table(name = "tweets")
public class Tweet extends Model {
    @Column (name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long tweet_id;

    @Column (name = "body")
    String body;

    @Column (name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    User user;

    @Column (name = "created_at")
    String created_at;

    @Column (name = "media_type")
    String media_type;

    @Column (name = "media_url")
    String media_url;

    //public static long max_id=Long.MAX_VALUE;

    public Tweet() {
        super();
        //this.media_type = "none";
    }

    public long getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(long tweet_id) {
        this.tweet_id = tweet_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    /*
                Tweet body - [x] —> text
                Username - [x] -> user -> name
                User profile image [x] -> user -> profile_image_url

                Media:
                    Type: [x] -> media[x] -> type
                    URL: [x] -> media[x] -> media_url
             */
    public static Tweet fromJSON(JSONObject jsonObject, TweetModelResponse tweetModelResponse) {
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.created_at = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"), tweetModelResponse);
            tweet.tweet_id = jsonObject.getLong("id");
            if (tweet.tweet_id < tweetModelResponse.getCurrent_max_id()) {
                // keep track of lowest id returned.
                // this will be set as max_id for next page request
                tweetModelResponse.setCurrent_max_id(tweet.tweet_id);
                Log.e("ID", "Setting max id to: "+tweetModelResponse.getCurrent_max_id());
            }
            if (jsonObject.getJSONObject("entities").has("media")){
                tweet.media_type = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("type");
                tweet.media_url = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
//                Log.e("media", "Type: "+tweet.media_type+" URL: "+tweet.media_url);
            }
            else {
                tweet.media_type = "none";
                tweet.media_url = "invalid";
//                Log.e("media", "No media detected..");
            }
//            Log.e("ID", "Current ID: "+tweet.tweet_id+" Max ID: "+tweetModelResponse.getCurrent_max_id());
//            Log.e("Dump", jsonObject.toString());

            // Save in SQL db
            if (tweetModelResponse.isRequireLocalCache()) {
                tweet.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }


    public static TweetModelResponse fromJSONArray(JSONArray jsonArray, TweetModelResponse tweetModelResponse){
        ArrayList<Tweet> lTweet = tweetModelResponse.getlTweets();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet t = Tweet.fromJSON(jsonArray.getJSONObject(i), tweetModelResponse);
                lTweet.add(t);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tweetModelResponse;
    }

    // get from local cache
    public static List<Tweet> getAll() {
        return new Select()
                .from(Tweet.class)
                .orderBy("tweet_id DESC")
                .execute();
    }
}
