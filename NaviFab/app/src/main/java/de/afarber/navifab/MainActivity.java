package de.afarber.navifab;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_CLOSE_DELAY = 250;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mLeftDrawer;
    private ImageButton mAccount;
    private CircleImageView mPhotoImageView;
    private TextView mGivenTextView;
    private TextView mPlaceTextView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mLeftDrawer = (NavigationView) findViewById(R.id.left_drawer);
        mLeftDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        Menu menu = mLeftDrawer.getMenu();

                        if (menuItem.getGroupId() == R.id.my_move) {
                            menu.setGroupCheckable(R.id.my_move, true, true);
                            menu.setGroupCheckable(R.id.his_move, false, false);
                            menu.setGroupCheckable(R.id.extras, false, false);
                        } else if (menuItem.getGroupId() == R.id.his_move) {
                            menu.setGroupCheckable(R.id.my_move, false, false);
                            menu.setGroupCheckable(R.id.his_move, true, true);
                            menu.setGroupCheckable(R.id.extras, false, false);
                        } else if (menuItem.getGroupId() == R.id.extras) {
                            menu.setGroupCheckable(R.id.my_move, false, false);
                            menu.setGroupCheckable(R.id.his_move, false, false);
                            menu.setGroupCheckable(R.id.extras, true, true);
                        }

                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawer(mLeftDrawer);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // TODO show the selected game or settings or help - after delay
                            }
                        }, DRAWER_CLOSE_DELAY);

                        return true;
                    }
                });

        mGivenTextView = (TextView) findViewById(R.id.given);
        mPlaceTextView = (TextView) findViewById(R.id.place);
        mPhotoImageView = (CircleImageView) findViewById(R.id.photo);

        mGivenTextView.setText("Alexander");
        mPlaceTextView.setText("Bochum");
        mPhotoImageView.setImageResource(R.drawable.farber);

        mAccount = (ImageButton) findViewById(R.id.account);
        // workaround, can not use android:onclick in headerLayout
        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccounts(v);
            }
        });
    }

    public void showAccounts(View v) {
        if (mDrawerLayout.isDrawerOpen(mLeftDrawer))
            mDrawerLayout.closeDrawer(mLeftDrawer);

        final SocialFragment f = new SocialFragment();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.root, f, SocialFragment.TAG)
                        .commit();
            }
        }, DRAWER_CLOSE_DELAY);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
            mDrawerLayout.closeDrawer(mLeftDrawer);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
}


