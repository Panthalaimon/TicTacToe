package com.example.tictactoe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class ClientClass extends Thread {

    private static final String TAG = "BluetoothGameService";
    private BluetoothDevice device;
    BluetoothAdapter bluetoothAdapter;
    SendReceive sendReceive;

    public ClientClass(BluetoothDevice mDevice) {

        device = mDevice;
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

        sendReceive = new SendReceive(device);
        sendReceive.start();

//            }catch(IOException e){
//                connectionFailed();
//            }

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