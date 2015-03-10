package com.codepath.apps.mytwitterclient;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.mytwitterclient.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Twitter oath class
	public static final String REST_URL = "https://api.twitter.com/1.1"; // base API URL
	public static final String REST_CONSUMER_KEY = "wDZL7I5gaH7BoTkpH9juk04Lq";
	public static final String REST_CONSUMER_SECRET = "bO2KfJf293LBObekxOv9VMgGeDmdKTUkhKe6QsRgLGqX2VdBnl";
	public static final String REST_CALLBACK_URL = "oauth://avktwitterclient"; // match it in manifest

    public static final int TWEETS_PER_PAGE = 25;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

    // Home timeline
    public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        // todo: try adding it in request URL directly.
        params.put("count", TWEETS_PER_PAGE);
        int since_id = ((page - 1)*TWEETS_PER_PAGE)+1;
        //params.put("since_id", ((page - 1)*TWEETS_PER_PAGE)+1);
        long prev_max_id = Tweet.max_id -1;
        params.put("max_id", prev_max_id);
        Log.e("Twitter Client", "Sending out network request(max id 1 - " + prev_max_id + "): " + apiUrl);
        getClient().get(apiUrl, params, handler);
    }

    // Rate limits
    // https://dev.twitter.com/rest/public/rate-limiting
    public void getRateLimits( AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("application/rate_limit_status.json");
        RequestParams params = new RequestParams();
        params.put("resources", "statuses"); //help,users,search,statuses
        Log.e("Twitter Client", "Sending out network request:" + apiUrl);
        getClient().get(apiUrl, params, handler);
    }

    // Compose
    // https://dev.twitter.com/rest/reference/post/statuses/update
    // Params: status (body)
    // Response gives the same tweet back.
    public void postCompose(String body, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", body); //help,users,search,statuses
        Log.e("New Tweet", body);
        Log.e("Twitter Client", "Sending out network request: " + apiUrl);
        getClient().post(apiUrl, params, handler);
    }
}