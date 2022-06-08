package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.text.format.DateUtils;
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
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        TextView tvScreenName;
        TextView tvTimeAgo;
        EditText etReply;
        Button btnReply;
        ImageButton ibSend;

        Tweet tweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            etReply = itemView.findViewById(R.id.etReply);
            btnReply = itemView.findViewById(R.id.btnReply);
            ibSend = itemView.findViewById(R.id.ibSend);

            btnReply.setOnClickListener(this);
            ibSend.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            this.tweet = tweet;

            setDefaultConditions();

            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvTimeAgo.setText(tweet.timeAgo);
            etReply.setText("@" + tweet.user.screenName + " ");
            etReply.setSelection(etReply.getText().length());
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            if(tweet.mediaUrl != null) {
                Glide.with(context).load(tweet.mediaUrl).into(ivMedia);
            }
        }

        @Override
        public void onClick(View view) {
            if(view == btnReply) {
                etReply.setVisibility(View.VISIBLE);
                ibSend.setVisibility(View.VISIBLE);
                btnReply.setVisibility(View.GONE);
            }
            else if(view == ibSend) {
                String replyContent = etReply.getText().toString();
                if(replyContent.isEmpty()) {
                    Toast.makeText(context, "Sorry, your reply cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(replyContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(context, "Sorry, your reply is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                TwitterClient client = TwitterApp.getRestClient(context);

                // Make an API call to Twitter to publish the reply
                client.publishReply(replyContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish reply");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish reply", throwable);
                    }
                }, tweet.id);

                setDefaultConditions();
            }
            else {
                // hide the keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                setDefaultConditions();
            }
        }

        public void setDefaultConditions() {
            etReply.setText("@" + tweet.user.screenName + " ");
            etReply.setVisibility(View.GONE);
            ibSend.setVisibility(View.GONE);
            btnReply.setVisibility(View.VISIBLE);
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
