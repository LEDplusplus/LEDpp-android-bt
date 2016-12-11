package de.lrapp.ledpp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

public class BtComm {

    String TAG = "LEDpp - BTComm";

    // Bluetooth stuff
    BluetoothAdapter mBluetoothAdapter;

    /**
     * initializes the bluetooth adapter
     */
    protected void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * gets the paired devices and
     * @return the paired devices names
     */
    protected String[] getBondedDevs() {
        Set pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
        String[] pairedDevsNames = new String[pairedDevicesSet.size()];
        if (pairedDevicesSet.size() != 0) {
            Object[] pairedDevicesArray = pairedDevicesSet.toArray();
            for(int i = 0; i < pairedDevicesArray.length; i++) {
                // create BluetoothDevice from actual mac address to get the name
                BluetoothDevice actual_bt_device = mBluetoothAdapter.
                        getRemoteDevice(pairedDevicesArray[i].toString());
                pairedDevsNames[i] = actual_bt_device.getName();
            }
        }
    return pairedDevsNames;
    }
}
