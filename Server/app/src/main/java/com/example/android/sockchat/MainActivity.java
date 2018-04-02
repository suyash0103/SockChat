package com.example.android.sockchat;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    Button button_sent;
    EditText smessage;
    TextView chat, display_status;
    String str, msg = "";
    int serverport = 8000;
    ServerSocket serverSocket;
    Socket client;
    Handler handler = new Handler();
    WifiManager wmanager;

    // counter for clients
    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wmanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip =
                Formatter.formatIpAddress(wmanager.getConnectionInfo().getIpAddress());
        smessage = (EditText) findViewById(R.id.smessage);
        chat = (TextView) findViewById(R.id.chat);
        display_status = (TextView)
                findViewById(R.id.display_status);
        display_status.setText("Server hosted on " + ip);
        Thread serverThread = new Thread(new serverThread());
        serverThread.start();
        button_sent = (Button) findViewById(R.id.button_sent);
        button_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread sentThread = new Thread(new sentMessage());
                sentThread.start();
            }
        });
    }

    class sentMessage implements Runnable {
        @Override
        public void run() {
            try {
                if (serverSocket != null) {
                    Socket client = serverSocket.accept();
                    DataOutputStream os = new
                            DataOutputStream(client.getOutputStream());
                    str = smessage.getText().toString();
                    msg = msg + "\n Server : " + str;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chat.setText(msg);
                        }
                    });
                    os.writeBytes(str);
                    os.flush();
                    os.close();
                    client.close();
                } else {
                    Log.v("No Client to connect", "No Client to connect");
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class serverThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    serverSocket = new ServerSocket(serverport);
                    Socket client = serverSocket.accept();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            display_status.setText("Connected");
                        }
                    });
                    DataInputStream in = new
                            DataInputStream(client.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
//                    Log.v("AAAAAAAAA", in.toString());
//                    String x = in.readLine();
//                    Log.v("BBBBBBBb", x);
                    while (bufferedReader.readLine() != null) {
                        line = bufferedReader.readLine();
                        msg = msg + "\n Client : " + line;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                chat.setText(msg);
                            }
                        });
                    }
                    in.close();
                    client.close();
                    Thread.sleep(100000);
                }
            } catch (Exception e) {
            }
        }
    }
}