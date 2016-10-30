package com.codepath.apps.tweetsatease.api_helpers;

import android.util.Log;

import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.oauth.OAuthAsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by aditikakadebansal on 10/26/16.
 */

public class HomeTimelineFetcher implements IAPIFetcher {

    private static HomeTimelineFetcher sInstance;

    public static final String SINCE_ID = "since_id";
    public static final String MAX_ID = "max_id";
    public static final String COUNT = "count";

    private long minIDAvailable = 1;

    public static HomeTimelineFetcher getInstance() {
        if (sInstance == null) {
            sInstance = new HomeTimelineFetcher();
        }
        return sInstance;
    }

    public static void setMinIdAvailable() {
        getInstance().minIDAvailable = 1;
    }

    public static void observeTweetID(long id) {
        long minID = getInstance().minIDAvailable;
        if (minID == 1) {
            getInstance().minIDAvailable = id;
            return;
        }
        getInstance().minIDAvailable = Math.min(getInstance().minIDAvailable, id);
        Log.d("DEBUG", "minIDAvailable = "+getInstance().minIDAvailable);
    }

    public String getAPIURL() {
        return "statuses/home_timeline.json";
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(COUNT, 10);
        params.put(SINCE_ID, 1);
        if (minIDAvailable == 1)
            params.put(SINCE_ID, 1);
        else
            params.put(MAX_ID, minIDAvailable - 1);
        return params;
    }
}
