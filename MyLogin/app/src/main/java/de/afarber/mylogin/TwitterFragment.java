package de.afarber.mylogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TwitterFragment extends LoginFragment {

    public final static String TAG = "TwitterFragment";

    public TwitterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSocial = Utils.TWITTER;
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
