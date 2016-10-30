package com.codepath.apps.tweetsatease.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.TwitterApplication;
import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweetsatease.api_helpers.HomeTimelineFetcher;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.fragments.ComposeTweetFragment;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TableTweetOperations;
import com.codepath.apps.tweetsatease.helpers.TableUserOperations;
import com.codepath.apps.tweetsatease.helpers.TwitterActions;
import com.codepath.apps.tweetsatease.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.User;

import java.util.ArrayList;


public class TimelineActivity
        extends AppCompatActivity
        implements TwitterActions.OnTweetListener,
        TwitterActions.OnReTweetListener, TwitterActions.OnFavoriteListener,
        TwitterActions.OnReplyListener{

    private ArrayList<Tweet> mTweets;
    private TweetsArrayAdapter mTweetsArrayAdapter;
    private RecyclerView mRvTweets;
    private SwipeRefreshLayout mSwipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager mLinearLayoutManager;
    private FloatingActionButton mFab;
    private TwitterClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setUpData();
        setUpListeners();

        getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_launcher));
        if(Helper.isInternetConnected(getApplicationContext())) {
            mTweetsArrayAdapter.fetchTimeline(findViewById(android.R.id.content), mClient);
        }else {
            mTweetsArrayAdapter.clear();
            mTweetsArrayAdapter.addAll(TableTweetOperations.getAllTweets());
            Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void loadNextDataFromApi() {
        if(Helper.isInternetConnected(getApplicationContext())) {
            mTweetsArrayAdapter.addMoreToTimeline(findViewById(android.R.id.content), mClient);
        }else {
            mTweetsArrayAdapter.clear();
            mTweetsArrayAdapter.addAll(TableTweetOperations.getAllTweets());
            Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
        }

    }

    public void setUpData(){
        mClient = TwitterApplication.getRestClient();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        mTweets = new ArrayList<>();
        mTweetsArrayAdapter = new TweetsArrayAdapter(this, mClient, mTweets);
        mRvTweets.setAdapter(mTweetsArrayAdapter);

        Helper.setCurrentUser(TwitterApplication.getRestClient());

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvTweets.setLayoutManager(mLinearLayoutManager);

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mFab = (FloatingActionButton) findViewById(R.id.fab_compose);
    }

    public void setUpListeners(){
        scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };

        mRvTweets.addOnScrollListener(scrollListener);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                HomeTimelineFetcher.setMinIdAvailable();
                if(Helper.isInternetConnected(getApplicationContext())) {
                    mTweetsArrayAdapter.fetchTimeline(findViewById(android.R.id.content), mClient);
                }else {
                    mTweetsArrayAdapter.clear();
                    mTweetsArrayAdapter.addAll(TableTweetOperations.getAllTweets());
                    Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
                }
                mSwipeContainer.setRefreshing(false);
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment tweetDialog = ComposeTweetFragment.newInstance(User.getloggedInUser(), null);
                if(Helper.isInternetConnected(getApplicationContext())) {
                    tweetDialog.show(fm, "Compose Tweet");
                }else {
                    Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
                }
            }
        });

        mRvTweets.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && mFab.isShown())
                {
                    mFab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    mFab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void onTweetSent(Tweet tweet) {
        mTweetsArrayAdapter.insert(tweet, 0);
        mTweetsArrayAdapter.notifyItemInserted(0);

    }
    @Override
    public void onReTweet(Tweet tweet, Tweet newTweet) {

        int position = mTweetsArrayAdapter.getPosition(tweet);

        tweet.setRetweetCount(newTweet.getRetweetCount());
        tweet.setRetweeted(newTweet.isRetweeted());

        mTweetsArrayAdapter.remove(position);
        mTweetsArrayAdapter.insert(tweet, position);
        mTweetsArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFavorite(final Tweet tweet, Tweet newTweet) {

        int position = mTweetsArrayAdapter.getPosition(tweet);

        mTweetsArrayAdapter.remove(position);
        mTweetsArrayAdapter.insert(newTweet, position);
        mTweetsArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReply(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(User.getloggedInUser(), tweet);
        if(Helper.isInternetConnected(getApplicationContext())) {
            composeTweetDialog.show(fm, "Compose Tweet");
        }else {
            Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);

        }
    }


}
