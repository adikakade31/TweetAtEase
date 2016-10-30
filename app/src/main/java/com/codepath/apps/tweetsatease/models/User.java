package com.codepath.apps.tweetsatease.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.tweetsatease.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditikakadebansal on 10/25/16.
 */
@Table(database = TwitterDatabase.class, name = "User")
public class User extends BaseModel implements Parcelable {

    @PrimaryKey
    @Column( name = "uid")
    private long uid;

    @Column( name = "user_name")
    private String name;

    @Column ( name = "screen_name")
    private String screenName;

    @Column( name = "profile_image_url")
    private String profileImageUrl;

    public static User currentUser = null;

    public static void setloggedInUser(User user){currentUser = user;}

    public static User getloggedInUser(){return currentUser;}

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name;}

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) { this.uid = uid;}

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) { this.screenName = screenName;}

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String url) { this.profileImageUrl = url; }

    public User() { super();}

    public User(JSONObject jsonObject) {
        super();
        try {
            this.name = jsonObject.getString("name");
            this.uid = jsonObject.getLong("id");
            this.screenName = String.format("@%s",jsonObject.getString("screen_name"));
            this.profileImageUrl = jsonObject.getString("profile_image_url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
