package com.example.tictactoe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import android.os.Handler;

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

//            try{
//                socket.connect();
//                Message message = Message.obtain();
//                message.what = STATE_CONNECTED;
//                handler.sendMessage(message);

        sendReceive = new SendReceive(device, handler);
        sendReceive.start();

//            }catch(IOException e){
//                connectionFailed();
//            }

    }
    public SendReceive getSendReceive() {
        while (sendReceive == null);
        return sendReceive;
    }
//
//        public void cancel(){
//            try{
//                socket.close();
//            }catch(IOException e){
//                Log.e(TAG, "close() of connect socket failed", e);
//            }
//        }

}