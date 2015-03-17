package com.codepath.apps.mytwitterclient.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mytwitterclient.R;
import com.codepath.apps.mytwitterclient.TwitterUtils;
import com.codepath.apps.mytwitterclient.models.Tweet;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by avkadam on 3/7/15.
 */
public class TimelineAdapter extends ArrayAdapter<Tweet> {
    public TimelineAdapter(Context context, List<Tweet> objects) {
        super(context, R.layout.item_tweet, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

            holder = new ViewHolder();
            holder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            holder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            holder.tvRDate = (TextView) convertView.findViewById(R.id.tvRDate);
            holder.ivMedia = (ImageView) convertView.findViewById(R.id.ivMedia);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        Tweet tweet = getItem(position);

        // Load Profile image using Picasso
        holder.ivProfile.setImageResource(0);
        if (tweet.getUser() != null) {
            Picasso.with(getContext())
                    .load(tweet.getUser().getProfile_image_url())
                    .transform(transformation)
                    .into(holder.ivProfile);

            holder.tvUserName.setText(tweet.getUser().getUsername());
            holder.ivProfile.setTag(tweet.getUser().getScreenName());
        }
        else {
            holder.tvUserName.setText("n/a");
        }
        holder.tvBody.setText(Html.fromHtml(tweet.getBody()));
        holder.tvRDate.setText(TwitterUtils.getRelativeTimeAgo(tweet.getCreated_at()));

        // check for media
        if (tweet.getMedia_type().equals("photo")){
//            Log.e("Media", "Loading media: " + tweet.getMedia_url());
//            Log.e("Media", "Loading Text is: " + tweet.getBody());
            holder.ivMedia.setVisibility(View.VISIBLE);
            Picasso.with(getContext())
                    .load(tweet.getMedia_url())
                    .into(holder.ivMedia);

        }
        else {
//            Log.e("Media", "Not loading media: " + tweet.getMedia_url()+" Type:"+tweet.getMedia_type());
            holder.ivMedia.setVisibility(View.GONE);
        }

        return convertView;
    }

    public class ViewHolder {
        TextView tvUserName;
        TextView tvBody;
        TextView tvRDate;
        ImageView ivProfile;
        ImageView ivMedia;
    }

    Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(0)
            .cornerRadiusDp(2)
            .oval(false)
            .build();

}
