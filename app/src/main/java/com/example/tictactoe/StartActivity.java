package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StartActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    /*
      if(bluetoothAdapter == null){
      Toast.makeText(this,"Your device do not support bluetooth",Toast.LENGTH_LONG).show();
  }

      if (!(bluetoothAdapter.isEnabled())) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
  }*/
    public void startGame(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
private class sendRecieve extends Thread{

    private final BluetoothSocket bluetoothSocket;
    private final InputStream iSt;
    private final OutputStream oSt;

    public sendRecieve(BluetoothSocket socket){

            bluetoothSocket = socket;
            InputStream tempInput = null;
            OutputStream tempOutput = null;

        try {
            tempInput = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            tempOutput =bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        iSt = tempInput;
        oSt = tempOutput;

    }



    }
}