package de.afarber.qr_bt_pairing;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "VwConnect";
    private final static int REQUEST_BT_ENABLE = 1;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceListAdapter mDeviceListAdapter;
    private IntentFilter mFilter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;

            String action = intent.getAction();
            Log.d(TAG, "action=" + action);

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mRefresh.setEnabled(false);
                    }
                });
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mRefresh.setEnabled(true);
                    }
                });
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceListAdapter.add(device);
                    }
                });
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int newState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                int oldState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                final BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null &&
                        newState == BluetoothDevice.BOND_BONDED &&
                        oldState != BluetoothDevice.BOND_BONDED) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            successfulConnection(device);
                        }
                    });
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mFilter = new IntentFilter();
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mDeviceListAdapter = new DeviceListAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbar.setTitle(getString(R.string.app_name));

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(Color.GRAY);
                mCollapsingToolbar.setContentScrimColor(mutedColor);
            }
        });

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRefresh.setEnabled(false);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh.setRefreshing(false);

                if (!mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.startDiscovery();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mDeviceListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(mReceiver, mFilter);
        if (mBluetoothAdapter.isEnabled()) {
            onActivityResult(REQUEST_BT_ENABLE, Activity.RESULT_OK, null);
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                if (!mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.startDiscovery();
            } else {
                Toast.makeText(this, R.string.bluetooth_not_enabled, Toast.LENGTH_LONG).show();
                finish();
            }

            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result == null) {
            Toast.makeText(this, R.string.scan_failed_1, Toast.LENGTH_LONG).show();
            return;
        }

        String address = result.getContents();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Toast.makeText(this, R.string.scan_failed_2, Toast.LENGTH_LONG).show();
            return;
        }

        confirmConnection(address);
    }

    public void confirmConnection(String address) {
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        String name = device.getName();
        if (name == null || name.length() == 0)
            name = getString(R.string.unknown_device);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_confirm) + name + " (" + device + ")?");
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mBluetoothAdapter.isDiscovering())
                    MainActivity.this.mBluetoothAdapter.cancelDiscovery();

                Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
                if (bondedDevices != null && bondedDevices.contains(device)) {
                    successfulConnection(device);
                } else {
                    device.createBond();
                }
            }
        });
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }

    private void successfulConnection(BluetoothDevice device) {

        String name = device.getName();
        if (name == null || name.length() == 0)
            name = getString(R.string.unknown_device);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_success) + name + " (" + device + ")!");
        builder.setPositiveButton(R.string.button_ok, null);
        builder.show();
    }

    public void scanQRCode(View v) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

}

