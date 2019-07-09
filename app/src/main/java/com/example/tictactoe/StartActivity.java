package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Steffen Hanzlik
 * Matriculation number: 1207417
 * https://github.com/Panthalaimon/TicTacToe.git
 *
 * Start activity just for chooseing the one Device mode
 * or two device mode
 *
 */

public class StartActivity extends AppCompatActivity {

    private Button about;
    private Button instruction;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    Intent intent;

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

        about = findViewById(R.id.about);
        instruction = findViewById(R.id.instruction);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAbout();
            }
        });

        instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInstructionDialog();
            }
        });
    }

    private void openDialogAbout() {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.show(getSupportFragmentManager(),"About");
    }

    public void openInstructionDialog(){
       InstructionDialog instructionDialog =new InstructionDialog();
        instructionDialog.show(getSupportFragmentManager(),"Instruction");
    }

    @Override
    public void onBackPressed(){
        int pid = android.os.Process.myPid();
        finish();
        android.os.Process.killProcess(pid);
    }
    public void onDestroy() {
        super.onDestroy();
    }
}