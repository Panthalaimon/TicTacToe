package com.example.tictactoe;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Steffen Hanzlik
 * Matriclation number: 1207417
 *
 * WinningActivity just show the winner or draw and a play again button
 * which handles the user back to the mainactivity
 */
public class WinningActivity extends AppCompatActivity {
    TextView winnerView ;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    /**
     * on pressing the play again button the activity changes to the mainActivity
     * @param view
     */
    public void playAgain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        winnerView.setText("");
        Log.d(LOG_TAG,"Play Again!");

    }

    /**
     * set the intent sended strings in the text files
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);


        winnerView = findViewById(R.id.winnerView);
        String winnerText = getIntent().getExtras().getString("winner");
        winnerView.setText(winnerText);
    }
}