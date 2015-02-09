package de.afarber.myscroll;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    
    static class ViewHolder {
        public TextView text1;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
    	private int i;

		@Override
		protected Void doInBackground(Void... params) {
            for (i = 0; i < 1000000; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	mAdapter.add("Item " + i);
                    }
                });
            }
			return null;
		}
    }
    
    private ListView mMyListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mMyListView = (ListView) findViewById(R.id.myListView);

        mAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1,
        		android.R.id.text1,
        		mItems) {
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		ViewHolder holder;
        		if (convertView == null) {
        			LayoutInflater inflater = getLayoutInflater();
        			convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        			holder = new ViewHolder();
        			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
        			convertView.setTag(holder);
        		} else {
        			holder = (ViewHolder) convertView.getTag();
        		}

        		String str = (String) getItem(position);
        		holder.text1.setText(str);
        		return convertView;
        	}
        };

        mMyListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        mMyListView.setAdapter(mAdapter);

        new MyTask().execute();
    }

    public void clearList(View v) {
    	mAdapter.clear();
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
