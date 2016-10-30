package com.codepath.apps.tweetsatease.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.TwitterApplication;
import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TwitterActions;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.User;

/**
 * Created by aditikakadebansal on 10/26/16.
 */

public class ComposeTweetFragment extends DialogFragment {
    private ImageView mIvClose;
    private ImageView mIvProfilePic;
    private TextView mTvName;
    private TextView mTvScreenName;
    private EditText mEtTweet;
    private TextView mTvTweetLength;
    private TextView mTvReplyToTweet;
    private Button mButtonTweet;
    private LinearLayout replyBox;
    User currentUser;
    Tweet tweetToReply;

    final static String CURRENT_USER = "CurrentUser";
    final static int TWEET_LENGTH = 140;
    final static String REPLY_TO_TWEET = "ReplyToTweet";


    public ComposeTweetFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetFragment newInstance(User currentUser, Tweet tweetToReply) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(CURRENT_USER, currentUser);
        args.putParcelable(REPLY_TO_TWEET, tweetToReply);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = getArguments().getParcelable(CURRENT_USER);
        tweetToReply = getArguments().getParcelable(REPLY_TO_TWEET);
        setUpData(view);
        setUpListeners();

        mTvName.setText(currentUser.getName());
        mTvScreenName.setText(currentUser.getScreenName());
        Glide.with(getActivity()).load(currentUser.getProfileImageUrl()).into(mIvProfilePic);

    }

    private void setUpData(View view){
        mIvClose = (ImageView) view.findViewById(R.id.ivClose);
        mIvProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        mTvName = (TextView) view.findViewById(R.id.tvName);
        mTvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
        mEtTweet = (EditText) view.findViewById(R.id.etTweet);
        mButtonTweet = (Button) view.findViewById(R.id.buttonTweet);
        mTvTweetLength = (TextView) view.findViewById(R.id.tvTweetLength);
        replyBox = (LinearLayout) view.findViewById(R.id.replyBox);
        replyBox.setVisibility(tweetToReply == null ? View.GONE : View.VISIBLE);
        mTvReplyToTweet = (TextView) view.findViewById(R.id.tvReplyToTweet);

        if (tweetToReply != null) {
            mTvReplyToTweet.setText("Reply to" + " " + tweetToReply.getUser().getName());
            mEtTweet.setText(tweetToReply.getUser().getScreenName() + " ");
            mEtTweet.setSelection(mEtTweet.getText().length());
        }
    }

    private void setUpListeners(){
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mButtonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeTweet(mEtTweet.getText().toString(), getView());
            }
        });

        mEtTweet.addTextChangedListener(textWatcher);

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int charsRemaining = TWEET_LENGTH - s.length();
            mTvTweetLength.setText(Integer.toString(charsRemaining));

            if (charsRemaining >= 0 && charsRemaining < TWEET_LENGTH) {
                mButtonTweet.setEnabled(true);
                mTvTweetLength.setTextColor(getResources().getColor(R.color.dodger_blue));
            } else {
                mButtonTweet.setEnabled(false);
                if (charsRemaining < 0)
                    mTvTweetLength.setText("0");
                    mTvTweetLength.setTextColor(getResources().getColor(R.color.dark_red));
            }
        }
    };

    public void composeTweet(String tweet, View view) {
        if (Helper.isInternetConnected(getContext())) {
            TwitterClient client = TwitterApplication.getRestClient();
            if (tweetToReply == null)
                TwitterActions.onTweet(client, null, tweet, (TwitterActions.OnTweetListener) getActivity());
            else
                TwitterActions.onTweet(client, Long.toString(tweetToReply.getTweetID()), tweet, (TwitterActions.OnTweetListener) getActivity());
        }
        else {
            Helper.showErrorSnackBar(view, ResponseStatus.NO_INTERNET_CONNECTION);
        }
        dismiss();
    }

}

