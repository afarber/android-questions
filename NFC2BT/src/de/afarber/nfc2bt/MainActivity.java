package de.afarber.nfc2bt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	public final String KEALGO = "ECDH";
	public final int KEYSIZE = 192;
	public BluetoothAdapter mBluetoothAdapter;
	public NfcAdapter mNfcAdapter;
	public NdefMessage mNdefMessage;
	public String myAddress;
	public String theirAddress;
	public KeyPair keys;
	public KeyAgreement ka;
	private TextView status;
	private PendingIntent mPendingIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mBluetoothAdapter == null || mNfcAdapter == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Your device does not support the required functions to run this application")
					.setCancelable(false)
					.setNeutralButton("Quit",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									MainActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}

		myAddress = mBluetoothAdapter.getAddress();

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		status = (TextView) findViewById(R.id.status);

		try {

			KeyPairGenerator kg = KeyPairGenerator.getInstance(KEALGO, "BC2"); // does not work
			kg.initialize(192, new SecureRandom());
			keys = kg.generateKeyPair();
			ka = KeyAgreement.getInstance(KEALGO, "BC2");
			ka.init(keys.getPrivate());

			NdefRecord[] records = new NdefRecord[2];

			records[0] = new NdefRecord(NdefRecord.TNF_UNKNOWN, new byte[0],
					new byte[0], myAddress.getBytes());
			records[1] = new NdefRecord(NdefRecord.TNF_UNKNOWN, new byte[0],
					new byte[0], keys.getPublic().getEncoded());
			mNdefMessage = new NdefMessage(records);
		} catch (Exception e) {
			Log.i("NFCtoBluetoothTest", e.getMessage());
		}
	}

	public void doFinalKey(byte[] keybytes) {

		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEALGO, "BC2");
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keybytes);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			ka.doPhase(publicKey, true);
			SecretKey bluetoothkey = ka.generateSecret("RC4");

			this.status.setText("DH key exchanged - "
					+ new String(android.util.Base64.encode(
							bluetoothkey.getEncoded(), 0)));
			
			BluetoothDevice device = mBluetoothAdapter
					.getRemoteDevice(theirAddress);




			/*try {
				Class cls = Class.forName(("android.bluetooth.BluetoothDevice"));
				Method m = cls.getMethod("setPairingConfirmation", boolean.class);
				m.setAccessible(true);
				m.invoke(device, false);
				
				m = cls.getMethod("createBondOutOfBand", byte[].class, byte[].class);
				m.setAccessible(true);
				m.invoke(device, bluetoothkey.getEncoded(), bluetoothkey.getEncoded());
				
			} catch (SecurityException ex) {
				Log.i("NFCtoBluetoothTest", ex.getMessage());
			}*/

			//BluetoothServerSocket ss = mBluetoothAdapter
					//.listenUsingRfcommWithServiceRecord("CartoonRacer",
					//		UUID.fromString("CartoonRacer"));
			//BluetoothSocket socket = ss.accept();
			//socket.close();
			//ss.close();

		} catch (Exception e) {
			Log.i("NFCtoBluetoothTest", e.getMessage());
		}
	}

	public void onResume() {
		super.onResume();
		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
					null);
			mNfcAdapter.enableForegroundNdefPush(this, mNdefMessage);
		}
	}

	public void onPause() {
		super.onPause();
		if (mNfcAdapter != null)
			mNfcAdapter.disableForegroundNdefPush(this);
	}

	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage message = ((NdefMessage) rawMsgs[0]);
			NdefRecord[] records = message.getRecords();
			this.theirAddress = new String(records[0].getPayload());
			Log.i("NFCtoBluetoothTest", "TheirAddress - " + theirAddress);
			doFinalKey(records[1].getPayload());
		}
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
