package com.example.tictactoe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import android.os.Handler;
/**
 * @author Steffen Hanzlik
 * Matriculation number: 1207417
 * ==========================================================================
 * Client Class
 */
public class ClientClass extends Thread {

    private static final String TAG = "BluetoothGameService";
    private BluetoothDevice device;
    BluetoothAdapter bluetoothAdapter;
    SendReceive sendReceive;
    Handler handler;

    public ClientClass(BluetoothDevice mDevice, BluetoothAdapter btadap, Handler h) {
        bluetoothAdapter =  btadap;
        device = mDevice;
        this.handler = h;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectTread");
        setName("ConnectThread");
        bluetoothAdapter.cancelDiscovery();



        sendReceive = new SendReceive(device, handler);
        sendReceive.start();


    }
    public SendReceive getSendReceive() {
        while (sendReceive == null);
        return sendReceive;
    }

}