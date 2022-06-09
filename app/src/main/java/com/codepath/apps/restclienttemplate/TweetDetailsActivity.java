package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

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
    ImageButton ibRetweetEmpty;
    ImageButton ibRetweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        Context context = this;
        TwitterClient client = TwitterApp.getRestClient(this);

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
        ibRetweetEmpty = findViewById(R.id.ibRetweetEmpty);
        ibRetweet = findViewById(R.id.ibRetweet);

        // unwrap the tweet passed in via intent, using its simple name as a key
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        if(tweet.favorited) {
            ibLike.setVisibility(View.VISIBLE);
            ibLikeEmpty.setVisibility(View.GONE);
        }
        else {
            ibLike.setVisibility(View.GONE);
            ibLikeEmpty.setVisibility(View.VISIBLE);
        }

        if(tweet.retweeted) {
            ibRetweet.setVisibility(View.VISIBLE);
            ibRetweetEmpty.setVisibility(View.GONE);
        }
        else {
            ibRetweet.setVisibility(View.GONE);
            ibRetweetEmpty.setVisibility(View.VISIBLE);
        }

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start reply activity
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("original_author", tweet.user.screenName);
                context.startActivity(intent);
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

                tweet.likeCount++;
                tweet.favorited = true;
                tvLikeCount.setText(tweet.likeCount + " Likes");

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

                tweet.likeCount--;
                tweet.favorited = false;
                tvLikeCount.setText(tweet.likeCount + " Likes");
            }
        });
        ibRetweetEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.retweetTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Retweeted tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Retweet tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibRetweetEmpty.setVisibility(View.GONE);
                ibRetweet.setVisibility(View.VISIBLE);

                tweet.retweetCount++;
                tweet.retweeted = true;
                tvRetweetCount.setText(tweet.retweetCount + " Retweets");

            }
        });
        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.unretweetTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("DEBUG", "Unretweeted tweet");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("DEBUG", "Unretweet tweet error: " + throwable.toString());
                    }
                }, tweet.id);
                ibRetweet.setVisibility(View.GONE);
                ibRetweetEmpty.setVisibility(View.VISIBLE);

                tweet.retweetCount--;
                tweet.retweeted = false;
                tvRetweetCount.setText(tweet.retweetCount + " Retweets");
            }
        });

        tvScreenName.setText("@" + tweet.user.screenName);
        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        //tvCreatedAt.setText(tweet.createdAt);
        tvCreatedAt.setText(tweet.createdAtFormatted);

        tvRetweetCount.setText(tweet.retweetCount + " Retweets");
        tvLikeCount.setText(tweet.likeCount + " Likes");

        int radius = 50;

        if(tweet.mediaUrl != null) {
            Glide.with(this).load(tweet.mediaUrl).transform(new RoundedCorners(radius)).into(ivMedia);
        }
        else {
            ivMedia.setVisibility(View.GONE);
        }

        Glide.with(this).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);

    }

}