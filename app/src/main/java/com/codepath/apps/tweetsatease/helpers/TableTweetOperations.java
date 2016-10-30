package com.codepath.apps.tweetsatease.helpers;

import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.Tweet_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by aditikakadebansal on 10/28/16.
 */

public class TableTweetOperations {

    public static Tweet byTweetId(long tweetId) {
        return new Select().from(Tweet.class).where(Tweet_Table.tweetId.eq(tweetId)).querySingle();
    }

    public static void deleteAll() {
        new Delete().from(Tweet.class).execute();
    }

    public static List<Tweet> getAllTweets() {
        return SQLite.select().
                from(Tweet.class).queryList();
    }


}
