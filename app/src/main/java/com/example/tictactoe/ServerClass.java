package com.example.tictactoe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.UUID;

import static com.example.tictactoe.BluetoothEn.STATE_CONNECTING;

public class ServerClass extends Thread {
        private final BluetoothServerSocket serverSocket;
        BluetoothAdapter bluetoothAdapter;
        private static final String app_name = "TicTacToe";
        private static final UUID uuid = UUID.fromString("d256873e-97d4-11e9-bc42-526af7764f64");
        Handler handler ;
        SendReceive sendReceive;
        Context context;

        public ServerClass(Handler handler, BluetoothAdapter adap, Context context) {
            this.handler = handler;
            this.context = context;
            this.bluetoothAdapter = adap;
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(app_name, uuid);
                Toast.makeText(context,"Waiting for other device to bind....", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            serverSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {

                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                handler.sendMessage(message);

                sendReceive = new SendReceive(serverSocket, handler);
                sendReceive.start();

                break;
            }
        }

    public SendReceive getSendReceive() {
        while (sendReceive == null);
        return sendReceive;
    }


        public void cancel() {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
    }

