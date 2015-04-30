package de.afarber.myprefs;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity 
    extends Activity 
	implements OnBackStackChangedListener, 
			   OnSharedPreferenceChangeListener {
	
	public static final String BOOL_1 = "BOOL_1";
	public static final String STR_1 = "STR_1";
	public static final String STR_2 = "STR_2";
	
	private SharedPreferences mPrefs;	
	private Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
	    getFragmentManager().addOnBackStackChangedListener(this);
        setContentView(R.layout.activity_main);
        
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		mEditor = mPrefs.edit();
		
	    if (savedInstanceState == null) {
	    	Fragment fragment = new MainFragment();
	        getFragmentManager().beginTransaction()
		    	.replace(R.id.root, fragment, "main")
				.commit();
	    }
    }

    public void showPrefs(View v) {
    	Fragment fragment = new PrefFragment();
        getFragmentManager().beginTransaction()
       		.addToBackStack(null)
	    	.replace(R.id.root, fragment, "prefs")
			.commit();
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
        if (id == R.id.action_settings) {
        	showPrefs(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public void onBackPressed() {
	    if (!getFragmentManager().popBackStackImmediate()) {
	        super.onBackPressed();
	    }
	}
	
	@Override
	public void onBackStackChanged() {
		getActionBar().setDisplayHomeAsUpEnabled(getFragmentManager().getBackStackEntryCount() > 0);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d("XXX", key);
		
		if (BOOL_1.equals(key)) {
		} else if (STR_1.equals(key)) {
		} else if (STR_2.equals(key)) {
		} 		
	}
}
