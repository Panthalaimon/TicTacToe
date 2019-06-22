package com.example.tictactoe;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WinningActivity extends AppCompatActivity {
    TextView winnerView ;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();





    public void playAgain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        winnerView.setText("");
        Log.d(LOG_TAG,"Play Again!");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);


        winnerView = findViewById(R.id.winnerView);
        String winnerText = getIntent().getExtras().getString("winner");
        winnerView.setText(winnerText);
    }
}