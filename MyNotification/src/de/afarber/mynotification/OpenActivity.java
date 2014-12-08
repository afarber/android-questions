package de.afarber.mynotification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * This Activity is displayed when users click the notification itself. 
 * It provides UI for opening the car
 */
public class OpenActivity extends Activity {
    private Intent mServiceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        mServiceIntent = new Intent(getApplicationContext(), RegionService.class);
   }

    public void openCar(View v) {
    	/*
		Toast.makeText(this, 
				getString(R.string.car_opened), 
				Toast.LENGTH_SHORT).show();
		*/
        mServiceIntent.setAction("open");
        startService(mServiceIntent);
    }

    public void flashLights(View v) {
    	/*
		Toast.makeText(this, 
				getString(R.string.lights_flashed), 
				Toast.LENGTH_SHORT).show();
		*/
        mServiceIntent.setAction("flash");
        startService(mServiceIntent);
    }
}

