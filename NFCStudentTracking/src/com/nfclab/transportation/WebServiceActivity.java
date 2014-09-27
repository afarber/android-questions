package com.nfclab.transportation;

import com.nfclab.transportation.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WebServiceActivity extends Activity {
	
	TextView orderTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webservice);
        
        final TextView webServiceTextView = (TextView)this.findViewById(R.id.webServiceTextView);

        webServiceTextView.setText( "Data is processed. " );

        Button exitButton= (Button)this.findViewById(R.id.exitButton);
    	exitButton.setOnClickListener(new android.view.View.OnClickListener() 
        {
        	public void onClick(View v) {
        		finish();
        	}
        });           
    }    
}