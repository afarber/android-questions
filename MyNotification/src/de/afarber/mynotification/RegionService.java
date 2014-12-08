package de.afarber.mynotification;

import android.app.IntentService;
import android.content.Intent;
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
		
        if(action.equals("open")) {
    		Toast.makeText(this, 
    				getString(R.string.car_opened), 
    				Toast.LENGTH_SHORT).show();
        } else if (action.equals("flash")) {
    		Toast.makeText(this, 
    				getString(R.string.lights_flashed), 
    				Toast.LENGTH_SHORT).show();
    	}
	}
}