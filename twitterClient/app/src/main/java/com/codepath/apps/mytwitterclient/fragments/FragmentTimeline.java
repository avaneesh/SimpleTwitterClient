package com.codepath.apps.mytwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.mytwitterclient.Adapters.TimelineAdapter;
import com.codepath.apps.mytwitterclient.EndlessScrollListener;
import com.codepath.apps.mytwitterclient.R;
import com.codepath.apps.mytwitterclient.TwitterApplication;
import com.codepath.apps.mytwitterclient.TwitterClient;
import com.codepath.apps.mytwitterclient.TwitterUtils;
import com.codepath.apps.mytwitterclient.models.Tweet;
import com.codepath.apps.mytwitterclient.models.TweetModelResponse;
import com.codepath.apps.mytwitterclient.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avkadam on 3/14/15.
 */
public class FragmentTimeline extends Fragment {

    ListView lvTweets;
    TimelineAdapter aTweets;
    ArrayList<Tweet> lTweets;
    SwipeRefreshLayout sdRefresh;

    TweetModelResponse tweetModelResponse;

    public static final int FIRST_PAGE = 1;

    TwitterClient tw_client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tw_client = TwitterApplication.getRestClient();
        tweetModelResponse = new TweetModelResponse();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_timeline, container, false);

        setupViews(v);

        fetchTweets(FIRST_PAGE);

        return v;
    }

    private void setupViews(View parent) {
        lvTweets = (ListView) parent.findViewById(R.id.lvTweets);
        lTweets = new ArrayList();
        aTweets = new TimelineAdapter(getActivity(), lTweets);
        lvTweets.setAdapter(aTweets);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                fetchTweets(page);
                Log.e("LoadMore", "===== Done loading more...");
            }
        });

        sdRefresh = (SwipeRefreshLayout) parent.findViewById(R.id.swipeContainer);
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

    private void fetchTweets(int page) {
        boolean isNetworkActive = TwitterUtils.isNetworkAvailable(getActivity());
        if (page == FIRST_PAGE){
            // clear if its a new page that user is requesting
            Log.e("Timeline", "Cleanup for first page");
            aTweets.clear();

            // new response holder
            tweetModelResponse.resetAll();

            if (!isNetworkActive) {
                // load from local storage
                if (tweetModelResponse.isRequireLocalCache()) {
                    Toast.makeText(getActivity(), "No internet, connecting from local cache..", Toast.LENGTH_SHORT).show();
                    aTweets.addAll(Tweet.getAll());
                }
                else {
                    Toast.makeText(getActivity(), "Sorry, no internet connection..", Toast.LENGTH_SHORT).show();
                }
                sdRefresh.setRefreshing(false);
                return;
            }
            else {
                // invalidate the cache
//                Toast.makeText(getActivity(), "Back online.", Toast.LENGTH_SHORT).show();
                if (tweetModelResponse.isRequireLocalCache()) {
                    new Delete().from(Tweet.class).execute();
                    new Delete().from(User.class).execute();
                }
            }
        }
        else {
            if (!isNetworkActive) {
                // next page, nothing can be done, sorry!!
                return;
            }
        }
        // only reset list
        tweetModelResponse.reset();
        // set tweetModelResponse before this.
        fetchTimeline();
    }

//    abstract public void fetchHomeTimeline();
    public void fetchTimeline() {
        Log.e("NOTHING", "should not hit this");
        // caller should override this.
    }


    public void insertItem(Tweet t) {
        aTweets.insert(t, 0);
        aTweets.notifyDataSetChanged();
    }

    public void addAll (List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }
}
