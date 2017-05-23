package de.afarber.googleauth;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkListener;

import static de.afarber.googleauth.DatabaseService.ACTION_GOOGLE_USER_EXISTS;
import static de.afarber.googleauth.DatabaseService.ACTION_GOOGLE_USER_MISSING;
import static de.afarber.googleauth.DatabaseService.ACTION_NEWEST_USER_DATA;
import static de.afarber.googleauth.DatabaseService.EXTRA_USER;
import static de.afarber.googleauth.User.FACEBOOK;
import static de.afarber.googleauth.User.GOOGLE;
import static de.afarber.googleauth.User.VKONTAKTE;

// keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v

// keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore -list -v

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GOOGLE_SIGNIN = 1972;
    private static final int PHOTO_WIDTH = 200;
    //private static final String VKONTAKTE_FIELDS = "photo_200,lat,long";
    private static final String VKONTAKTE_FIELDS = "photo_200";
    private final static String FACEBOOK_PHOTO  = "https://graph.facebook.com/%s/picture?type=large";

    private RequestOptions mGlideOptions;
    private IntentFilter mFilter;
    private LocalBroadcastManager mBroadcastManager;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;

    private FloatingActionButton mFab;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private ImageView mPhotoView;
    private TextView mGivenView;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            if (i == null)
                return;

            final String action = i.getAction();
            if (ACTION_GOOGLE_USER_MISSING.equals(action)) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGNIN);
            } else if (ACTION_GOOGLE_USER_EXISTS.equals(action)) {
                DatabaseService.findNewestUser(MainActivity.this);
            } else if (ACTION_NEWEST_USER_DATA.equals(action)) {
                User user = i.getParcelableExtra(EXTRA_USER);
                mGivenView.setText(user.given);
                if (URLUtil.isNetworkUrl(user.photo)) {
                    Glide.with(MainActivity.this)
                        .load(user.photo)
                        .apply(mGlideOptions)
                        .into(mPhotoView);
                }
            }
        }
    };

    private FacebookCallback mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "Facebook onSuccess: " + loginResult);
            Profile profile = Profile.getCurrentProfile();
            User user = new User(FACEBOOK);
            user.sid = profile.getId();
            user.given = profile.getFirstName();
            user.family = profile.getLastName();
            Uri photoUrl = profile.getProfilePictureUri(PHOTO_WIDTH, PHOTO_WIDTH);
            user.photo = (photoUrl != null ? photoUrl.toString() : null);
            DatabaseService.updateUser(MainActivity.this, user);
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "Facebook onCancel");
        }

        @Override
        public void onError(FacebookException ex) {
            Log.d(TAG, "Facebook onError", ex);
        }
    };

    private VKRequest.VKRequestListener mVkontakteListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            try {
                VKList<VKApiUserFull> list = (VKList<VKApiUserFull>) response.parsedModel;
                VKApiUserFull vkUser = list.get(0);
                User user = new User(VKONTAKTE);
                user.sid = String.valueOf(vkUser.id);
                user.given = vkUser.first_name;
                user.family = vkUser.last_name;
                user.photo = vkUser.photo_200;
                DatabaseService.updateUser(MainActivity.this, user);
            } catch (Exception ex) {
                Log.d(TAG, "onComplete exception: " + ex);
            }
        }
    };

    private VKCallback<VKAccessToken> mVkontakteCallback = new VKCallback<VKAccessToken>() {
        @Override
        public void onResult(VKAccessToken res) {
            Log.d(TAG, "Vkontakte onResult: " + res);
            VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,  VKONTAKTE_FIELDS));
            request.executeWithListener(mVkontakteListener);
        }
        @Override
        public void onError(VKError error) {
            Log.d(TAG, "Vkontakte onError: " + error);
        }
    };

    private OkListener mOdnoklassnikiCallback = new OkListener() {
        @Override
        public void onSuccess(final JSONObject json) {
            DatabaseService.fetchOdnoklassnikiUser(MainActivity.this);
        }

        @Override
        public void onError(String error) {
            Log.d(TAG, "Odnoklassniki onError: " + error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGlideOptions = new RequestOptions();
        mGlideOptions.circleCrop();

        mBroadcastManager = LocalBroadcastManager.getInstance(this);

        mFilter = new IntentFilter();
        mFilter.addAction(ACTION_GOOGLE_USER_MISSING);
        mFilter.addAction(ACTION_GOOGLE_USER_EXISTS);
        mFilter.addAction(ACTION_NEWEST_USER_DATA);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, mFacebookCallback);

        setContentView(R.layout.activity_main);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        View headerLayout = mNavigationView.getHeaderView(0);
        mPhotoView = (ImageView) headerLayout.findViewById(R.id.photoView);
        mGivenView = (TextView) headerLayout.findViewById(R.id.givenView);

        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Delete all database records",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mGivenView.setText("");
                mPhotoView.setImageResource(0);
                DatabaseService.deleteAll(MainActivity.this);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            mDrawer,
            mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBroadcastManager.registerReceiver(mMessageReceiver, mFilter);
        DatabaseService.findGoogleUser(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBroadcastManager.unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                User user = new User(GOOGLE);
                user.sid = acct.getId();
                user.given = acct.getGivenName();
                user.family = acct.getFamilyName();
                Uri photoUrl = acct.getPhotoUrl();
                user.photo = (photoUrl != null ? photoUrl.toString() : null);
                DatabaseService.updateUser(this, user);
            } else {
                // Google sign-in is mandatory
                finish();
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)&&
                mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            // do nothing
        } else if (VKSdk.onActivityResult(requestCode, resultCode, data, mVkontakteCallback)) {
            // do nothing
        } else if (Odnoklassniki.getInstance().onAuthActivityResult(requestCode, resultCode, data, mOdnoklassnikiCallback)) {
            // do nothing
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void printSignatures() {
        String[] signatures = VKUtil.getCertificateFingerprint(this, getPackageName());
        Log.d(TAG, "Vkontakte signatures: " + Arrays.toString(signatures));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );
            for (Signature sig: info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(sig.toByteArray());
                Log.d(TAG, "Facebook signature: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ex) {
            Log.w(TAG, "printSignatures", ex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            printSignatures();
            DatabaseService.printAll(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.addView) {
            FragmentManager manager = getFragmentManager();
            AddDialog dialog = new AddDialog();
            dialog.show(manager, "dialog");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed: " + connectionResult);
    }
}

