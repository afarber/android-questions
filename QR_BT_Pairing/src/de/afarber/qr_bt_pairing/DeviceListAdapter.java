package de.afarber.qr_bt_pairing;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
    
    private LayoutInflater mInflator;

    public DeviceListAdapter(Context context) {
		super(context, R.layout.rowlayout);
        mInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        if (getPosition(device) == -1) {
        	Log.d("DeviceListAdapter", "Adding: " + device.getAddress());
            add(device);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(android.R.id.text1);
            viewHolder.deviceAddress = (TextView) view.findViewById(android.R.id.text2);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = getItem(i);
        String address = device.getAddress();
        
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        
        viewHolder.deviceAddress.setText(address);
        return view;
    }
}

