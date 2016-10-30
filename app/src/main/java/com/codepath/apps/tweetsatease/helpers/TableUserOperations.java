package com.codepath.apps.tweetsatease.helpers;

import com.codepath.apps.tweetsatease.TwitterDatabase;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.User;
import com.codepath.apps.tweetsatease.models.User_Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;

/**
 * Created by aditikakadebansal on 10/28/16.
 */

public class TableUserOperations {
    public static User byUserId(long userId) {
        return new Select().from(User.class).where(User_Table.uid.eq(userId)).querySingle();
    }

    public static User byUserName(String name) {
        return new Select().from(User.class).where(User_Table.user_name.eq(name)).querySingle();
    }

    public static void deleteAll() {
        new Delete().from(User.class).execute();
    }

}
