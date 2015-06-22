package de.afarber.qr_bt_pairing;

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {
	
	private final static int REQUEST_BT_ENABLE   = 1;
	private final static int REQUEST_BT_SETTINGS = 2;
	private final static int REQUEST_BT_PAIRING  = 3;

	private ListView mListView;
	
	private BluetoothAdapter mBluetoothAdapter;
	private DeviceListAdapter mDeviceListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mDeviceListAdapter = new DeviceListAdapter(this);
		mListView = (ListView) findViewById(R.id.devices_list);
        mListView.setAdapter(mDeviceListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
				Log.d("XXX", "device=" + device);
				MainActivity.this.startBluetoothPairing(device);
			}
		});
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			showDialog(R.string.bluetooth_not_supported, "");
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
		}
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
	    for (BluetoothDevice device : pairedDevices) {
	        // Add the name and address to an array adapter to show in a ListView
	    	mDeviceListAdapter.add(device);
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
        if (requestCode == REQUEST_BT_ENABLE) { 
            // the user has chosen not to enable Bluetooth
        	if (resultCode != Activity.RESULT_OK) {
                finish();
        	}
        	
            return;
        }

		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (result != null) {
			String contents = result.getContents();
			if (contents != null) {
				showDialog(R.string.result_succeeded, result.toString());
			} else {
				showDialog(R.string.result_failed, getString(R.string.result_failed_why));
			}
		}
	}

	public void scanQRCode(View v) {
		openBluetoothSettings();
		//startBluetoothPairing();
		
		/*
		IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
		*/
	}

	private void showDialog(int title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok_button, null);
		builder.show();
	}
	
	private void openBluetoothSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
	    startActivityForResult(settingsIntent, REQUEST_BT_SETTINGS);
	}
	
	private void startBluetoothPairing(BluetoothDevice device) {
		Intent pairingIntent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        //pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PIN);
        pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 1234);
        //device.setPin(new byte[]{1,2,3,4});
        //device.setPairingConfirmation(false);
        pairingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivityForResult(pairingIntent, REQUEST_BT_SETTINGS);
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
