package com.codepath.apps.tweetsatease.helpers;

import android.util.Log;

import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aditikakadebansal on 10/26/16.
 */

public class TwitterActions {

    public interface OnTweetListener {
        void onTweetSent(Tweet tweet);
    }

    public interface OnReTweetListener {
        void onReTweet(Tweet tweet, Tweet newTweet);
    }

    public interface OnFavoriteListener {
        void onFavorite(Tweet tweet, Tweet newTweet);
    }

    public interface OnReplyListener {
        void onReply(Tweet tweet);
    }

    public static void onTweet(TwitterClient client, String replyStatusID, String tweet, final OnTweetListener listener)
    {
        client.composeTweet(tweet, replyStatusID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = new Tweet(response);
                listener.onTweetSent(newTweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {

            }

        });
    }

    public static void onReTweet(TwitterClient client, final Tweet tweet, final OnReTweetListener listener) {

        client.reTweet(Long.toString(tweet.getTweetID()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = new Tweet(response);
                listener.onReTweet(tweet, newTweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }
        });
    }

    public static void onFavorite(TwitterClient client, final Tweet tweet, final OnFavoriteListener listener) {
        client.favoriteTweet(Long.toString(tweet.getTweetID()), tweet.isFavorited(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = new Tweet(response);
                listener.onFavorite(tweet, newTweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    public static void onReply(final Tweet tweet, final OnReplyListener listener) {
        listener.onReply(tweet);
    }

}
