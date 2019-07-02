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

/**
 * @author Steffen Hanzlik
 * Matriculation number: 1207417
 *
 * Start activity just for chooseing the one Device mode
 * or two device mode
 *
 */

public class StartActivity extends AppCompatActivity {

    // open Mainactivty class
    public  void startGameSingle(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    //open Bluetooth_enable_Activity class
    public void startGameDual(View view){
        Intent intent = new Intent(this, BluetoothEn.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

}