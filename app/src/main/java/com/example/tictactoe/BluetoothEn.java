package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothEn extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    Button buttonOn;
    Button buttonOff;
    BluetoothAdapter bluetoothAdapter;
    //Intent bluEnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_en);

        buttonOn = findViewById(R.id.bluOn);
        buttonOff = findViewById(R.id.bluOff);
        bluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
      //  bluEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activateBluetooth();
        disableBluetooth();
    }

    private void disableBluetooth() {
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isEnabled()){
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

    private void activateBluetooth(){

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
