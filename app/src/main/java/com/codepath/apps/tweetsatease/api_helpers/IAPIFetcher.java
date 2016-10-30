package com.codepath.apps.tweetsatease.api_helpers;

import com.codepath.oauth.OAuthAsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by aditikakadebansal on 10/26/16.
 */

public interface IAPIFetcher {
    public RequestParams getRequestParams();
    public String getAPIURL();
}
