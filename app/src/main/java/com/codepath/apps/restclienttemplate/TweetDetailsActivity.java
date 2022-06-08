package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;

    ImageView ivProfileImage;
    ImageView ivMedia;
    TextView tvScreenName;
    TextView tvName;
    TextView tvBody;
    TextView tvCreatedAt;

    TextView tvRetweetCount;
    TextView tvQuoteCount;
    TextView tvLikeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // resolve the view objects
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvBody = findViewById(R.id.tvBody);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);

        /*
        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvQuoteCount = findViewById(R.id.tvQuoteCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);

         */

        // unwrap the tweet passed in via intent, using its simple name as a key
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvScreenName.setText("@" + tweet.user.screenName);
        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvCreatedAt.setText(tweet.createdAt);

        /*

        tvRetweetCount.setText(tweet.retweetCount + " Retweets");
        tvQuoteCount.setText(tweet.quoteCount + " Quote Tweets");

         */

        if(tweet.mediaUrl != null) {
            Glide.with(this).load(tweet.mediaUrl).into(ivMedia);
        }
        else {
            ivMedia.setVisibility(View.GONE);
        }

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);

    }
}