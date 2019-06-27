package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothEn extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private static final boolean bool = true;
    private static final String TAG = "BluetoothGameService";

    private static final String name = "TicTacToe";
    private static final UUID uuid = UUID.fromString("d256873e-97d4-11e9-bc42-526af7764f64");

    BluetoothAdapter bluetoothAdapter;
    Handler handler;
    ServerClass BluetoothServer;
    ClientClass BluetoothClient;
    ConnectedThread BluetoothDataTransfer;

    TextView status;

    private int state;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_DISCONNECTED = 4;
    public static final int STATE_MESSAGE_RECEIVED= 5;
    public static final int  STATE_CONNECTION_FAILED = 6;
    Button buttonOn;
    Button buttonOff;

    //Intent bluEnable;

    String DeviceMACAdress;

    private ArrayAdapter<String> mAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_en);

        buttonOn = findViewById(R.id.bluOn);
        buttonOff = findViewById(R.id.bluOff);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //  bluEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activateBluetooth();
        disableBluetooth();
    }



    Handler BluetoothDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            status = findViewById(R.id.status);
            switch(message.what){
                case STATE_LISTEN:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting...");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
            }

            return true;
        }
    });


    /**
     *
     * Server class
     *
     */


    private class ServerClass extends Thread {
        private final BluetoothServerSocket serverSocket;

        public ServerClass(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("tictactoe",uuid);
                Toast.makeText(getApplicationContext(),"ServerSocket Class constructor Successful!",Toast.LENGTH_LONG).show();
            }catch (Exception e){
            }

            serverSocket = tmp;
        }

        @Override
        public void run(){
            BluetoothSocket socket;
            while (true){
                try{

                    Message message = Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                }catch (IOException e){

                    Message message = Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                    break;
                }

                if(socket != null){

                    ManageConnectedSocket(socket);

                    Message message = Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
                    Toast.makeText(getApplicationContext(),"sending to manage connected socket Successful!",Toast.LENGTH_LONG).show();
                    try{
                        serverSocket.close();
                    } catch (Exception e){}

                    break;
                }
            }
        }

        public void cancel(){
            try{
                serverSocket.close();
            }catch (Exception e){}
        }
    }





    private void disableBluetooth() {
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Blutetooth is Enable", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth Enabling Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void activateBluetooth() {

        buttonOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Your device does not support bluetooth", Toast.LENGTH_LONG).show();
                } else {

                    if (!(bluetoothAdapter.isEnabled())) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            }
        });


    }



    /**
     * for managing Connected Sockets
     * @param socket
     */

    public void ManageConnectedSocket(BluetoothSocket socket){
        BluetoothDataTransfer = new ConnectedThread(socket);
        BluetoothDataTransfer.start();

        Toast.makeText(getApplicationContext(),"Successful connection!",Toast.LENGTH_LONG).show();
    }

    /**
     * connected Threads
     */


    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                Toast.makeText(getApplicationContext(),"connected** Thread constructor Successful!",Toast.LENGTH_LONG).show();
            }catch (Exception e){}

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try{
                    bytes = mInStream.read(buffer);

                }catch (Exception e){
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                mOutStream.write(bytes);
            }catch (Exception e){}
        }

        public void cancel(){
            try{
                mSocket.close();
            }catch (Exception e){}
        }
    }

    private class ClientClass extends Thread{

        private BluetoothSocket mSocket ;
        private BluetoothDevice mDevice;

        public ClientClass(BluetoothDevice device){

            mDevice = device;
            try {
                mSocket = device.createRfcommSocketToServiceRecord(uuid);
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Couldn´t create mSocket for device Create line 286");
            }
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectTread");
            setName("ConnectThread");
            bluetoothAdapter.cancelDiscovery();

            try{
                mSocket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
            }catch(IOException e){
                connectionFailed();
            }

        }

        public void cancel(){
            try{
                mSocket.close();
            }catch(IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    }

    private void connectionFailed() {
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect to device");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void setState(int newState){
        if(bool){
            Log.d(TAG,"setState()" + state + "to" + newState);
        }
        state = newState;
        handler.obtainMessage();
    }



    /**
     * =======================================================================================
      */
    private class sendRecieve extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream iSt;
        private final OutputStream oSt;

        public sendRecieve(BluetoothSocket socket) {

            bluetoothSocket = socket;
            InputStream tempInput = null;
            OutputStream tempOutput = null;

            try {
                tempInput = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tempOutput = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            iSt = tempInput;
            oSt = tempOutput;

        }
    }

}