package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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

public class BluetoothEn extends AppCompatActivity {

    int activePlayer =1;
    public static int globalTag;

    // 0: is  circle; 1: is cross; 2: is empty
    int [] State = {2,2,2,2,2,2,2,2,2};

    //winning Position

    int [] [] winningPos = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};

    boolean gameStarted = true;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final boolean bool = true;
    private static final String TAG = "BluetoothGameService";

    private static final String app_name = "TicTacToe";
    private static final UUID uuid = UUID.fromString("d256873e-97d4-11e9-bc42-526af7764f64");

    BluetoothAdapter bluetoothAdapter;
    //Handler handler;
    ServerClass BluetoothServer;
    ClientClass BluetoothClient;
    ConnectedThread BluetoothDataTransfer;
    BluetoothDevice [] btArray;
    ListView listView;
   // SendReceive sendReceive;
    TextView status;

    private int state;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_DISCONNECTED = 4;
    public static final int STATE_MESSAGE_RECEIVED= 5;
    public static final int STATE_CONNECTION_FAILED = 6;
    Button buttonOn;
    Button buttonOff;
    Button listen;
    Button showDevice;

    //Intent bluEnable;
    SendReceive sendReceive;

    String DeviceMACAdress;

    private ArrayAdapter<String> mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_en);


        status = findViewById(R.id.status);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listen =findViewById(R.id.listenButton);
        showDevice = findViewById(R.id.showButton);
        listView = findViewById(R.id.deviceList);
        implementListener();


    }


    /**
     *
     * Server class
     *
     */


    private class ServerClass extends Thread
    {
        private final BluetoothServerSocket serverSocket;

        public ServerClass()
        {
            BluetoothServerSocket tmp = null;
            try
            {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(app_name,uuid);
                Toast.makeText(getApplicationContext(),"ServerSocket Class constructor Successful!",Toast.LENGTH_LONG).show();
            }catch (Exception e)
            {
            }

            serverSocket = tmp;
        }

        @Override
        public void run()
        {
            BluetoothSocket socket =null;
            while (socket == null)
            {
                try
                {

                    Message message = Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    //sendReceive = new SendReceive(socket);
                    //sendReceive.start();

                    socket = serverSocket.accept();
                }catch (IOException e)
                {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket != null){

//                    ManageConnectedSocket(socket);

                    Message message = Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
//                    Toast.makeText(getApplicationContext(),"sending to manage connected socket Successful!",Toast.LENGTH_LONG).show();

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }

        public void cancel(){
            try{
                serverSocket.close();
            }catch (Exception e){}
        }
    }


    /**
     * Clientclass
     * ================================================================
     */

    private class ClientClass extends Thread{

        private BluetoothSocket socket ;
        private BluetoothDevice device;

        public ClientClass(BluetoothDevice mDevice){

            device = mDevice;
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Couldn´t create mSocket for device Create line 286");
            }
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectTread");
            setName("ConnectThread");
            bluetoothAdapter.cancelDiscovery();

            try{
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive= new SendReceive(socket);
                sendReceive.start();

            }catch(IOException e){
                connectionFailed();
            }

        }

        public void cancel(){
            try{
                socket.close();
            }catch(IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    }

    /**
     * for managing Connected Sockets
     * @param socket
     */


    /**
     * connected Threads
     */


    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                Toast.makeText(getApplicationContext(),"connected** Thread constructor Successful!",Toast.LENGTH_LONG).show();
            }catch (Exception e){}

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try{
                    bytes = mInStream.read(buffer);

                }catch (Exception e){
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                mOutStream.write(bytes);
            }catch (Exception e){}
        }

        public void cancel(){
            try{
                mSocket.close();
            }catch (Exception e){}
        }
    }


    private void connectionFailed() {
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect to device");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void setState(int newState){
        if(bool){
            Log.d(TAG,"setState()" + state + "to" + newState);
        }
        state = newState;
        handler.obtainMessage();
    }



    /**
     * =======================================================================================
      */



    /**
     * BroadcastReceiver
     * ==================================================================
     */


    /**
     * ============================================================
     * Bluetooth functions
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



    public static int buttonPressed=0;

    private void implementListener() {

        showDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonPressed==0) {
                    buttonPressed =1;
                    }else {
                    buttonPressed=0;
                }
                if(buttonPressed==1){
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
                if(buttonPressed ==0){
                    hideList();
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();

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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            {   switch (message.what) {
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
                        int[] actualState = {2,2,2,2,2,2,2,2,2};
                        String tempMsg = new String(actualState,0,message.arg1);
                        actualState= stringToIntegerArray(tempMsg);
                        State = actualState;
                        break;
                }

                return true;
            }
        }
    });


    protected void showList(){
        listView.setVisibility(View.VISIBLE);

    }

    protected void hideList(){
        listView.setVisibility(View.INVISIBLE);
    }
    public void startBluetoothGame(View view){

    }


    private class SendReceive extends Thread{
        BluetoothSocket bluetoothSocket;
        InputStream inStream;
        OutputStream outStream;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket =socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try{
            tempIn = bluetoothSocket.getInputStream();
            tempOut = bluetoothSocket.getOutputStream();
            }catch(IOException e){
                e.printStackTrace();
            }
            inStream = tempIn;
            outStream = tempOut;
        }

        public void run()
        {
            byte[]  buffer =new byte [1024];
            int bytes = globalTag;

            while(true)
            {
                try {
                    bytes = inStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                }catch(IOException e){
                    e.printStackTrace();
                    System.out.println("something wrong in line 500");
                }
            }
        }
        public void write(byte[] bytes){
            try {
                outStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void dropIn(View view){
        ImageView image= (ImageView) view;
        Log.i("Tag",image.getTag().toString());

        int taget = Integer.parseInt(image.getTag().toString());


        if(State[taget] == 2 && gameStarted){
            State[taget] = activePlayer;
            System.out.println("State =========  " + State[taget]);
            globalTag = State[taget];

            // where the magic comes out
            String toSent = String.valueOf(State[taget]);
            sendReceive.write(toSent.getBytes());

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

            image.animate().translationYBy(1500).setDuration(300);



        }
    }


    public int[] stringToIntegerArray(String s){
        int size = s.length();
        int[] intArray = new int[size];

        for(int i=0; i<=size;i++){
            intArray[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return intArray;
    }

    public void reset(View view){
        State = new  int []{2,2,2,2,2,2,2,2,2};
        ImageView image= (ImageView) view;
        activePlayer = 1;
        image.setTranslationY(-1500);
    }
}