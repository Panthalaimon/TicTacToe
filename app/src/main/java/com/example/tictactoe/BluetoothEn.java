package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.Menu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * @author Steffen Hanzlik
 * Matriculation number: 1207417
 * https://github.com/Panthalaimon/TicTacToe.git
 * ==========================================================================
 * Bluetooth_Enable_Activity
 */


public class BluetoothEn extends AppCompatActivity {

    int activePlayer = 1;
    public static int globalTag = 2;
    public static int buttonPressed = 0;
   private static Context mContext;

    private int state;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_DISCONNECTED = 4;
    public static final int STATE_MESSAGE_RECEIVED = 5;
    public static final int STATE_CONNECTION_FAILED = 6;
    private final static int REQUEST_ENABLE_BT = 7;
    // 0: is  circle; 1: is cross; 2: is empty
    public static int[] State = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    //winning Position

    int[][] winningPos = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    boolean gameStarted = true;

    ClientClass clientClass;
    ServerClass serverClass;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    ListView listView;
    TextView status;
    Button playAgain;

    SendReceive sendReceive;


    Button listen;
    Button showDevice;


    int[] refs = {
            R.id.imageButton0,
            R.id.imageButton1,
            R.id.imageButton2,
            R.id.imageButton3,
            R.id.imageButton4,
            R.id.imageButton5,
            R.id.imageButton6,
            R.id.imageButton7,
            R.id.imageButton8,

    };

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_en);

        playAgain =findViewById(R.id.playAgainLow);
        status = findViewById(R.id.status);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listen = findViewById(R.id.listenButton);
        showDevice = findViewById(R.id.showButton);
        listView = findViewById(R.id.deviceList);
        implementListener();

        mContext =getApplicationContext();

        ImageView[] position = new ImageView[9];



        for (int i = 0; i < 9; i++) {
            position[i] = findViewById(refs[i]);
            final int j = i;
            position[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int a;
                    globalTag = j;

                    if (State[globalTag] == 2) {
                        a = sendReceive.write(String.valueOf(globalTag).getBytes());
                        if(a>-1) {
                            State[globalTag] = a;
                            activePlayer = a;
                            dropIn(view);

                        }
                    }
                }
            });
        }

    }

    /**
     * ============================================================================================
     * Handler for handling bluetooth connection
     */

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            {
                switch (message.what) {
                    case STATE_LISTEN:
                        status.setText("Listening");

                        break;
                    case STATE_CONNECTING:
                        status.setText("Connecting...");
                        break;
                    case STATE_CONNECTED:
                        status.setText("Connected");
                        hideList();
                        break;
                    case STATE_CONNECTION_FAILED:
                        status.setText("Connection Failed");
                        break;
                    case STATE_MESSAGE_RECEIVED:

                        byte[] readBuff = (byte[]) message.obj;
                        String tempMsg = new String(readBuff, 0, message.arg1);
                        globalTag = Integer.valueOf(tempMsg);
                        if (serverClass != null) {
                            State[globalTag] = 1;
                            activePlayer =1;
                        }
                        else{
                            State[globalTag] = 0;
                            activePlayer =0;
                        }

                        dropIn(findViewById(refs[globalTag]));
                        status.setText("MessageReceived!");


                }

                return true;
            }
        }
    });





    /**
     * for managing Connected Sockets
     */


    /**
     * ============================================================
     * Bluetooth enabling functions
     *
     */



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


    /**
     * Button listener listen Button, ListView, Show Devices Button
     */

    private void implementListener() {

        hideButton();

        showDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    if (buttonPressed == 0) {
                        buttonPressed = 1;
                    } else {
                        buttonPressed = 0;
                    }
                    if (buttonPressed == 1) {

                        showList();
                        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                        String[] strings = new String[bt.size()];

                        int i = 0;

                        if (bt.size() > 0) {
                            btArray = new BluetoothDevice[bt.size()];
                            for (BluetoothDevice device : bt) {

                                btArray[i] = device;
                                strings[i] = device.getName();
                                i++;
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, strings);
                            listView.setAdapter(arrayAdapter);
                        }
                    }
                    if (buttonPressed == 0) {
                        hideList();



                    }
                }else{
                    bluetoothAdapter.enable();
                    Toast.makeText(getApplicationContext(),"activate bluetooth press second time show device button!",Toast.LENGTH_LONG).show();
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    if (buttonPressed == 0) {
                        buttonPressed = 1;

                    } else {
                        buttonPressed = 0;

                    }
                    if (buttonPressed == 1) {
                        serverClass = new ServerClass(handler, bluetoothAdapter, getApplicationContext());
                        serverClass.start();
                        sendReceive = serverClass.getSendReceive();
                    } else {
                        bluetoothAdapter.enable();
                        Toast.makeText(getApplicationContext(),"activate bluetooth press second time show device button!",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clientClass = new ClientClass(btArray [i], bluetoothAdapter, handler);
                clientClass.start();
                sendReceive = clientClass.getSendReceive();
                status.setText("Connecting...");

            }
        });

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset(view);
            }
        });


    }



    /**
     * show at onClick Image View Crosses or Circles
     * @param view
     */
    public void dropIn(View view){
        ImageView image= (ImageView) view;
        Log.i("Tag",image.getTag().toString());
        int taget = Integer.parseInt(image.getTag().toString());

            // where the magic comes out
            image.setTranslationY(-1500);
            // set which player is on it
            if(State[taget]== 1){
                //Player1
                image.setImageResource(R.drawable.x);

            } else if(State[taget] == 0){
                //Player2
                image.setImageResource(R.drawable.o);

            }

            image.animate().translationYBy(1500).setDuration(300);

        Intent intent = new Intent(BluetoothEn.this, WinningActivity.class);
        for(int[] winning:winningPos) {
            boolean winnerTextShow =false;

            if (State[winning[0]] == State[winning[1]] && State[winning[1]] == State[winning[2]] && State[winning[0]] != 2) {
                String winnerText = "";


                // if player one solves three in a row put extra into the intent the winner is
                // and player 1
                if (activePlayer == 1) {
                    gameStarted = false;
                    winnerText = "Player 2";
                    intent.putExtra("winnerIs", "The Winner is:");
                    intent.putExtra("winner", winnerText);
                    intent.putExtra("state", State);
                    winnerTextShow =true;
                    startActivity(intent);
                    reset(view);


                    // if player one solves three in a row put extra into the intent the winner is
                    // and player 2
                } else if (activePlayer == 0) {
                    gameStarted = false;
                    winnerText = "Player 1";
                    intent.putExtra("winnerIs", "The Winner is:");
                    intent.putExtra("winner", winnerText);
                    intent.putExtra("state", State);
                    winnerTextShow =true;
                    startActivity(intent);
                    reset(view);
                }



                // TODO if the last try is completed to three in a row there will be
                // TODO also draw
                // draw doesnt work in the moment


            }
        }
        if (testState(State)){

            for(int counterState :State) {
                if (counterState == 2) {
                    gameStarted = true;
                }else gameStarted =false;

            }if(testState(State)) {
                intent.putExtra("winnerIs", "Try again!");
                intent.putExtra("winner", "DRAW");
                intent.putExtra("state", State);
                startActivity(intent);



            }
        }
    }



    /**
     *
     *  helping function to convert String to an Integer Array because input streams just can
     *  convert strings to bytes and bytes to strings
     * @param s
     * @return
     */

    /**
     * to reset game after changing to the winner class
     *
     */

    public void reset(View view){

        ImageView[] position = new ImageView[9];

        for(int i=0; i<9;i++){
            State[i] =2;
            // State[i]= sendReceive.write(String.valueOf(2).getBytes());
            findViewById(refs[i]).setTranslationY(-1500);
        }
        gameStarted =true;
        globalTag= 1;
        hideButton();


       //sendReceive.cancel();
    }

    /**
     * ================================================================================================================
     * UI functions
     */

    /**
     *
     */
    protected void showList(){
        listView.setVisibility(View.VISIBLE);

    }

    protected void showButton(){
        playAgain.setVisibility(View.VISIBLE);

    }
    protected void hideButton(){
        playAgain.setVisibility(View.INVISIBLE);
    }

    /**
     *
     */
    protected void hideList(){
        listView.setVisibility(View.INVISIBLE);
    }

    /**
     * finishes the application
     */

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        startActivity(new Intent(BluetoothEn.this, StartActivity.class));
        sendReceive.cancel();
    }


    /**
     * for testing gamestate and activate draw activtity
     *
     * @param mState
     * @return
     */
    public boolean testState(int[] mState) {
        boolean state = true;
        int tmp = 0;
        for (int i = 0; i < 9; i++) {
            if (State[i] == 2) {
                state = false;
            }


        }
        return state;
    }

}