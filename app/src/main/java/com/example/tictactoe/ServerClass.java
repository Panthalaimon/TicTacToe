package com.example.tictactoe;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.widget.Toast;

    public class ServerClass extends Thread {
        private final BluetoothServerSocket serverSocket;

        public ServerClass() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(app_name, uuid);
                Toast.makeText(getApplicationContext(), "ServerSocket Class constructor Successful!", Toast.LENGTH_LONG).show();
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

                sendReceive = new BluetoothEn.SendReceive(serverSocket);
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

