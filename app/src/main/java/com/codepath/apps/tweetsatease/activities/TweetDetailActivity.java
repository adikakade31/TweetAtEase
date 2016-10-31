package com.codepath.apps.tweetsatease.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.TwitterApplication;
import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TwitterActions;
import com.codepath.apps.tweetsatease.models.Tweet;

/**
 * Created by aditikakadebansal on 10/29/16.
 */

public class TweetDetailActivity extends AppCompatActivity implements TwitterActions.OnTweetListener{


    private TextView mTvUsername;
    private TextView mTvScreenname;
    private TextView mTvRetweetUser;
    private TextView mTvBody;
    private TextView mTvTimeAgo;
    private TextView mTvRetweetCount;
    private TextView mTvFavoriteCount;
    private TextView mTvTweetLength;
    private TextView mTvReplyToTweet;
    private EditText mEtReplyTweet;
    private LinearLayout mReplyBox;
    private Button mButtonTweet;
    private ImageView mIvIconRetweeted;
    private ImageView mIvProfile;
    int textColor;

    private Tweet mTweet;
    private Context mContext;
    private TwitterClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();
        mClient = TwitterApplication.getRestClient();
        setUpData();
        setUpListeners();
        mTweet = getIntent().getParcelableExtra("tweet");
        populate(mTweet);
    }

    public void setUpData(){

        mTvUsername = (TextView) findViewById(R.id.tvUsername);
        mTvScreenname = (TextView) findViewById(R.id.tvScreenName);
        mTvBody = (TextView) findViewById(R.id.tvBody);
        mTvTimeAgo = (TextView) findViewById(R.id.tvTimeAgo);
        mTvRetweetCount = (TextView) findViewById(R.id.tvRetweetCount);
        mTvFavoriteCount = (TextView) findViewById(R.id.tvFavoriteCount);
        mTvRetweetUser = (TextView) findViewById(R.id.tvRetweetUser);
        mTvTweetLength = (TextView) findViewById(R.id.tvTweetLength);
        mTvReplyToTweet = (TextView) findViewById(R.id.tvReplyToTweet);

        mIvIconRetweeted = (ImageView) findViewById(R.id.ivIconRetweeted);
        mIvProfile = (ImageView) findViewById(R.id.ivProfilePic);

        mEtReplyTweet = (EditText) findViewById(R.id.etTweet);
        mReplyBox = (LinearLayout) findViewById(R.id.replyBox);
        mButtonTweet = (Button) findViewById(R.id.buttonTweet);

        textColor = mTvTweetLength.getCurrentTextColor();
    }

    public void setUpListeners() {
        mButtonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isInternetConnected(TweetDetailActivity.this)) {
                    TwitterActions.onTweet(mClient, Long.toString(mTweet.getTweetID()), mEtReplyTweet.getText().toString(), TweetDetailActivity.this);
                }
            }
        });

        mEtReplyTweet.addTextChangedListener(textWatcher);

        mEtReplyTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtReplyTweet.hasFocus() && mEtReplyTweet.getText().length() == 0) {
                    mReplyBox.setVisibility(View.VISIBLE);
                    mEtReplyTweet.setText("@" + mTweet.getUser().getScreenName() + " ");
                    mEtReplyTweet.setSelection(mEtReplyTweet.getText().length());
                }
            }
        });
    }

    public void populate(final Tweet tweet) {
        if (tweet.isWasRetweeted())
            mTvRetweetUser.setText(tweet.getReTweetUser() + " Retweeted");

        mTvRetweetUser.setVisibility(tweet.isWasRetweeted() ? View.VISIBLE : View.GONE);
        mTvUsername.setText(tweet.getUser().getName());
        mTvScreenname.setText(tweet.getUser().getScreenName());
        mTvBody.setText(tweet.getBody());
        mTvTimeAgo.setText(tweet.getRelativeCreatedAt());
        mTvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        mTvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        mTvReplyToTweet.setText("Reply to" + " " + tweet.getUser().getName());

        mIvIconRetweeted.setVisibility(tweet.isWasRetweeted() ? View.VISIBLE : View.GONE);
        mIvProfile.setImageResource(android.R.color.transparent);
        Glide.with(mContext).load(tweet.getUser().getProfileImageUrl()).into(mIvProfile);

        mEtReplyTweet.setText(tweet.getUser().getScreenName() + " ");
        mEtReplyTweet.setSelection(mEtReplyTweet.getText().length());
    }

    @Override
    public void onTweetSent(Tweet tweet) {

        mEtReplyTweet.setText("");
        mTvTweetLength.setText("140");
        mTvTweetLength.setTextColor(getResources().getColor(R.color.dodger_blue));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.reply_toast_layout, null);
        Toast toast = new Toast(this);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    TextWatcher mEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int charsRemaining = 140 - s.length();
            mTvTweetLength.setText(Integer.toString(charsRemaining));

            if (charsRemaining >= 0 && charsRemaining < 140) {
                mTvTweetLength.setTextColor(getResources().getColor(R.color.dodger_blue));
                mButtonTweet.setEnabled(true);
            } else {
                if (charsRemaining < 0)
                    mTvTweetLength.setText("0");
                mTvTweetLength.setTextColor(getResources().getColor(R.color.dark_red));
                mButtonTweet.setEnabled(false);
            }
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };



}

