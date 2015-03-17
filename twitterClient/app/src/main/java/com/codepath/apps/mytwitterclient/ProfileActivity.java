package com.codepath.apps.mytwitterclient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.mytwitterclient.fragments.FragmentUserTimeline;
import com.codepath.apps.mytwitterclient.models.TweetModelResponse;
import com.codepath.apps.mytwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {
    TwitterClient tw_client;
    String screenName;
    TweetModelResponse tweetModelResponse;

    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_profile);

        tweetModelResponse = new TweetModelResponse();
        tweetModelResponse.setRequireLocalCache(false);

        rl = (RelativeLayout) findViewById(R.id.rlProfile);

        showProgressBar();

        screenName = getIntent().getStringExtra("screen_name");
        if (screenName != null) {
            getSupportActionBar().setTitle("@" + screenName);
        }
        else {
            // empty it for now, we will fill later
            getSupportActionBar().setTitle("");
        }
//        Toast.makeText(this, "Profile for: "+screenName+"..", Toast.LENGTH_SHORT).show();

//        setupViews();
        showUserProfileHeader();
        showUserTimeline();
    }

    // Should be called manually when an async task has started
    public void showProgressBar() {
//        setProgressBarIndeterminateVisibility(true);

        rl.setVisibility(View.INVISIBLE);
    }

    // Should be called when an async task has finished
    public void hideProgressBar() {
//        setProgressBarIndeterminateVisibility(false);
        rl.setVisibility(View.VISIBLE);
    }



    private void showUserProfileHeader() {
        tw_client = TwitterApplication.getRestClient();
        if (screenName == null) {
            // No screen name provided, get logged in user details.
            tw_client.getUserInfo(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.i("Timeline", "JSON Resp: " + response);
                    User user = User.fromJSON(response, tweetModelResponse);
                    populateUserProfileInfo(user);
//                addAll(tmr.getlTweets());
//                sdRefresh.setRefreshing(false);
                    getSupportActionBar().setTitle("@"+user.getScreenName());
                    hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                sdRefresh.setRefreshing(false);
//                TwitterUtils.handleReqFailure(this, tw_client, errorResponse, new JsonErrorHttpResponseHandler(getApplicationContext()));
                    hideProgressBar();
                }
            });
        }
        else {
            // screen name provided, get that users details
            tw_client.getUserDetails(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.i("Timeline", "JSON Resp: " + response);
                    User user = User.fromJSON(response, tweetModelResponse);
                    populateUserProfileInfo(user);
//                addAll(tmr.getlTweets());
//                sdRefresh.setRefreshing(false);
                    hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                sdRefresh.setRefreshing(false);
//                TwitterUtils.handleReqFailure(this, tw_client, errorResponse, new JsonErrorHttpResponseHandler(getApplicationContext()));
                    hideProgressBar();
                }
            });
        }
    }

    public void populateUserProfileInfo(User user) {
        TextView tvUserName = (TextView) findViewById(R.id.tvName);
        TextView tvDesciption = (TextView) findViewById(R.id.tvDesciption);
        TextView tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);
        ImageView ivHeader = (ImageView) findViewById(R.id.ivProfileHeader);

        Picasso.with(this)
                .load(user.getProfile_image_url())
                .transform(transformation)
                .into(ivProfile);
        tvUserName.setText(user.getUsername());
        tvDesciption.setText(user.getTagLine());
        tvFollowersCount.setText(Integer.toString(user.getFollowersCount()));
        tvFollowingCount.setText(Integer.toString(user.getFollowingCount()));

        Picasso.with(this)
                .load(user.getProfile_header_url())
                .into(ivHeader);

    }

    Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(0)
            .cornerRadiusDp(2)
            .oval(false)
            .build();


    private void showUserTimeline() {
        // get screen name

        FragmentUserTimeline fragmentUserTimeline = FragmentUserTimeline.newInstance(screenName);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flTweets, fragmentUserTimeline);
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onUserProfileImageClicked(View v) {
//        Toast.makeText(this, "Got: " + v.getTag() + "..", Toast.LENGTH_SHORT).show();
    }
}
