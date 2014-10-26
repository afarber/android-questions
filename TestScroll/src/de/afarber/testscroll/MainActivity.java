package de.afarber.testscroll;

import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {
	private static final String DEBUG_TAG = "de.afarber.testscroll";
	private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
		new BitmapLoaderTask().execute("game_board.png");
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
    
    private void setImageBitmap(Bitmap bmp) {
		imageView = new ScrollableImageView(this);
		imageView.setLayoutParams(new RelativeLayout.LayoutParams(bmp.getWidth(), bmp.getHeight()));
		imageView.setImageBitmap(bmp);
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		root.addView(imageView);
	}

	private class BitmapLoaderTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			AssetManager assets = getAssets();
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(assets.open(params[0]));
			} catch (IOException e) {
				Log.e(DEBUG_TAG, e.getMessage(), e);
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			setImageBitmap(result);
		}
	}
}
