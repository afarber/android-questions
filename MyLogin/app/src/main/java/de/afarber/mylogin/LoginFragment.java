package de.afarber.mylogin;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public abstract class LoginFragment
        extends Fragment
        implements View.OnClickListener {

    protected int mSocial;
    protected String mGiven;
    protected String mPhoto;
    protected String mPlace;

    private ImageView mPhotoImageView;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private TextView mGivenTextView;
    private TextView mPlaceTextView;

    private Animation mShowFab;
    private Animation mHideFab;

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mShowFab = AnimationUtils.makeInAnimation(context, false);
        mShowFab.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                mFab.setVisibility(View.VISIBLE);
            }
        });

        mHideFab = AnimationUtils.makeOutAnimation(context, true);
        mHideFab.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    public void showFab(boolean show) {
        boolean visible = mFab.isShown();
        if (show) {
            if (!visible)
                mFab.startAnimation(mShowFab);
        } else {
            if (visible)
                mFab.startAnimation(mHideFab);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mPhotoImageView = (ImageView) v.findViewById(R.id.photo);
        mPhotoImageView.setBackgroundColor(Utils.SOCIAL_COLORS[mSocial]);

        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mGivenTextView = (TextView) v.findViewById(R.id.given);
        mPlaceTextView = (TextView) v.findViewById(R.id.place);

        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        if (isLoggedIn()) {
            disconnect();
            mGiven = null;
            mPhoto = null;
            mPlace = null;

        } else {
            connect();
        }

        updateUi();
    }

    private boolean isLoggedIn() {
        return (mGiven != null && mGiven.length() > 0);
    }

    protected abstract boolean isConnecting();
    protected abstract boolean isConnected();
    protected abstract void connect();
    protected abstract void disconnect() ;

    protected void updateUi() {
        if (isConnecting()) {
            mProgressBar.setVisibility(View.VISIBLE);
            showFab(false);
        } else if (isConnected()) {
            mProgressBar.setVisibility(View.GONE);
            mFab.setImageResource(R.drawable.ic_account_remove_white_48dp);
            showFab(true);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mFab.setImageResource(R.drawable.ic_account_plus_white_48dp);
            showFab(true);
        }

        mGivenTextView.setText(mGiven != null ? mGiven : "");
        mPlaceTextView.setText(mPlace != null ? mPlace : "");

        if (isLoggedIn()) {
            if (URLUtil.isNetworkUrl(mPhoto))
                Picasso.with(getContext()).load(mPhoto).into(mPhotoImageView);
            else
                mPhotoImageView.setImageResource(R.drawable.photo);
        } else {
            mPhotoImageView.setImageDrawable(null);
        }
    }
}
