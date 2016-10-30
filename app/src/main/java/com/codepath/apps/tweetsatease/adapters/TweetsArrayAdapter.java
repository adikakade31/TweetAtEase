package com.codepath.apps.tweetsatease.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.activities.TweetDetailActivity;
import com.codepath.apps.tweetsatease.api_helpers.HomeTimelineFetcher;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TableTweetOperations;
import com.codepath.apps.tweetsatease.helpers.TableUserOperations;
import com.codepath.apps.tweetsatease.helpers.TwitterActions;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aditikakadebansal on 10/25/16.
 */

public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.TweetViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;
    private TwitterClient mClient;

    public TweetsArrayAdapter(Context context, TwitterClient client, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
        mClient = client;
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList) {
        mTweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    public void insert(Tweet tweet, int position) {
        mTweets.add(position, tweet);
    }

    public void remove(int position) {
        mTweets.remove(position);
    }

    public int getPosition(Tweet tweet) {
        return mTweets.indexOf(tweet);
    }

    public void fetchTimeline(View view, TwitterClient mClient) {
        //Delete all rows from user
        TableUserOperations.deleteAll();
        //Delete all rows from tweet
        TableTweetOperations.deleteAll();
        //Insert rows in Tweet and User
        populateTimeline(view, mClient);
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder {

         ImageView ivProfile;
         ImageView ivReply;
         ImageView ivRetweet;
         ImageView ivFavorite;
         ImageView ivIconRetweeted;
         TextView tvUsername;
         TextView tvScreenname;
         TextView tvRetweetUser;
         TextView tvBody;
         TextView tvTimeAgo;
         TextView tvRetweetCount;
         TextView tvFavoriteCount;


        public TweetViewHolder(View itemView) {
            super(itemView);
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfilePic);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvScreenname = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTimeAgo = (TextView) itemView.findViewById(R.id.tvTimeAgo);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            tvRetweetUser = (TextView) itemView.findViewById(R.id.tvRetweetUser);
            ivIconRetweeted = (ImageView) itemView.findViewById(R.id.ivIconRetweeted);
        }

        public void populate(final Tweet tweet) {
            if (tweet.isWasRetweeted())
                tvRetweetUser.setText(tweet.getReTweetUser() + " Retweeted");

            tvRetweetUser.setVisibility(tweet.isWasRetweeted() ? View.VISIBLE : View.GONE);
            ivIconRetweeted.setVisibility(tweet.isWasRetweeted() ? View.VISIBLE : View.GONE);
            ivProfile.setImageResource(android.R.color.transparent);
            if(Helper.isInternetConnected(mContext)){
                Glide.with(mContext).load(tweet.getUser().getProfileImageUrl()).into(ivProfile);
            }else {
                Glide.with(mContext).load(R.drawable.ic_launcher).into(ivProfile);
            }

            ivRetweet.setImageResource(tweet.isRetweeted() ? R.drawable.ic_retweeted : R.drawable.ic_retweet);
            ivFavorite.setImageResource(tweet.isFavorited() ? R.drawable.ic_heart_on : R.drawable.ic_heart);
            tvUsername.setText(tweet.getUser().getName());
            tvScreenname.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            tvTimeAgo.setText(tweet.getRelativeCreatedAt());
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        }

        public void listeners(final Tweet tweet) {
            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (Helper.isInternetConnected(mContext)) {
                    TwitterActions.onReply(tweet, (TwitterActions.OnReplyListener) mContext);
                }else {
                    Helper.showErrorSnackBar(v, ResponseStatus.NO_INTERNET_CONNECTION);
                }
                }
            });

            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (Helper.isInternetConnected(mContext)) {
                    if (tweet.isFavorited())
                        ivFavorite.setImageResource(R.drawable.ic_heart_on);
                    else
                        ivFavorite.setImageResource(R.drawable.ic_heart);
                    TwitterActions.onFavorite(mClient, tweet, (TwitterActions.OnFavoriteListener) mContext);
                }else {
                     Helper.showErrorSnackBar(v,ResponseStatus.NO_INTERNET_CONNECTION);
                }
                }
            });

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(Helper.isInternetConnected(mContext)) {
                    ivRetweet.setImageResource(R.drawable.ic_retweeted);
                    TwitterActions.onReTweet(mClient, tweet, (TwitterActions.OnReTweetListener) mContext);
                }else {
                    Helper.showErrorSnackBar(v,ResponseStatus.NO_INTERNET_CONNECTION);
                }
                }
            });

            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent i = new Intent(mContext.getApplicationContext(), TweetDetailActivity.class);
                    Tweet tweet = mTweets.get(position);
                    i.putExtra("tweet", tweet);
                    mContext.startActivity(i);
                }
            });
        }

    }

    @Override
    public TweetsArrayAdapter.TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TweetViewHolder(
                inflater.inflate(R.layout.item_tweet, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.TweetViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.populate(tweet);
        holder.listeners(tweet);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void populateTimeline(final View view, TwitterClient client) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                clear();
                addAll(Tweet.fromJSONArray(json));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Helper.showErrorSnackBar(view, ResponseStatus.REQUEST_FAILURE );
            }
        });
    }

    public void addMoreToTimeline(final View view, TwitterClient client) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                addAll(Tweet.fromJSONArray(json));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Helper.showErrorSnackBar(view, ResponseStatus.REQUEST_FAILURE );
            }
        });
    }


}
