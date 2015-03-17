package com.codepath.apps.mytwitterclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by avkadam on 3/7/15.
 */
public class TwitterUtils {

    public static final int ERR_CODE_RATE_LIMITED=88;

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String outRelativeDate ="";
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String date_info[] = relativeDate.split("\\s+");
        outRelativeDate = relativeDate;

        //override 'outRelativeDate' if it can be shortened further
        if (date_info[0].equals("Yesterday")){
            outRelativeDate = "1d";
        } else if (date_info.length > 1){
            if (date_info[2].equals("ago")) {
                outRelativeDate = date_info[0] + date_info[1].charAt(0);
            }
        }


        return outRelativeDate;
    }

    // in 10 minutes
    public static String getRelativeTimeAfter(long epochFuture){
        String afterTime = DateUtils.getRelativeTimeSpanString(epochFuture*1000,
                                                               System.currentTimeMillis(),
                                                               DateUtils.SECOND_IN_MILLIS).toString();
        return afterTime;
    }

    // If the status code indicates that the request was rate limited,
    // find out the remaining time and show it to user.
    public static void handleReqFailure(Context c, TwitterClient tw_client, JSONObject errorJSON, JsonErrorHttpResponseHandler handler) {
        //{ "errors": [ { "code": 88, "message": "Rate limit exceeded" } ] }

        try {
            String errorMessage = errorJSON.getJSONArray("errors").getJSONObject(0).getString("message");
            int errorCode = errorJSON.getJSONArray("errors").getJSONObject(0).getInt("code");


            if (errorCode == ERR_CODE_RATE_LIMITED){
                // Get time limits.
                tw_client.getRateLimits(handler);
            }
            else {
                Toast.makeText(c, "JSON req Failed!! Reason: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}

