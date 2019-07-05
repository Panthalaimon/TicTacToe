package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 * ==========================================================================
 * Bluetooth_Enable_Activity
 */


public class BluetoothEn extends AppCompatActivity {

    int activePlayer = 1;
    public static int globalTag = 2;
    public static int buttonPressed = 0;
    private static BluetoothEn mContext;

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
    int[] State = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    //winning Position

    int[][] winningPos = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    boolean gameStarted = true;
    boolean token = true;
    boolean isWinner=false;

    private static final boolean bool = true;
    private static final String TAG = "BluetoothGameService";

    private static final String app_name = "TicTacToe";
    private static final UUID uuid = UUID.fromString("d256873e-97d4-11e9-bc42-526af7764f64");

    ClientClass clientClass;
    ServerClass serverClass;
    BluetoothAdapter bluetoothAdapter;
    ServerClass BluetoothServer;
    ClientClass BluetoothClient;
    BluetoothDevice[] btArray;
    ListView listView;
    TextView status;


    SendReceive sendReceive;


    Button listen;
    Button showDevice;

    String DeviceMACAdress;
    private ArrayAdapter<String> mAdapter;

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


        status = findViewById(R.id.status);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listen = findViewById(R.id.listenButton);
        showDevice = findViewById(R.id.showButton);
        listView = findViewById(R.id.deviceList);
        implementListener();

        ImageView[] position = new ImageView[9];



        for (int i = 0; i < 9; i++) {
            position[i] = findViewById(refs[i]);
            final int j = i;
            position[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int a;
                    globalTag = j;
                    String toSent;
                    if (State[globalTag] == 2) {
                        a = sendReceive.write(String.valueOf(globalTag).getBytes());
                        if(a>-1) {
                            State[globalTag] = a;
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

                        // TODO check if win
                        status.setText("MessageReceived!");


                        for (int[] winning : winningPos) {
                            Intent intent = new Intent(BluetoothEn.this, WinningActivity.class);
                            if (State[winning[0]] == State[winning[1]] && State[winning[1]] == State[winning[2]] && State[winning[0]] != 2) {
                                String winnerText = "";
                                gameStarted = false;
                                // if player one solves three in a row put extra into the intent the winner is
                                // and player 1
                                if (activePlayer ==1) {
                                    winnerText = "Player2";
                                    intent.putExtra("winnerIs", "The Winner is:");
                                    intent.putExtra("winner", winnerText);
                                    reset();

                                    // if player one solves three in a row put extra into the intent the winner is
                                    // and player 2
                                } else if (activePlayer ==2) {
                                    winnerText = "Player1";
                                    intent.putExtra("winnerIs", "The Winner is:");
                                    intent.putExtra("winner", winnerText);
                                    startActivity(intent);
                                    reset();

                                }

                            }

                        }

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

                    Toast.makeText(getApplicationContext(),"You must enable Bluetooth on your device!",Toast.LENGTH_LONG).show();
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isEnabled()){
                serverClass = new ServerClass(handler, bluetoothAdapter, getApplicationContext());
                serverClass.start();
                sendReceive = serverClass.getSendReceive();
                }else{
                    Toast.makeText(getApplicationContext(),"You must enable Bluetooth on your device!",Toast.LENGTH_LONG).show();
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

            //isWinner(State);


    }

    /**
     *
     *  helping function to convert String to an Integer Array because input streams just can
     *  convert strings to bytes and bytes to strings
     * @param s
     * @return
     */

    public int[] stringToIntegerArray(String s){
        int size = s.length();
        int[] intArray = new int[size];

        for(int i=1; i<size;i++){
            intArray[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return intArray;
    }

    /**
     * to reset game after changing to the winner class
     *
     */

    public void reset(){
        State = new  int []{2,2,2,2,2,2,2,2,2};
        activePlayer = 1;
        for(int i=0;i<9;i++) {
            findViewById(refs[i]).setTranslationY(-1500);
        }
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

        if(BluetoothServer.isAlive()){
            BluetoothServer.cancel();
        }
        if(BluetoothClient.isAlive()){
            //BluetoothClient.cancel();
        }
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }


        startActivity(new Intent(BluetoothEn.this, StartActivity.class));
        BluetoothEn.this.finish();
    }



    /**
     * free the bluetooth and client socket
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(BluetoothServer.isAlive()){
            BluetoothServer.cancel();
        }
        if(BluetoothClient.isAlive()){
            //BluetoothClient.cancel();
        }

    }
    public static BluetoothEn getContext() {
        return mContext;
    }

}