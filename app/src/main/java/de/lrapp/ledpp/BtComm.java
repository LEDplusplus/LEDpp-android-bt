package de.lrapp.ledpp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BtComm {

    String TAG = "LEDpp - BTComm";

    // Bluetooth stuff
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBtSocket;
    ConnectedThread mConnThread;
    Object[] pairedDevicesArray;

    private boolean btSocketReady = false;

    /**
     * initializes the bluetooth adapter
     */
    protected void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * gets the paired devices and
     *
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
            startConnThread();
            btSocketReady = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Waits until socket is connected, starts connection thread
     */
    private void startConnThread() {
        new Thread(new Runnable() {
            public void run() {
                // wait until socket is connected
                while (true) {
                    if (mBtSocket.isConnected()) {

                        mConnThread = new ConnectedThread(mBtSocket);
                        mConnThread.start();
                        break;
                    }
                }
            }
        }).start();
    }


    /**
     * Class for connection thread
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mBtSocket;
        private final OutputStream mOutStream;

        /**
         * constructor
         * @param socket socket to connect to
         */
        public ConnectedThread(BluetoothSocket socket) {
            mBtSocket = socket;
            OutputStream tmpOut = null;

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutStream = tmpOut;

        }

        /**
         * writes a byte array into outstream
         * @param bytes data to send as byte array
         */
        public void write(byte[] bytes) {
            try {
                mOutStream.write(bytes);
                Log.i(TAG, "Command: " + bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * closes the connection
         */
        public void cancel() {
            try {
                mBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * passes data to connected thread write function
     * @param data data to send
     */
    public boolean send(byte[] data) {
        if (btSocketReady && data != null) {
            ConnectedThread mConnThread = new ConnectedThread(mBtSocket);
            mConnThread.write(data);
            return true;
        }
        return false;
    }
}
