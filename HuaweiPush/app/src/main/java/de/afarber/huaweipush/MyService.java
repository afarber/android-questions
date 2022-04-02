package de.afarber.huaweipush;

import static de.afarber.huaweipush.MyApplication.TAG;
import static de.afarber.huaweipush.MyApplication.TOKEN;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

public class MyService extends HmsMessageService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG,"onNewToken token=" + token);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(TOKEN, token).apply();
    }
}

