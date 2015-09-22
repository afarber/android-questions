package de.afarber.qr_bt_pairing;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();

    protected static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView deviceName;
        private TextView deviceAddress;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String address = deviceAddress.getText().toString();

            Toast.makeText(v.getContext(),
                    "How to call MainActivity.confirmConnection(address)?",
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.rowlayout,
                parent,
                false);
        ViewHolder vh = new ViewHolder(v);
        vh.deviceName = (TextView) v.findViewById(R.id.text1);
        vh.deviceAddress = (TextView) v.findViewById(R.id.text2);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        BluetoothDevice device = mDevices.get(position);
        String address = device.getAddress();

        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            vh.deviceName.setText(deviceName);
        else
            vh.deviceName.setText(R.string.unknown_device);

        vh.deviceAddress.setText(address);
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void add(BluetoothDevice device) {
        if (!mDevices.contains(device)) {
            mDevices.add(device);
            notifyDataSetChanged();
        }
    }
}
