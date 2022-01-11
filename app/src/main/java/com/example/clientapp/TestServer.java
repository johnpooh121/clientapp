package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TestServer extends AppCompatActivity {
    TextView tv;
    EditText et;
    Button btn;
    String username="pooh",roomNumber="1";
    TcpClient mClient;
    Socket mSocket;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test_server);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.edit_text);
        btn = findViewById(R.id.send);
        try {
            //mSocket = IO.socket("http://172.10.5.101:80");
            //mSocket = IO.socket("http://172.20.41.101:80"); //local
            mSocket = IO.socket("http://192.249.18.147:443"); //server //172.10.18.147

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();
        mSocket.on("update",receive);
        mSocket.emit("enter",gson.toJson(new RoomData(roomNumber,username)));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"?",Toast.LENGTH_SHORT).show();
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String content = et.getText().toString();
        mSocket.emit("newMessage", gson.toJson(new MessageData(
                username,
                roomNumber,
                content,
                "",
                ""
                )));
        Log.d("MESSAGE", String.valueOf(new MessageData(
                username,
                roomNumber,
                content,
                "",
                ""
                )));
        appendString(username+" : "+content);
        et.setText("");
    }

    public Emitter.Listener receive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Toast.makeText(getApplicationContext(),"??",Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(),"!!",Toast.LENGTH_SHORT).show();
                    MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
//                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String content;
                    username = data.username;
                    content = data.content;
                    //Toast.makeText(getApplicationContext(),""+content,Toast.LENGTH_SHORT).show();
                    // add the message to view
                    appendString(username+" : "+content);
                }
            });
        }
    };

    void appendString(String str){
        if(tv==null)return;
        tv.append(str+"\n");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}