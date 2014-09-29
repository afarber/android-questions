package com.nfclab.transportation;


import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TransportationActivity extends Activity {
   
    Group group = new Group();

	//String ids[] = new String[] { "1", "2", "2", "1", "3", "3" };
	private String studentId="";
	private String studentName="";
	//String names[] = new String[] { "vedat", "kerem", "kerem", "vedat", "busra", "busra" };
	private String id;
	private String name;
	private String payload;
	int k = -1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView students = (TextView)this.findViewById(R.id.students);
        final TextView info = (TextView)this.findViewById(R.id.info);
        students.setInputType(students.getInputType() | InputType.TYPE_TEXT_FLAG_MULTI_LINE); 
        info.setText("Touch NFC tag to read student data");
        
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
       	 	  
         	  NdefMessage[] messages = getNdefMessages(getIntent());
               for(int i=0;i<messages.length;i++){
               	 for(int j=0;j<messages[0].getRecords().length;j++){
               		 NdefRecord record = messages[i].getRecords()[j];
               		payload=new String(record.getPayload());
               		/* if(j==0)
               			studentId=new String(record.getPayload(),0,record.getPayload().length,Charset.forName("UTF-8"));
               		 else if(j==1)
               			studentName=new String(record.getPayload(),0,record.getPayload().length,Charset.forName("UTF-8"));
               			*/
               		String delimiter = ":";
             		String[] temp = payload.split(delimiter);
             		studentId=temp[0];
             		studentName=temp[1];
               	 }
               }
               
               k += 1;
               if ( k <= 5) 
               {       			
	       			id =  studentId;
	       			name = studentName;
	
	       			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	       			String curentDateandTime = sdf.format(new Date()).toString();
	       			
	       			Group.process( id, name, curentDateandTime );
	       		
	       			students.setText("");
	       			for ( int i = 0; i < Group.size(); i++ )
	       			{
	       				students.setText(students.getText() + "\n" + Group.getStudentAsString( i ) );
	       			}
               }
           }
          else{
        	  info.setText("Touch NFC tag to read student data");
          }
     
        Button webServiceButton = (Button)this.findViewById(R.id.webServiceButton);
        webServiceButton.setOnClickListener(new android.view.View.OnClickListener() 
        {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), WebServiceActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });        

        
        Button emptyButton= (Button)this.findViewById(R.id.emptyButton);
    	emptyButton.setOnClickListener(new android.view.View.OnClickListener() 
        {
        	public void onClick(View v) {
        		Group.empty();
       			students.setText("");
        		info.setText("Items deleted");
        	}
        });
        
        
        Button exitButton	= (Button)this.findViewById(R.id.exitButton);
    	exitButton.setOnClickListener(new android.view.View.OnClickListener() 
        {
        	public void onClick(View v) {
        		finish();
        	}
        });        
        
    }
	
	  NdefMessage[] getNdefMessages(Intent intent) {
	        
	    	// Parse the intent
	        NdefMessage[] msgs = null;
	    	 if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
	    		 Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		        if (rawMsgs != null) {
		            msgs = new NdefMessage[rawMsgs.length];
		            for (int i = 0; i < rawMsgs.length; i++) {
		                msgs[i] = (NdefMessage) rawMsgs[i];
		            }
		        } else {
		            // Unknown tag type
		            byte[] empty = new byte[] {};
		            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
		            NdefMessage msg = new NdefMessage(new NdefRecord[] {
		                record
		            });
		            msgs = new NdefMessage[] {
		                msg
		            };
		        }
	    	 }else {
		            Log.d("NFC Transportation", "Unknown intent.");
		            finish();
		        }
	         
	        return msgs;
	    }	  
  }
