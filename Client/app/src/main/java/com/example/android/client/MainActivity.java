package com.example.android.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    EditText serverIp, smessage;
    TextView chat;
    Button connectPhones, sent;
    String serverIpAddress = "", msg = "", str;
    Handler handler = new Handler();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat = (TextView) findViewById(R.id.chat);
        serverIp = (EditText) findViewById(R.id.server_ip);
        smessage = (EditText) findViewById(R.id.smessage);
        sent = (Button) findViewById(R.id.sent_button);
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread sentThread = new Thread(new sentMessage());
                sentThread.start();
            }
        });


        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Not", Toast.LENGTH_SHORT).show();

                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    i++;
                    Log.v("hhhh", "hhh");
                    Thread clientThread = new Thread(new
                            ClientThread());
                    clientThread.start();
                }
                else {
                    Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        while(i > 0)
        {
            serverIpAddress = serverIp.getText().toString();
            if (!serverIpAddress.equals("")) {
                Log.v("hhhh", "hhh");
                Thread clientThread = new Thread(new
                        ClientThread());
                clientThread.start();
            }
            else {
                Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
            }
        }

    }


    class sentMessage implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Socket socket = new Socket(serverAddr, 8000);
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                str = smessage.getText().toString();
                str = str + "\n";
                msg = msg + "Client : " + str;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        chat.setText(msg);
                    }
                });
                os.writeBytes(str);
                os.flush();
                os.close();
                socket.close();
            } catch (IOException e) {

            }
        }
    }


    public class ClientThread implements Runnable {
        public void run() {
            try {
                while (true) {
                    InetAddress serverAddr =
                            InetAddress.getByName(serverIpAddress);
                    Socket socket = new Socket(serverAddr, 8000);
                    DataInputStream in = new
                            DataInputStream(socket.getInputStream());
                    String line = null;
                    while ((line = in.readLine()) != null){
                        msg = msg + "Server : " + line + "\n";
                        handler.post(new Runnable(){
                            @Override
                            public void run(){
                                chat.setText(msg);
                            }
                        });
                    }

                    in.close();
                    socket.close();
                    Thread.sleep(100);
                }
            }
            catch (Exception e){

            }
        }
    }
}
