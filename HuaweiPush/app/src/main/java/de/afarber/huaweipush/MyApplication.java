package de.afarber.huaweipush;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.push.HmsMessaging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MyApplication extends MultiDexApplication {
    public static final String TAG = "HuaweiPush";
    public static final String TOKEN = "token";

    private final ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        // generate AAID before requesting push token
        HmsMessaging.getInstance(getApplicationContext()).setAutoInitEnabled(true);

        mExecutor.execute(() -> {
            try {
                // request push token for this Huawei device
                String appId = getApplicationContext().getString(R.string.huawei_app_id);
                String token = HmsInstanceId.getInstance(getApplicationContext()).getToken(appId, "HCM");
                if (!TextUtils.isEmpty(token)) {
                    // this only works for EMUI 10 or newer devices, otherwise MyService.onNewToken() is called
                    Log.d(TAG,"getToken token=" + token);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().putString(TOKEN, token).apply();
                }
            } catch (ResolvableApiException ex) {
                Log.w(TAG,"getToken failed", ex);
                tryToResolve(getApplicationContext(), ex);
            } catch (Exception ex) {
                Log.w(TAG,"getToken failed", ex);
            }
        });
    }

    private static void tryToResolve(@NonNull Context context, @NonNull ResolvableApiException ex) {
        PendingIntent resolution = ex.getResolution();
        Log.d(TAG,"tryToResolve resolution=" + resolution);
        if (resolution != null) {
            try {
                resolution.send();
            } catch (PendingIntent.CanceledException ex2) {
                Log.w(TAG,"tryToResolve failed", ex2);
            }
        } else {
            Intent resolutionIntent = ex.getResolutionIntent();
            if (resolutionIntent != null) {
                resolutionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(resolutionIntent);
            }
        }
    }
}
