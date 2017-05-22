package de.afarber.googleauth;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static de.afarber.googleauth.DatabaseService.ACTION_GOOGLE_USER_EXISTS;
import static de.afarber.googleauth.DatabaseService.ACTION_GOOGLE_USER_MISSING;
import static de.afarber.googleauth.DatabaseService.ACTION_NEWEST_USER_DATA;
import static de.afarber.googleauth.DatabaseService.EXTRA_USER;
import static de.afarber.googleauth.User.GOOGLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GOOGLE_SIGNIN = 1972;

    private RequestOptions mGlideOptions;
    private IntentFilter mFilter;
    private LocalBroadcastManager mBroadcastManager;
    private GoogleApiClient mGoogleApiClient;

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
                Log.d(TAG, user.toString());
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
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                User user = new User();
                user.sid = acct.getId();
                user.net = GOOGLE;
                user.given = acct.getGivenName();
                user.family = acct.getFamilyName();
                Uri photoUrl = acct.getPhotoUrl();
                user.photo = (photoUrl != null ? photoUrl.toString() : null);
                DatabaseService.updateUser(this, user);
            } else {
                finish();
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

/*
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }
*/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google Sign-In will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
