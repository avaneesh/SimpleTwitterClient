package com.codepath.apps.mytwitterclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.mytwitterclient.models.Tweet;
import com.codepath.apps.mytwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity implements ComposeFragment.OnFragmentInteractionListener {

    TwitterClient tw_client;
    ListView lvTweets;
    TimelineAdapter aTweets;
    ArrayList<Tweet> lTweets;
    SwipeRefreshLayout sdRefresh;

    public static final int FIRST_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupViews();

        tw_client = new TwitterClient(this);
        fetchTweets(FIRST_PAGE);
    }

    private void fetchTweets(int page) {
        boolean isNetworkActive = isNetworkAvailable();
        if (page == FIRST_PAGE){
            // clear if its a new page that user is requesting
            Log.e("Timeline", "Cleanup for first page");
            aTweets.clear();
            Tweet.max_id = Long.MAX_VALUE;
            if (!isNetworkActive) {
                // load from local storage
                Toast.makeText(this, "No intenet, connecting from local cache..", Toast.LENGTH_SHORT).show();
                aTweets.addAll(Tweet.getAll());
                sdRefresh.setRefreshing(false);
                return;
            }
            else {
                // invalidate the cache
                Toast.makeText(this, "Back online.", Toast.LENGTH_SHORT).show();
                new Delete().from(Tweet.class).execute();
                new Delete().from(User.class).execute();
            }
        }
        else {
            if (!isNetworkActive) {
                // next page, nothing can be done, sorry!!
                return;
            }
        }
        tw_client.getHomeTimeline(page, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.i("Timeline", "JSON Resp: " + response);
                aTweets.addAll(Tweet.fromJSONArray(response));
                sdRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                sdRefresh.setRefreshing(false);
                TwitterUtils.handleReqFailure(getApplicationContext(), tw_client, errorResponse, new JsonErrorHttpResponseHandler(getApplicationContext()));
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private void setupViews() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lTweets = new ArrayList();
        aTweets = new TimelineAdapter(this, lTweets);
        lvTweets.setAdapter(aTweets);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                fetchTweets(page);
            }
        });

        sdRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        sdRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTweets(FIRST_PAGE);
            }
        });

        sdRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_tweet) {
            composeNewTweet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void composeNewTweet() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance();
        composeFragment.show(fm, "fragment_compose");
    }

    @Override
    public void onDoneCompose(String body) {
        Toast.makeText(this, "Got data from compose fragment: "+body, Toast.LENGTH_SHORT).show();
        tw_client.postCompose(body, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getApplicationContext(), "New tweet created", Toast.LENGTH_SHORT).show();
                Tweet t = Tweet.fromJSON(response);
                aTweets.insert(t, 0);
                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                TwitterUtils.handleReqFailure(getApplicationContext(), tw_client, errorResponse, new JsonErrorHttpResponseHandler(getApplicationContext()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Could not tweet!!! Error: "+ statusCode, Toast.LENGTH_LONG).show();
                Log.e("Compose", "Could not tweet!!! Error: "+ statusCode+" Message: "+responseString);
            }
        });
    }

}
