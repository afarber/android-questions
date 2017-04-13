package de.afarber.validateprefs;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    public static final String ADDRESS   = "address";
    public static final String USERNAME  = "username";
    public static final String PASSWORD  = "password";

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        addPreferencesFromResource(R.xml.settings);

        EditTextPreference address = (EditTextPreference) findPreference(ADDRESS);
        EditTextPreference username = (EditTextPreference) findPreference(USERNAME);
        EditTextPreference password = (EditTextPreference) findPreference(PASSWORD);

        address.setOnPreferenceChangeListener(this);
        username.setOnPreferenceChangeListener(this);
        password.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        Log.d(TAG, "onPreferenceChange preference=" + preference + ", value=" + value);

        if (! (preference instanceof EditTextPreference)) {
            return false;
        }

        EditTextPreference editTextPreference = (EditTextPreference)preference;
        String key = editTextPreference.getKey();
        String text = editTextPreference.getText();

        Log.d(TAG, String.format("key %s, text %s", key, text));

        if (key == null || key.isEmpty() || text == null || text.isEmpty()) {
            return false;
        }

        switch (key) {
            case ADDRESS: {
                try {
                    new URI(text);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                    return false;
                }
                return true;
            }

            case USERNAME: {
                return text.length() > 0;
            }

            case PASSWORD: {
                return text.length() > 0;
            }
        }

        return false;
    }
}
