package de.afarber.mynotification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        mServiceIntent = new Intent(this, RegionService.class);
   }

    public void openCar(View v) {
        mServiceIntent.setAction("open");
        startService(mServiceIntent);
    }

    public void flashLights(View v) {
        mServiceIntent.setAction("flash");
        startService(mServiceIntent);
    }
}

