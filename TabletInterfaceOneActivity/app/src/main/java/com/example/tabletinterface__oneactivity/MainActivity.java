package com.example.tabletinterface__oneactivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


//This activity holds a TextView for the watch's IP address, a button to connect to the robot, and a button to switch to the second (control) activity.


public class MainActivity extends Activity {


    //private BoxInsetLayout boxInsetLayout;
    Button button;

    // This will display the IP address as entered into SERVER_IP. It cannot be changed from the watch interface.
    TextView ipAddress;
    TextView port;

    Button pickUpObject;
    Button placeObject;
    Button enterGCM;
    Button exitGCM;

    SeekBar sb1;
    SeekBar sb2;
    SeekBar sb3;
    SeekBar sb4;
    SeekBar sb5;
    SeekBar sb6;

    String str = "default"; //this allows you to test your str modifiers in the onClick method: if the message sent is "default," you know something went wrong. str is active in both MainActivity and SendMessage classes.


    // Change this to the address of the server you're connecting to.
    static final String SERVER_IP = "169.254.152.5";

    // Returns SERVER_IP so it can be accessed in the DoInBackground class.
    public static String getSERVER_IP() {
        return SERVER_IP;
    }


    static final int serverport = 20602;

    // Returns serverport so it can be accessed in the DoInBackground class. (It doesn't need to be declared in the main class but I find it's easier to spot the IP and port fields when they're near the top.
    public static int getServerport() {
        return serverport;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets the TextView to SERVER_IP
        ipAddress = findViewById(R.id.ipAddress); //Try commenting out this line
        ipAddress.setText("Connecting to: " + SERVER_IP);

        port = findViewById(R.id.port);
        port.setText("Connecting to port " + serverport);


        //boxInsetLayout = findViewById(R.id.main_activity);
        button = findViewById(R.id.connect);

        button.setOnClickListener(new View.OnClickListener() {

            // Connects when (and only when!) the "connect" button is clicked.
            @Override
            public void onClick(View v) {
                connect();
            }
        });


        View.OnClickListener  listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int buttonPressed = v.getId();
                Log.i("MainActivity", "Button pressed: " + v.getId());

                // Creates a new SendMessage object each time a button is clicked. All of these run on the same socket, declared in MainActivity: this socket is only created once.
                MainActivity.SendMessage asyncTask = new MainActivity.SendMessage();
                asyncTask.execute();

                switch (buttonPressed){
                    case R.id.pick_up_object:
                        str = "pick";
                        break;
                    case R.id.place_object:
                        str = "place";
                        break;
                    case R.id.enter_GCM:
                        str = "gravcomp_on";
                        break;
                    case R.id.exit_GCM:
                        str = "gravcomp_off";
                        break;
                }
            }
        };

        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            int seekBarChanged;
            int progress = -1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarChanged = seekBar.getId();
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                seekBarChanged = seekBar.getId();

                switch (seekBarChanged){
                    case R.id.sb1:
                        str = "sb1 " + progress;
                        Log.i("MainActivity", str);
                        break;
                    case R.id.sb2:
                        str = "sb2 " + progress;
                        Log.i("MainActivity", str);
                        break;
                    case R.id.sb3:
                        str = "sb3 " + progress;
                        Log.i("MainActivity", str);
                        break;
                    case R.id.sb4:
                        str = "sb4 " + progress;
                        Log.i("MainActivity", str);
                        break;
                    case R.id.sb5:
                        str = "sb5 " + progress;
                        Log.i("MainActivity", str);
                        break;
                    case R.id.sb6:
                        str = "sb6 " + progress;
                        Log.i("MainActivity", str);
                        break;
                }

                MainActivity.SendMessage asyncTask = new MainActivity.SendMessage();
                asyncTask.execute();

            }
        };


        //assigns all button objects to their respective xml code
        pickUpObject = findViewById(R.id.pick_up_object);
        placeObject = findViewById(R.id.place_object);
        enterGCM = findViewById(R.id.enter_GCM);
        exitGCM = findViewById(R.id.exit_GCM);

        //assigns all button objects to the same onClickListener
        pickUpObject.setOnClickListener(listener);
        placeObject.setOnClickListener(listener);
        enterGCM.setOnClickListener(listener);
        exitGCM.setOnClickListener(listener);

        sb1 = findViewById(R.id.sb1);
        sb2 = findViewById(R.id.sb2);
        sb3 = findViewById(R.id.sb3);
        sb4 = findViewById(R.id.sb4);
        sb5 = findViewById(R.id.sb5);
        sb6 = findViewById(R.id.sb6);

        sb1.setOnSeekBarChangeListener(seekBarListener);
        sb2.setOnSeekBarChangeListener(seekBarListener);
        sb3.setOnSeekBarChangeListener(seekBarListener);
        sb4.setOnSeekBarChangeListener(seekBarListener);
        sb5.setOnSeekBarChangeListener(seekBarListener);
        sb6.setOnSeekBarChangeListener(seekBarListener);

    }

    // Starts the DoInBackground thread. This should ony need to be done once per session: the connection should not be renewed every time a message is sent.
    public void connect() {
        MainActivity.DoInBackground asyncTask = new MainActivity.DoInBackground();
        asyncTask.execute();
    }

    // Class to open socket and connect to computer.
    static class DoInBackground extends AsyncTask<Void, Void, String> {

        public static Socket getSocket() {
            Log.i("MainActivity", "Returning socket");
            return socket;
        }

        public static Socket socket;


        @Override
        public void onPreExecute() {
            Log.i("MainActivity", "onPreExecute"); // This is just for documentation
        }

        @Override
        public String doInBackground(Void... voids) {

            final int serverport = getServerport(); //20602 is the default for the Unity engine I was using at the time
            final String SERVER_IP = getSERVER_IP();
            Log.i("MainActivity", "Server_IP = " + SERVER_IP);

            try {
                Log.i("MainActivity", "'Try' loop started in onClick");

                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                Log.i("MainActivity", "serverAddr: " + serverAddr);

                socket = new Socket(serverAddr, serverport);

            } catch (UnknownHostException e) {
                Log.i("MainActivity", "caught UnknownHostException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("MainActivity", "caught IOException");
                e.printStackTrace();
            } catch (Exception e) {
                Log.i("MainActivity", "caught Exception");
                e.printStackTrace();
            }

            // Returned value isn't important, since it is never used.
            Log.i("MainActivity", "returning doInBackground");
            return "";
        }
    }

    class SendMessage extends AsyncTask<Void, Void, String> {

        Socket socket = MainActivity.DoInBackground.getSocket();

        @Override
        public void onPreExecute(){
        }

        @Override
        public String doInBackground(Void... voids) {

            try {
                // Sends message
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                out.println(str);
                Log.i("MainActivity", "Message Sent");
            } catch (UnknownHostException e) {
                Log.i("MainActivity", "caught UnknownHostException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("MainActivity", "caught IOException");
                e.printStackTrace();
            } catch (Exception e) {
                Log.i("MainActivity", "caught Exception");
                e.printStackTrace();
            }

            //the return type does not seem to matter for this program. The return value will never be used.
            return "";

        }
    }

}