package com.codepath.apps.mytwitterclient.fragments;

import android.os.Bundle;

import com.codepath.apps.mytwitterclient.JsonErrorHttpResponseHandler;
import com.codepath.apps.mytwitterclient.TwitterUtils;
import com.codepath.apps.mytwitterclient.models.Tweet;
import com.codepath.apps.mytwitterclient.models.TweetModelResponse;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by avkadam on 3/15/15.
 */
public class FragmentMentionsTimeline extends FragmentTimeline {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweetModelResponse.setRequireLocalCache(false);
    }

    @Override
    public void fetchTimeline() {

        tw_client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.i("Timeline", "JSON Resp: " + response);
                TweetModelResponse tmr = Tweet.fromJSONArray(response, tweetModelResponse);
                addAll(tmr.getlTweets());
                sdRefresh.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                sdRefresh.setRefreshing(false);
                TwitterUtils.handleReqFailure(getActivity(), tw_client, errorResponse, new JsonErrorHttpResponseHandler(getActivity()));
            }
        }, tweetModelResponse.getCurrent_max_id());
    }
}
