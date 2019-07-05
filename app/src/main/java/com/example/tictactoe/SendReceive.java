package com.example.tictactoe;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

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
    int[] State = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winningPos = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    boolean gameStarted = true;
    int activePlayer = 1;
    Context context;

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
        context =BluetoothEn.getContext();
        Intent myIntent = new Intent(context.getApplicationContext(), WinningActivity.class);
        for (int[] winning : winningPos) {
            if (State[winning[0]] == State[winning[1]] && State[winning[1]] == State[winning[2]] && State[winning[0]] != 2) {
                String winnerText = "";
                gameStarted = false;
                // if player one solves three in a row put extra into the intent the winner is
                // and player 1
                if (activePlayer ==1) {
                    winnerText = "Player2";
                    myIntent.putExtra("winnerIs", "The Winner is:");
                    myIntent.putExtra("winner", winnerText);
                    context.startActivity(myIntent);
                    // if player one solves three in a row put extra into the intent the winner is
                    // and player 2
                } else if (activePlayer ==2) {
                    winnerText = "Player1";
                    myIntent.putExtra("winnerIs", "The Winner is:");
                    myIntent.putExtra("winner", winnerText);
                    context.startActivity(myIntent);
                }
            }



        }
        if (token) {
            try {
                outStream.write(bytes);
                token =false;


            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bluetoothServerSocket != null) {
                return 0;
            } else return 1;
        }return -1;
    }


}



