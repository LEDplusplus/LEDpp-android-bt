package de.lrapp.ledpp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import java.util.Set;
import java.util.UUID;

public class BtComm {

    String TAG = "LEDpp - BTComm";

    // Bluetooth stuff
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBtSocket;
    Object[] pairedDevicesArray;

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
            pairedDevicesArray = pairedDevicesSet.toArray();
            for(int i = 0; i < pairedDevicesArray.length; i++) {
                // create BluetoothDevice from actual mac address to get the name
                BluetoothDevice actual_bt_device = mBluetoothAdapter.
                        getRemoteDevice(pairedDevicesArray[i].toString());
                pairedDevsNames[i] = actual_bt_device.getName();
            }
        }
        return pairedDevsNames;
    }

    /**
     * Establishes a connection to the selected device
     * @param position Position inside pairedDevicesArray
     * @return true, if connecting was successful, false otherwise
     */
    public boolean connect(int position) {
        // get bluetooth mac address and create device
        String remoteDeviceMac = pairedDevicesArray[position].toString();
        BluetoothDevice mBtDevice = mBluetoothAdapter.getRemoteDevice(remoteDeviceMac);
        // create socket
        try {
            mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            mBtSocket.connect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
