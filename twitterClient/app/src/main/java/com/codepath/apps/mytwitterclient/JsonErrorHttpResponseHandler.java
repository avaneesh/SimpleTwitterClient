package com.codepath.apps.mytwitterclient;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by avkadam on 3/8/15.
 */
public class JsonErrorHttpResponseHandler extends JsonHttpResponseHandler {
        Context c;

        public JsonErrorHttpResponseHandler(Context c) {
            this.c = c;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // "resources" -> "statuses" -> "/statuses/home_timeline" -> "reset(timestamp)"
            // "resources" -> "statuses" -> "/statuses/home_timeline" -> "remaining(int)"
            try {
                JSONObject statusesLimits = response.getJSONObject("resources").getJSONObject("statuses");
                int resetTime = statusesLimits.getJSONObject("/statuses/home_timeline").getInt("reset");
                long remaining = statusesLimits.getJSONObject("/statuses/home_timeline").getLong("remaining");
                String remaining_Time = TwitterUtils.getRelativeTimeAfter(resetTime);
                Toast.makeText(c, "Please try again " + remaining_Time, Toast.LENGTH_LONG).show();
                Log.e("Limit", "Remaining: " + remaining + " Try after: " + resetTime + " - " + remaining_Time);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            // nothing much can be done
        }
}
