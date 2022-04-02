package de.afarber.huaweipush;

import static de.afarber.huaweipush.MyApplication.TOKEN;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView mTokenValue;

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefsListener = (prefs, key) -> {
        switch (key) {
            case TOKEN:
                String token = prefs.getString(key, null);
                mTokenValue.setText(token);
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTokenValue = findViewById(R.id.tokenValue);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefsListener.onSharedPreferenceChanged(prefs, TOKEN);
        prefs.registerOnSharedPreferenceChangeListener(mPrefsListener);
    }
}