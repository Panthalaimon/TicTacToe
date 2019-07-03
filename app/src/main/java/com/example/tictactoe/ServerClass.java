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

        public ServerClass() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(app_name, uuid);
                Toast.makeText(context,"ServerSocket Class constructor Successful!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            serverSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
//                try
//                {

                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                handler.sendMessage(message);

//                    socket = serverSocket.accept();
//                }catch (IOException e)
//                {
//                    e.printStackTrace();
//                    Message message = Message.obtain();
//                    message.what=STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message);
//                }

//                if(socket != null){


//                    Toast.makeText(getApplicationContext(),"sending to manage connected socket Successful!",Toast.LENGTH_LONG).show();

                sendReceive = new SendReceive(serverSocket);
                sendReceive.start();

                break;
            }
        }
//        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
    }

