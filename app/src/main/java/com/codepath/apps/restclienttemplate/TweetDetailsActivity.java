package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);

    Tweet tweet;

    ImageView ivProfileImage;
    ImageView ivMedia;
    TextView tvScreenName;
    TextView tvName;
    TextView tvBody;
    TextView tvCreatedAt;

    TextView tvRetweetCount;
    TextView tvLikeCount;

    ImageButton btnReply;
    ImageButton ibLikeEmpty;
    ImageButton ibLike;

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

        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);

        btnReply = findViewById(R.id.btnReply);
        ibLikeEmpty = findViewById(R.id.ibLikeEmpty);
        ibLike = findViewById(R.id.ibLike);

        // HELP: how to check if the tweet is currently liked by the user?

        ibLike.setVisibility(View.GONE);

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ibLikeEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.likeTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Liked tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Like tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibLikeEmpty.setVisibility(View.GONE);
                ibLike.setVisibility(View.VISIBLE);
            }
        });
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.unlikeTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Unliked tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Unlike tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibLike.setVisibility(View.GONE);
                ibLikeEmpty.setVisibility(View.VISIBLE);
            }
        });

        // unwrap the tweet passed in via intent, using its simple name as a key
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvScreenName.setText("@" + tweet.user.screenName);
        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvCreatedAt.setText(tweet.createdAt);

        tvRetweetCount.setText(tweet.retweetCount + " Retweets");
        tvLikeCount.setText(tweet.likeCount + " Likes");

        if(tweet.mediaUrl != null) {
            Glide.with(this).load(tweet.mediaUrl).into(ivMedia);
        }
        else {
            ivMedia.setVisibility(View.GONE);
        }

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);

    }

}