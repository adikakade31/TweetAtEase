package com.codepath.apps.tweetsatease.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.codepath.apps.tweetsatease.TwitterDatabase;
import com.codepath.apps.tweetsatease.api_helpers.HomeTimelineFetcher;
import com.codepath.apps.tweetsatease.helpers.TableTweetOperations;
import com.codepath.apps.tweetsatease.helpers.TableUserOperations;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by aditikakadebansal on 10/25/16.
 */
@Table(database = TwitterDatabase.class, name = "Tweet")

public class Tweet extends BaseModel implements Parcelable {

    @PrimaryKey
    @Column( name = "tweetId")
    private long tweetID;

    @Column( name = "body")
    private String body;

    @Column( name = "created_at")
    private String createdAt;

    @Column( name = "retweet_count")
    private int retweetCount;

    @Column( name = "favorite_count")
    private int favoriteCount;

    @Column( name = "is_favorite")
    private boolean favorited;

    @Column( name = "is_retweeted")
    private boolean retweeted;

    @Column(name = "wasRetweeted")
    private boolean wasRetweeted;

    @Column(name = "reTweetUser")
    private String reTweetUser;

    @Column(name = "twitterURL")
    private String twitterURL;

    @Column
    @ForeignKey(saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.CASCADE)
    private User user;


    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() { return user;}
    public void setTweetID(long tweetID) { this.tweetID = tweetID;}

    public void setBody(String body) { this.body = body;}

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt;}

    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount;}

    public void setFavorited(boolean favorited) { this.favorited = favorited;}

    public String getBody() {
        return body;
    }

    public long getTweetID() {
        return tweetID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getRetweetCount() { return retweetCount;}

    public void setRetweetCount(int retweetCount) { this.retweetCount = retweetCount;}

    public int getFavoriteCount() { return favoriteCount;}

    public boolean isFavorited() { return favorited;}

    public boolean isRetweeted() { return retweeted;}

    public void setRetweeted(boolean retweeted) { this.retweeted = retweeted; }

    public String getRelativeCreatedAt() {
        return getRelativeTimeAgo(createdAt);
    }

    public boolean isWasRetweeted() { return wasRetweeted; }

    public void setReTweetUser(String reTweetUser) { this.reTweetUser = reTweetUser; }

    public void setWasRetweeted(boolean wasRetweeted) { this.wasRetweeted = wasRetweeted; }

    public String getReTweetUser() { return reTweetUser; }

    public String getTwitterURL() { return twitterURL; }

    public void setTwitterURL(String twitterURL) { this.twitterURL = twitterURL; }

    public Tweet() {
        super();
    }

    public Tweet (JSONObject jsonObject){
        super();

        try {
                this.body = jsonObject.getString("text");
                this.tweetID = jsonObject.getLong("id");
                this.createdAt = jsonObject.getString("created_at");
                this.retweetCount = jsonObject.getInt("retweet_count");
                this.favoriteCount = jsonObject.getInt("favorite_count");

                if (jsonObject.has("retweeted_status")) {
                    this.user = new User(jsonObject.getJSONObject("retweeted_status").getJSONObject("user"));
                    this.user.save();
                    this.reTweetUser = new User(jsonObject.getJSONObject("user")).getName();
                    this.wasRetweeted = true;
                    //this.tweetID = jsonObject.getJSONObject("retweeted_status").getLong("id");
                } else {
                    this.user = new User(jsonObject.getJSONObject("user"));
                    this.user.save();
                    this.wasRetweeted = false;
                    this.reTweetUser = null;
                    User dummyUSer = TableUserOperations.byUserName(this.user.getName());
                    Log.d("DEBUG", "dummyUser name = "+dummyUSer.getName());
                }

                if (jsonObject.has("extended_entities")) {
                    if (jsonObject.getJSONObject("extended_entities").getJSONArray("media").length() > 0) {
                        this.twitterURL = jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("url");
                    }
                }
                this.favorited = jsonObject.getBoolean("favorited");
                this.retweeted = jsonObject.getBoolean("retweeted");
                HomeTimelineFetcher.observeTweetID(this.tweetID);
            } catch (JSONException e) {
             e.printStackTrace();
            }
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tweet tweet = TableTweetOperations.byTweetId(jsonObject.getLong("id"));
                if(tweet == null) {
                    tweet = new Tweet(jsonObject);
                }else {
                    tweet.retweetCount = jsonObject.getInt("retweet_count");
                    tweet.favoriteCount = jsonObject.getInt("favorite_count");
                    tweet.favorited = jsonObject.getBoolean("favorited");
                    tweet.retweeted = jsonObject.getBoolean("retweeted");
                    HomeTimelineFetcher.observeTweetID(tweet.tweetID);
                }
                tweets.add(tweet);
                tweet.save();
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;

    }

    private static String getRelativeTimeAgo(String rawJsonDate) {
        PrettyTime p = new PrettyTime();
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            try {
                String relativeTimeAgo = p.format(sdf.parse(rawJsonDate)).replace("from now", "").replace("moments", "just now").replace("ago", "")
                        .replace(" days", "d").replace(" hours", "h").replace(" minutes", "m").replace(" seconds", "s")
                        .replace(" day", "d").replace(" hour", "h").replace(" minute", "m").replace(" second", "s");

                return relativeTimeAgo;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.tweetID);
        dest.writeString(this.body);
        dest.writeString(this.createdAt);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favoriteCount);
        dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
        dest.writeByte(this.retweeted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.wasRetweeted ? (byte) 1 : (byte) 0);
        dest.writeString(this.reTweetUser);
        dest.writeString(this.twitterURL);
        dest.writeParcelable(this.user, flags);
    }

    protected Tweet(Parcel in) {
        this.tweetID = in.readLong();
        this.body = in.readString();
        this.createdAt = in.readString();
        this.retweetCount = in.readInt();
        this.favoriteCount = in.readInt();
        this.favorited = in.readByte() != 0;
        this.retweeted = in.readByte() != 0;
        this.wasRetweeted = in.readByte() != 0;
        this.reTweetUser = in.readString();
        this.twitterURL = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
