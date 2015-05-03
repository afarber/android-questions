package de.afarber.myprefs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class PrefFragment extends PreferenceFragment 
    implements OnSharedPreferenceChangeListener {
	
	public static String BOOL_1;
	public static String STR_1;
	public static String STR_2;
	
	private CheckBoxPreference mBool1;
	private EditTextPreference mStr1;
	private EditTextPreference mStr2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setHasOptionsMenu(false); 							// TODO does not work
   		addPreferencesFromResource(R.xml.preferences);
   		
   		BOOL_1 = getString(R.string.bool_1);
   		STR_1  = getString(R.string.str_1);
   		STR_2  = getString(R.string.str_2);

   		mBool1 = (CheckBoxPreference) findPreference(BOOL_1);
   		mStr1 = (EditTextPreference) findPreference(STR_1);
   		mStr2 = (EditTextPreference) findPreference(STR_2);
    }
    
	@Override
	public void onResume() {
	    super.onResume();
	    
	    SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
	    // set the summaries from saved values
	    onSharedPreferenceChanged(prefs, BOOL_1);
	    onSharedPreferenceChanged(prefs, STR_1);
	    onSharedPreferenceChanged(prefs, STR_2);
	    prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
	    SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		prefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		Log.d("onSharedPreferenceChanged", key);
		
		if (BOOL_1.equals(key)) {
			boolean bool1 = prefs.getBoolean(key, false);
			mBool1.setSummary(bool1 ? "Enabled" : "Disabled");
		} else if (STR_1.equals(key)) {
			String str1 = prefs.getString(key, "");
			mStr1.setSummary(str1);
		} else if (STR_2.equals(key)) {
			String str2 = prefs.getString(key, "");
			mStr2.setSummary(str2);
		} 		
	}    
}
