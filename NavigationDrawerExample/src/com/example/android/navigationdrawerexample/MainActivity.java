/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Added by Alexander Farber:
 * 
 *     Right drawer with music actions
 *     Icons for all ListView entries
 *     Toolbar with ImageButton
 *     Broadcasts sent from Activity to Fragment
 *     minSdkLevel decreased to 8
 */

package com.example.android.navigationdrawerexample;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;
    private ListView mRightDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private String[] mActions;
    private String[] mLabels;
    private int[] mIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mActions = getResources().getStringArray(R.array.actions_array);
        mLabels = getResources().getStringArray(R.array.labels_array);
        
        TypedArray ta = getResources().obtainTypedArray(R.array.icons_array);
        mIcons = new int[ta.length()];
        for (int i = 0; i < mIcons.length; i++)
        	mIcons[i] = ta.getResourceId(i, R.drawable.ic_menu_black_24dp);
        ta.recycle();
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);
        mRightDrawer = (ListView) findViewById(R.id.right_drawer);

        // set up the left drawer's list view with items and click listener
        mLeftDrawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPlanetTitles) {
        	@Override
            public View getView(int position, View convertView, ViewGroup parent) {
	    		TextView view = (TextView) super.getView(position, convertView, parent);
	    		view.setCompoundDrawablePadding(24);
	    		view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stars_black_24dp, 0, 0, 0);
	    		return view;
        	}
        });
        mLeftDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            selectLeftItem(position);
	            mDrawerLayout.closeDrawer(mLeftDrawer);
			}
		});

        mRightDrawer.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mLabels) {
        	@Override
            public View getView(int position, View convertView, ViewGroup parent) {
        		TextView view = (TextView) super.getView(position, convertView, parent);
	    		view.setCompoundDrawablePadding(24);
        		view.setCompoundDrawablesWithIntrinsicBounds(mIcons[position], 0, 0, 0);
        		return view;
            }
        });
        mRightDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String action = mActions[position];
				Intent intent = new Intent(action);
				//intent.putExtra("message", "data");
				sendBroadcast(intent);
		        mDrawerLayout.closeDrawer(mRightDrawer);
			}
		});
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                mToolbar.setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mToolbar.setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        if (savedInstanceState == null) {
            selectLeftItem(0);
        }
    }

    public void openActions(View v) {
        boolean actionsOpen = mDrawerLayout.isDrawerOpen(mRightDrawer);
        if (actionsOpen)
        	mDrawerLayout.closeDrawer(mRightDrawer);
        else
        	mDrawerLayout.openDrawer(mRightDrawer);
    }
  
    @Override
    public void onBackPressed() {
    	if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
    		mDrawerLayout.closeDrawer(mLeftDrawer);
    	} else if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
    		mDrawerLayout.closeDrawer(mRightDrawer);
    	} else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mLeftDrawer);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
    	
	    menu.findItem(R.id.action_websearch).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, mToolbar.getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void selectLeftItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mLeftDrawer.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        mToolbar.setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
