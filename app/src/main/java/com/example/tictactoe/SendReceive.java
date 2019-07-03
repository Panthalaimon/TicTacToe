package com.example.tictactoe;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.tictactoe.BluetoothEn.STATE_CONNECTED;
import static com.example.tictactoe.BluetoothEn.STATE_MESSAGE_RECEIVED;

public class SendReceive extends Thread {
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    InputStream inStream;
    OutputStream outStream;
    BluetoothServerSocket bluetoothServerSocket;
    Handler handler;
    boolean token = true;
    private static final UUID uuid = UUID.fromString("d256873e-97d4-11e9-bc42-526af7764f64");

    public SendReceive(BluetoothDevice device, Handler handler) {
        this.handler = handler;
        bluetoothDevice = device;
    }

    public SendReceive(BluetoothServerSocket socket, Handler handler) {
        bluetoothServerSocket = socket;
        this.handler = handler;

    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        InputStream tempIn = null;
        OutputStream tempOut = null;
        try {
            if (bluetoothDevice != null) {
                token = false;
                bluetoothSocket = (bluetoothDevice.createRfcommSocketToServiceRecord(uuid));

                bluetoothSocket.connect();
            } else {
                token = true;
                bluetoothSocket = bluetoothServerSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {

                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                try {
                    tempIn = bluetoothSocket.getInputStream();
                    tempOut = bluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                inStream = tempIn;
                outStream = tempOut;
                bytes = -1;
                while (true) {

                    if ((bytes = inStream.read(buffer)) != -1) {
                        token = true;
                        //    bytes = inStream.read(buffer);
                        handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("something wrong in line 500");
            }
        }
    }

    public int write(byte[] bytes) {
        if (token) {
            try {
                outStream.write(bytes);
                token =false;
                //TODO Check if win

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bluetoothServerSocket != null) {
                return 0;
            } else return 1;
        }return -1;
    }
}

