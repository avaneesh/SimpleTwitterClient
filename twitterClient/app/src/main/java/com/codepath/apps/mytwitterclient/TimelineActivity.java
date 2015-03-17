package com.codepath.apps.mytwitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mytwitterclient.fragments.ComposeFragment;
import com.codepath.apps.mytwitterclient.fragments.FragmentHomeTimeline;
import com.codepath.apps.mytwitterclient.fragments.FragmentMentionsTimeline;
import com.codepath.apps.mytwitterclient.fragments.FragmentTimeline;
import com.codepath.apps.mytwitterclient.models.Tweet;
import com.codepath.apps.mytwitterclient.models.TweetModelResponse;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

// Using Fragments
public class TimelineActivity extends ActionBarActivity implements ComposeFragment.OnFragmentInteractionListener {

    TwitterClient tw_client;

    FragmentTimeline fragmentTimeline;

    FragmentHomeTimeline homeTimelineFragment;

    //ViewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // access the static fragment
        if (savedInstanceState == null) {
            //fragmentTimeline = (FragmentTimeline) getSupportFragmentManager().findFragmentById(R.id.fContainer);
        }

        tw_client = TwitterApplication.getRestClient();
        homeTimelineFragment = new FragmentHomeTimeline();

        // get view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        // set adapter to view pager
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // find sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // attach sliding tabstrip to view pager
        tabStrip.setViewPager(viewPager);
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
        else if (id == R.id.action_profile) {
            showUserProfile();
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
                Tweet t = Tweet.fromJSON(response, new TweetModelResponse());
                // todo: insert into correct fragment ???
//                FragmentHomeTimeline.insertItemInto(t);
                homeTimelineFragment.insertItem(t);
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

    // return order of fragments into view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private final String TAB_TITLES[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return homeTimelineFragment;
            } else if (position == 1){
                return new FragmentMentionsTimeline();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }
    }

    public void showUserProfile() {
        if (TwitterUtils.isNetworkAvailable(this)) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Sorry, no internet connection..", Toast.LENGTH_SHORT).show();
        }
    }


    public void onUserProfileImageClicked(View v) {
//        Toast.makeText(this, "Got: "+v.getTag()+"..", Toast.LENGTH_SHORT).show();
        if (TwitterUtils.isNetworkAvailable(this)) {
            Intent i = new Intent(this, ProfileActivity.class);
            Bundle args = new Bundle();
            args.putString("screen_name", v.getTag().toString());
            i.putExtras(args);
            startActivity(i, args);
        }
        else {
            Toast.makeText(this, "Sorry, no internet connection..", Toast.LENGTH_SHORT).show();
        }
    }

}
