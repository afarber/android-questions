package de.afarber.mylogin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookSdk;

public class FacebookFragment extends LoginFragment {

    public final static String TAG = "FacebookFragment";

    public FacebookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        FacebookSdk.sdkInitialize(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSocial = Utils.FACEBOOK;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected boolean isConnecting() {
        return false;
    }

    @Override
    protected boolean isConnected() {
        return false;
    }

    @Override
    protected void connect() {
    }

    @Override
    protected void disconnect() {
    }
}
