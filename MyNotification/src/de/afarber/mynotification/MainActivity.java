package de.afarber.mynotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	private Context mContext;
	private NotificationManager mManager;
	private static final int NOTIFY_ID = 1234;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		mContext = getApplicationContext();
		mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        
		Button showButton = (Button) findViewById(R.id.show);
		showButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openCar();

		        Intent appIntent = new Intent(mContext, MainActivity.class);
		        appIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        appIntent.putExtra("my_data", 12345);

				//Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://afarber.de"));
				
		        String question = getString(R.string.the_question);
		        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		        Notification notification = new NotificationCompat.Builder(mContext)
		                .setContentTitle(question)
		                .setContentText(question)
		                .setTicker(question)
		                .setWhen(System.currentTimeMillis())
		                .setContentIntent(contentIntent)
		                .setDefaults(Notification.DEFAULT_ALL)
		                .setAutoCancel(true)
		                .setSmallIcon(R.drawable.ic_launcher)
		                .build();

		        mManager.notify(NOTIFY_ID, notification);
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				closeCar();
				
				mManager.cancel(NOTIFY_ID);
			}
		});
    }
    
    private void openCar() {
		Toast.makeText(mContext, getString(R.string.car_opened), Toast.LENGTH_SHORT).show();
    }

    private void closeCar() {
		Toast.makeText(mContext, getString(R.string.car_closed), Toast.LENGTH_SHORT).show();
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
