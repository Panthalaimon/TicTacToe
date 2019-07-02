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

    private static final boolean bool = true;
    private static final String TAG = "BluetoothGameService";

    private static final String app_name = "TicTacToe";
    private static final UUID uuid = UUID.fromString("d256873e-97d4-11e9-bc42-526af7764f64");


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

//        for(int i=0;i<8;i++){
//            if(State[i] ==1 || State[i] == 0){
//                dropIn(position[i]);
//            }
//        }

        position[0] = findViewById(R.id.imageButton0);
        position[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a;
                globalTag = 0;
                String toSent = String.valueOf(State[globalTag]);
                a = sendReceive.write(toSent.getBytes());
                if(a>-1) {
                    State[globalTag] = a;
                    dropIn(view);
                }
            }
        });
        position[1] = findViewById(R.id.imageButton1);
        position[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                globalTag = 1;
                String toSent = String.valueOf(State[globalTag]);
                sendReceive.write(toSent.getBytes());
                dropIn(view);

            }
        });
        position[2] = findViewById(R.id.imageButton2);
        position[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                globalTag = 2;
                String toSent = String.valueOf(State[globalTag]);
                sendReceive.write(toSent.getBytes());
                System.out.print(toSent.getBytes());
                dropIn(view);
            }
        });
        position[3] = findViewById(R.id.imageButton3);

        position[4] = findViewById(R.id.imageButton4);
        position[5] = findViewById(R.id.imageButton5);
        position[6] = findViewById(R.id.imageButton6);
        position[7] = findViewById(R.id.imageButton7);
        position[8] = findViewById(R.id.imageButton8);


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
                        int[] actualState = stringToIntegerArray(tempMsg);
                        globalTag = actualState[0];
                        int size = actualState.length;
                        State[globalTag] = 0;

                        status.setText("MessageReceived!");


                        break;
                }

                return true;
            }
        }
    });


    /**
     * ==================================================================================================
     * Server class
     */
    private class ServerClass extends Thread {
        private final BluetoothServerSocket serverSocket;

        public ServerClass() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(app_name, uuid);
                Toast.makeText(getApplicationContext(), "ServerSocket Class constructor Successful!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            serverSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
//                try
//                {

                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                handler.sendMessage(message);

//                    socket = serverSocket.accept();
//                }catch (IOException e)
//                {
//                    e.printStackTrace();
//                    Message message = Message.obtain();
//                    message.what=STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message);
//                }

//                if(socket != null){


//                    Toast.makeText(getApplicationContext(),"sending to manage connected socket Successful!",Toast.LENGTH_LONG).show();

                sendReceive = new SendReceive(serverSocket);
                sendReceive.start();

                break;
            }
        }
//        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * Clientclass
     * ================================================================
     */

    private class ClientClass extends Thread {


        private BluetoothDevice device;

        public ClientClass(BluetoothDevice mDevice) {

            device = mDevice;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectTread");
            setName("ConnectThread");
            bluetoothAdapter.cancelDiscovery();

//            try{
//                socket.connect();
//                Message message = Message.obtain();
//                message.what = STATE_CONNECTED;
//                handler.sendMessage(message);

            sendReceive = new SendReceive(device);
            sendReceive.start();

//            }catch(IOException e){
//                connectionFailed();
//            }

        }
//
//        public void cancel(){
//            try{
//                socket.close();
//            }catch(IOException e){
//                Log.e(TAG, "close() of connect socket failed", e);
//            }
//        }

    }

    /**
     * for managing Connected Sockets
     */

    private class SendReceive extends Thread {
        BluetoothSocket bluetoothSocket;
        BluetoothDevice bluetoothDevice;
        InputStream inStream;
        OutputStream outStream;
        BluetoothServerSocket bluetoothServerSocket;

        public SendReceive(BluetoothDevice device) {

            bluetoothDevice = device;
        }

        public SendReceive(BluetoothServerSocket socket) {
            bluetoothServerSocket = socket;
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
                            token =true;
                            //    bytes = inStream.read(buffer);
                            handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                            break;
                        }
                    }
                    //       bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("something wrong in line 500");
                }
            }
        }

        public int write(byte[] bytes) {
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



//
//    private void connectionFailed() {
//        setState(STATE_LISTEN);
//
//        // Send a failure message back to the Activity
//        Message message = handler.obtainMessage(5);
//        Bundle bundle = new Bundle();
//        bundle.putString("toast", "Unable to connect to device");
//        message.setData(bundle);
//        handler.sendMessage(message);
//    }

    private void setState(int newState){
        if(bool){
            Log.d(TAG,"setState()" + state + "to" + newState);
        }
        state = newState;
        handler.obtainMessage();
    }


    /**
     * ============================================================
     * Bluetooth enabling functions
     *
     */

    private void setGlobalState(int state[]){
        State = state;
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
                ServerClass serverClass = new ServerClass();
                serverClass.start();
                }else{
                    Toast.makeText(getApplicationContext(),"You must enable Bluetooth on your device!",Toast.LENGTH_LONG).show();
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btArray [i]);
                clientClass.start();

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
     * @param view
     */

    public void reset(View view){
        State = new  int []{2,2,2,2,2,2,2,2,2};
        ImageView image= (ImageView) view;
        activePlayer = 1;
        image.setTranslationY(-1500);
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

}