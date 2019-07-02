package com.example.tictactoe;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;



/**
 * @author Steffen Hanzlik
 * Matriculation Number: 1207417
 * MainActivity
 */
public class  MainActivity extends AppCompatActivity {

    int activePlayer =1;

    // 0: is  circle; 1: is cross; 2: is empty
    int [] State = {2,2,2,2,2,2,2,2,2};

    //winning Position

    int [] [] winningPos = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};

    boolean gameStarted = true;

    /**
     * this function lets the images fall down from top
     * @param view
     */
    // animate the  O or X fall down from the top
    public void dropIn(View view){
        ImageView image= (ImageView) view;
        Log.i("Tag",image.getTag().toString());

        int taget = Integer.parseInt(image.getTag().toString());


        // if in the taget State is 2 and the game is started tag this image with the number
        // of the active player
        if(State[taget] == 2 && gameStarted){
            State[taget] = activePlayer;
            System.out.println("State =========  " + State[taget]);
            image.setTranslationY(-1500);
            // set which player is on it
            if(activePlayer == 1){
                //Player1
                image.setImageResource(R.drawable.x);
                activePlayer =0;

            } else if(activePlayer == 0){
                //Player2
                image.setImageResource(R.drawable.o);
                activePlayer =1;

            }


            // lets fall down the picture
            image.animate().translationYBy(1500).setDuration(300);

            /**
             * if one of the winningpositions is correct  look which player solved three in a row
             */

            for(int[] winning:winningPos) {
                Intent intent = new Intent(MainActivity.this, WinningActivity.class);
                if (State[winning[0]] == State[winning[1]] && State[winning[1]] == State[winning[2]] && State[winning[0]] != 2) {
                    String winnerText = "";
                    gameStarted = false;
                    // if player one solves three in a row put extra into the intent the winner is
                    // and player 1
                    if (activePlayer == 1) {
                        winnerText = "Player2";
                        intent.putExtra("winnerIs", "The Winner is:");
                        intent.putExtra("winner", winnerText);

                        // if player one solves three in a row put extra into the intent the winner is
                        // and player 2
                    } else if (activePlayer == 0) {
                        winnerText = "Player1";
                        intent.putExtra("winnerIs", "The Winner is:");
                        intent.putExtra("winner", winnerText);

                    }

                    startActivity(intent);
                    reset(view);

                    // TODO if the last try is completed to three in a row there will be
                    // TODO also draw
                    // draw doesnt work in the moment
                }else
                    {
                    gameStarted = false;
                    for(int counterState :State)
                    {
                        if(counterState == 2)
                        {
                            gameStarted = true;}

                    }if(!(gameStarted))
                    {
                        intent.putExtra("winner", "Nobody you play a draw");
                        startActivity(intent);
                        reset(view);
                    }

                    }
            }
        }
    }

    /**
     * resets the view after changing the activity
     * so the player can not regocnize it when the press the play again button in the
     * winnning activity
     * @param view
     */
    public void reset(View view){
        State = new  int []{2,2,2,2,2,2,2,2,2};
        ImageView image= (ImageView) view;
        activePlayer = 1;
        image.setTranslationY(-1500);
    }

    /**
     *
     * @param savedInstanceState
     */
private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





    }


}