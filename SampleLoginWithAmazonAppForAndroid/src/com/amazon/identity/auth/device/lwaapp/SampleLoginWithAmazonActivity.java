package com.amazon.identity.auth.device.lwaapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;

import java.util.Map;

public class SampleLoginWithAmazonActivity extends Activity {

    private static final String TAG = SampleLoginWithAmazonActivity.class.getName();

    private TextView mProfileText;
    private TextView mLogoutTextView;
    private ProgressBar mLogInProgress;
    private RequestContext requestContext;
    private boolean mIsLoggedIn;
    private View mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContext = RequestContext.create(this);

        requestContext.registerListener(new AuthorizeListener() {
            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult authorizeResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // At this point we know the authorization completed, so remove the ability to return to the app to sign-in again
                        setLoggingInState(true);
                    }
                });
                fetchUserProfile();
            }

            /* There was an error during the attempt to authorize the application */
            @Override
            public void onError(AuthError authError) {
                Log.e(TAG, "AuthError during authorization", authError);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthToast("Error during authorization.  Please try again.");
                        resetProfileView();
                        setLoggingInState(false);
                    }
                });
            }

            /* Authorization was cancelled before it could be completed. */
            @Override
            public void onCancel(AuthCancellation authCancellation) {
                Log.e(TAG, "User cancelled authorization");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthToast("Authorization cancelled");
                        resetProfileView();
                    }
                });
            }
        });


        setContentView(R.layout.activity_main);
        initializeUI();
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestContext.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Scope[] scopes = {ProfileScope.profile(), ProfileScope.postalCode()};
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {
            @Override
            public void onSuccess(AuthorizeResult result) {
                if (result.getAccessToken() != null) {
                    /* The user is signed in */
                    fetchUserProfile();
                } else {
                    /* The user is not signed in */
                }
            }

            @Override
            public void onError(AuthError ae) {
                /* The user is not signed in */
            }
        });
    }


    private void fetchUserProfile() {
        User.fetch(this, new Listener<User, AuthError>() {

            /* fetch completed successfully. */
            @Override
            public void onSuccess(User user) {
                final String name = user.getUserName();
                final String email = user.getUserEmail();
                final String account = user.getUserId();
                final String zipCode = user.getUserPostalCode();
                final Map<String,String> userInfo = user.getUserInfo();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProfileData(name, email, account, zipCode, userInfo);
                    }
                });
            }

            /* There was an error during the attempt to get the profile. */
            @Override
            public void onError(AuthError ae) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoggedOutState();
                        String errorMessage = "Error retrieving profile information.\nPlease log in again";
                        Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                        errorToast.setGravity(Gravity.CENTER, 0, 0);
                        errorToast.show();
                    }
                });
            }
        });
    }

    private void updateProfileData(String name, String email, String account, String zipCode, Map<String,String> userInfo) {
        StringBuilder profileBuilder = new StringBuilder();
        profileBuilder.append(String.format("Welcome, %s!\n", name));
        profileBuilder.append(String.format("Your account is %s\n", account));
        profileBuilder.append(String.format("Your email is %s\n", email));
        profileBuilder.append(String.format("Your zipCode is %s\n\n", zipCode));
        if (userInfo != null && !userInfo.isEmpty()) {
            for (String key : userInfo.keySet()) {
                profileBuilder.append(String.format("%s: %s\n", key, userInfo.get(key)));
            }
        }
        final String profile = profileBuilder.toString();
        Log.d(TAG, "Profile Response: " + profile);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProfileView(profile);
                setLoggedInState();
            }
        });
    }

    /**
     * Initializes all of the UI elements in the activity
     */
    private void initializeUI() {

        mLoginButton = findViewById(R.id.login_with_amazon);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthorizationManager.authorize(
                        new AuthorizeRequest.Builder(requestContext)
                                .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                                .build()
                );
            }
        });

        // Find the button with the logout ID and set up a click handler
        View logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AuthorizationManager.signOut(getApplicationContext(), new Listener<Void, AuthError>() {
                    @Override
                    public void onSuccess(Void response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoggedOutState();
                            }
                        });
                    }

                    @Override
                    public void onError(AuthError authError) {
                        Log.e(TAG, "Error clearing authorization state.", authError);
                    }
                });
            }
        });

        String logoutText = getString(R.string.logout);
        mProfileText = (TextView) findViewById(R.id.profile_info);
        mLogoutTextView = (TextView) logoutButton;
        mLogoutTextView.setText(logoutText);
        mLogInProgress = (ProgressBar) findViewById(R.id.log_in_progress);
    }

    /**
     * Sets the text in the mProfileText {@link TextView} to the value of the provided String.
     *
     * @param profileInfo the String with which to update the {@link TextView}.
     */
    private void updateProfileView(String profileInfo) {
        Log.d(TAG, "Updating profile view");
        mProfileText.setText(profileInfo);
    }

    /**
     * Sets the text in the mProfileText {@link TextView} to the prompt it originally displayed.
     */
    private void resetProfileView() {
        setLoggingInState(false);
        mProfileText.setText(getString(R.string.default_message));
    }

    /**
     * Sets the state of the application to reflect that the user is currently authorized.
     */
    private void setLoggedInState() {
        mLoginButton.setVisibility(Button.GONE);
        setLoggedInButtonsVisibility(Button.VISIBLE);
        mIsLoggedIn = true;
        setLoggingInState(false);
    }

    /**
     * Sets the state of the application to reflect that the user is not currently authorized.
     */
    private void setLoggedOutState() {
        mLoginButton.setVisibility(Button.VISIBLE);
        setLoggedInButtonsVisibility(Button.GONE);
        mIsLoggedIn = false;
        resetProfileView();
    }

    /**
     * Changes the visibility for both of the buttons that are available during the logged in state
     *
     * @param visibility the visibility to which the buttons should be set
     */
    private void setLoggedInButtonsVisibility(int visibility) {
        mLogoutTextView.setVisibility(visibility);
    }

    /**
     * Turns on/off display elements which indicate that the user is currently in the process of logging in
     *
     * @param loggingIn whether or not the user is currently in the process of logging in
     */
    private void setLoggingInState(final boolean loggingIn) {
        if (loggingIn) {
            mLoginButton.setVisibility(Button.GONE);
            setLoggedInButtonsVisibility(Button.GONE);
            mLogInProgress.setVisibility(ProgressBar.VISIBLE);
            mProfileText.setVisibility(TextView.GONE);
        } else {
            if (mIsLoggedIn) {
                setLoggedInButtonsVisibility(Button.VISIBLE);
            } else {
                mLoginButton.setVisibility(Button.VISIBLE);
            }
            mLogInProgress.setVisibility(ProgressBar.GONE);
            mProfileText.setVisibility(TextView.VISIBLE);
        }
    }

    private void showAuthToast(String authToastMessage) {
        Toast authToast = Toast.makeText(getApplicationContext(), authToastMessage, Toast.LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

}
