package com.codepath.apps.mytwitterclient.models;

import java.util.ArrayList;

/**
 * Created by avkadam on 3/15/15.
 */
public class TweetModelResponse {
    private ArrayList<Tweet> lTweets;
    private long current_max_id;
    private boolean requireLocalCache;

    public TweetModelResponse() {
        this.lTweets = new ArrayList<>();
        this.current_max_id = 0;
        this.requireLocalCache = false;
    }

    public ArrayList<Tweet> getlTweets() {
        return lTweets;
    }

    public void setlTweets(ArrayList<Tweet> lTweets) {
        this.lTweets = lTweets;
    }

    public long getCurrent_max_id() {
        return current_max_id;
    }

    public void setCurrent_max_id(long current_max_id) {
        this.current_max_id = current_max_id;
    }

    public void reset() {
        lTweets.clear();
    }

    public boolean isRequireLocalCache() {
        return requireLocalCache;
    }

    public void setRequireLocalCache(boolean requireLocalCache) {
        this.requireLocalCache = requireLocalCache;
    }

    public void resetAll() {
        lTweets.clear();
        current_max_id = Long.MAX_VALUE;
    }
}
