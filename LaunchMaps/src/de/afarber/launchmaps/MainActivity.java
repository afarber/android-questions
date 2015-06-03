package de.afarber.launchmaps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
    public void launchMaps(View v) {
    	double lat = 70.0;
    	double lng = 50.0;
    	String title = "Test";
    	String uri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + title + ")";
    	
    	// TODO: http://developer.android.com/guide/components/intents-common.html
    	
    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
	    	//Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345")
	    	Uri.parse(uri)
    	);
    	startActivity(intent);    	
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
