package de.afarber.mylogin;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.List;

public class GoogleFragment extends LoginFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public final static String TAG = "GoogleFragment";

    private GoogleApiClient mGoogleApiClient;

    public GoogleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSocial = Utils.GOOGLE;
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected boolean isConnecting() {
        return mGoogleApiClient.isConnecting();
    }

    @Override
    protected boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    protected void connect() {
        mGoogleApiClient.connect();
    }

    @Override
    protected void disconnect() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Person me = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        if (me != null) {
            String sid = me.getId();
            Person.Name name = me.getName();
            mGiven = name.getGivenName();
            String family = name.getFamilyName();
            boolean female = (me.hasGender() && me.getGender() == 1);

            mPhoto = null;
            if (me.hasImage() && me.getImage().hasUrl()) {
                mPhoto = me.getImage().getUrl();
                mPhoto = mPhoto.replaceFirst("\\bsz=\\d+\\b", "sz=300");
            }

            List<Person.PlacesLived> places = me.getPlacesLived();
            if (places != null) {
                for (Person.PlacesLived place : places) {
                    mPlace = place.getValue();
                    if (place.isPrimary())
                        break;
                }
            }

        }
        updateUi();
    }

    @Override
    public void onConnectionSuspended(int i) {
        connect();
        updateUi();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), 123);
            } catch (IntentSender.SendIntentException e) {
                connect();
            }
        } else {
            int code = connectionResult.getErrorCode();
            String str = GoogleApiAvailability.getInstance().getErrorString(code);
            Utils.showToast(getContext(), "Error: " + str);
        }
        updateUi();
    }
}
