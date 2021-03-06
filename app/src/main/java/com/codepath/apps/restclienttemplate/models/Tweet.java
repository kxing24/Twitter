package com.codepath.apps.restclienttemplate.models;

import android.provider.MediaStore;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public String createdAtFormatted;
    public User user;
    public String mediaUrl;
    public String timeAgo;
    public String id;
    public int retweetCount;
    public int likeCount;
    public boolean favorited;
    public boolean retweeted;

    // empty constructor needed by the Parceler Library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        }
        else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.createdAtFormatted = tweet.getCreatedAt(tweet.createdAt);
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        if(jsonObject.has("extended_entities")) {
            tweet.mediaUrl = jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url");
        }
        else {
            tweet.mediaUrl = null;
        }
        tweet.timeAgo = tweet.getRelativeTimeAgo(tweet.createdAt);
        tweet.id = jsonObject.getString("id");

        tweet.retweetCount = jsonObject.getInt("retweet_count");
        if(jsonObject.has("retweeted_status")) {
            tweet.likeCount = jsonObject.getJSONObject("retweeted_status").getInt("favorite_count");
        }
        else {
            tweet.likeCount = jsonObject.getInt("favorite_count");
        }

        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
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

        return relativeDate;
    }

    public String getCreatedAt(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String createdAt = "";
        try {
            createdAt = sf.parse(rawJsonDate).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return createdAt;
    }
}
