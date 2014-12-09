package de.afarber.mynotification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class RegionService extends IntentService {
	private static final String TAG = "RegionService";
	
	public RegionService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Received an intent: " + intent);
        String action = intent.getAction();
		Log.d(TAG, "Received an action: " + action);
		if (action == null)
			return;
		
    	Handler handler = new Handler(Looper.getMainLooper());
		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
        if (action.equals(CommonConstants.ACTION_OPEN)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                     Toast.makeText(getApplicationContext(), 
                                getString(R.string.car_opened), 
                                Toast.LENGTH_LONG).show();
                     manager.cancel(CommonConstants.NOTIFICATION_ID);
                }
            });
        } else if (action.equals(CommonConstants.ACTION_FLASH)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
            		Toast.makeText(getApplicationContext(), 
            				getString(R.string.lights_flashed), 
            				Toast.LENGTH_LONG).show();
                    manager.cancel(CommonConstants.NOTIFICATION_ID);
                }
            });
    	}
	}
}