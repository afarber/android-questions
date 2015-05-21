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
	
	public static final int DEFAULT_1 = 45;
	public static final int DEFAULT_2 = 65;
	
	public static String BOOL_1;
	public static String STR_1;
	public static String STR_2;
	public static String SEEK_1;
	public static String SEEK_2;
	public static String NUM_1;
	
	private CheckBoxPreference mBool1;
	private EditTextPreference mStr1;
	private EditTextPreference mStr2;
	private SeekBarPreference mSeek1;
	private SeekBarPreference mSeek2;
	private NumberPickerPreference mNum1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setHasOptionsMenu(false); 							// TODO does not work
   		addPreferencesFromResource(R.xml.preferences);
   		
   		BOOL_1 = getString(R.string.bool_1);
   		STR_1  = getString(R.string.str_1);
   		STR_2  = getString(R.string.str_2);
   		SEEK_1 = getString(R.string.seek_1);
   		SEEK_2 = getString(R.string.seek_2);
   		NUM_1  = getString(R.string.num_1);

   		mBool1 = (CheckBoxPreference) findPreference(BOOL_1);
   		mStr1 = (EditTextPreference) findPreference(STR_1);
   		mStr2 = (EditTextPreference) findPreference(STR_2);
   		mSeek1 = (SeekBarPreference) findPreference(SEEK_1);
   		mSeek2 = (SeekBarPreference) findPreference(SEEK_2);
   		mNum1 = (NumberPickerPreference) findPreference(NUM_1);
    }
    
	@Override
	public void onResume() {
	    super.onResume();
	    
	    SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
	    // set the summaries from saved values
	    onSharedPreferenceChanged(prefs, BOOL_1);
	    onSharedPreferenceChanged(prefs, STR_1);
	    onSharedPreferenceChanged(prefs, STR_2);
	    onSharedPreferenceChanged(prefs, SEEK_1);
	    onSharedPreferenceChanged(prefs, SEEK_2);
	    onSharedPreferenceChanged(prefs, NUM_1);
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
		} else if (SEEK_1.equals(key)) {
			int i1 = prefs.getInt(key, DEFAULT_1);
			mSeek1.setSummary("$ " + i1);
		} else if (SEEK_2.equals(key)) {
			int i2 = prefs.getInt(key, DEFAULT_2);
			mSeek2.setSummary("$ " + i2);
		} else if (NUM_1.equals(key)) {
			// TODO
			mNum1.setSummary("FIXME");
		} 		
	}    
}
