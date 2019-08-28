package com.huawei.logger;

import com.huawei.hmssample.LogFragment;
import com.huawei.hmssample.R;

import android.app.Activity;

public class LoggerActivity extends Activity{
	
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	initializeLogging();
    }
    
	
    private void initializeLogging() {
        LogFragment logFragment = (LogFragment) getFragmentManager().findFragmentById(R.id.framelog);

        LogCatWrapper logcat = new LogCatWrapper();
        logcat.setNext(logFragment.getLogView());

        Log.setLogNode(logcat);
    }
}
