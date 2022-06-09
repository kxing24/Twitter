package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public static final String TAG = "TweetsAdapter";
    public static final int MAX_TWEET_LENGTH = 280;
    Context context;
    List<Tweet> tweets;


    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements android.view.View.OnClickListener {

        ImageView ivProfileImage;
        ImageView ivMedia;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimeAgo;
        ImageButton ibReply;
        ImageButton ibLikeEmpty;
        ImageButton ibLike;
        TextView tvLikeCount;
        ImageButton ibRetweet;
        ImageButton ibRetweetEmpty;
        TextView tvRetweetCount;

        Tweet tweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            ibReply = itemView.findViewById(R.id.ibReply);
            ibLikeEmpty = itemView.findViewById(R.id.ibLikeEmpty);
            ibLike = itemView.findViewById(R.id.ibLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            ibRetweetEmpty = itemView.findViewById(R.id.ibRetweetEmpty);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);

            ibReply.setOnClickListener(this);
            itemView.setOnClickListener(this);
            ibLikeEmpty.setOnClickListener(this);
            ibLike.setOnClickListener(this);
            ibRetweetEmpty.setOnClickListener(this);
            ibRetweet.setOnClickListener(this);

        }

        public void bind(Tweet tweet) {
            this.tweet = tweet;

            int radius = 50;

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

            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvScreenName.setText("Replying to @" + tweet.user.screenName);
            tvTimeAgo.setText(tweet.timeAgo);
            tvLikeCount.setText(Integer.toString(tweet.likeCount));
            tvRetweetCount.setText(Integer.toString(tweet.retweetCount));
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            if(tweet.mediaUrl != null) {
                Glide.with(context).load(tweet.mediaUrl).transform(new RoundedCorners(radius)).into(ivMedia);
            }
            else {
                ivMedia.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            TwitterClient client = TwitterApp.getRestClient(context);
            if(view == ibReply) {
                // start reply activity
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("original_author", tweet.user.screenName);
                context.startActivity(intent);
            }
            else if(view == ibLikeEmpty) {
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
                tvLikeCount.setText(Integer.toString(tweet.likeCount));
            }
            else if(view == ibLike) {
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
                tvLikeCount.setText(Integer.toString(tweet.likeCount));
            }
            else if(view == ibRetweetEmpty) {
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
                tvRetweetCount.setText(Integer.toString(tweet.retweetCount));
            }
            else if(view == ibRetweet) {
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
                tvRetweetCount.setText(Integer.toString(tweet.retweetCount));
            }
            else {
                // START NEW ACTIVITY THAT SHOWS DETAILED TWEET
                // gets item position
                int position = getAdapterPosition();

                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the tweet at the position
                    Tweet tweet = tweets.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(context, TweetDetailsActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    context.startActivity(intent);
                }

            }

        }

    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

}
