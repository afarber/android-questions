package de.afarber.mynotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
	private Context mContext;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationBuilder;
	private static final int NOTIFY_ID = 1234;

	/*
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        Log.d("onNewIntent", "Extra: " + extras.getString("my_data"));
	    }
	}
	*/
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String question = getString(R.string.the_question);
		mContext = getApplicationContext();
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
        Intent appIntent = new Intent(mContext, OpenActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        appIntent.putExtra("my_data", 12345);
		
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 
    		0, 
    		appIntent, 
    		PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Sets up the Open and Flash action buttons that
        // will appear in the expanded view of the notification.
        Intent openIntent = new Intent(this, OpenActivity.class);
        openIntent.setAction("open");
        PendingIntent piOpen = PendingIntent.getService(this, 0, openIntent, 0);

        Intent flashIntent = new Intent(this, OpenActivity.class);
        flashIntent.setAction("flash");
        PendingIntent piFlash = PendingIntent.getService(this, 0, flashIntent, 0);

        mNotificationBuilder = new NotificationCompat.Builder(mContext)
        	.setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(question)
            .setContentText(question)
            .setTicker(question)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
	        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
	        /*
	         * Sets the big view "big text" style and supplies the
	         * text (the user's reminder message) that will be displayed
	         * in the detail area of the expanded notification.
	         * These calls are ignored by the support library for
	         * pre-4.1 devices.
	         */
	        .setStyle(new NotificationCompat.BigTextStyle()
	             .bigText(question))
	        .addAction(R.drawable.open,
	                getString(R.string.open_car_short), piOpen)
	        .addAction(R.drawable.flash,
	                getString(R.string.flash_lights_short), piFlash);

        
    }
    
	public void showNotification(View v) {
        mNotificationManager.notify(NOTIFY_ID, mNotificationBuilder.build());
	}

	public void cancelNotification(View v) {
		mNotificationManager.cancel(NOTIFY_ID);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
