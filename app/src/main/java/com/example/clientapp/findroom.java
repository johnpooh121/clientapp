package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class findroom extends AppCompatActivity {
    String id = "noname";
    TextView tv;
    EditText et;
    Button btn;
    String roomNumber="1";
    static Socket mSocket;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_findroom);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        Toast.makeText(this,""+id,Toast.LENGTH_SHORT).show();
        btn = this.findViewById(R.id.enter_room);
        et = this.findViewById(R.id.room_id);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSocket = IO.socket("http://192.249.18.147:80");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                mSocket.connect();
                roomNumber=et.getText().toString().trim();
                mSocket.emit("enter",gson.toJson(new MessageData(id,roomNumber,"","")));
                mSocket.on("roomfound",whenroomfound);
            }
        });
    }
    public Emitter.Listener whenroomfound = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"room found!",Toast.LENGTH_SHORT).show();
                    MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
                    Bundle bundle = new Bundle();
                    String opponentname = data.username;
                    if(opponentname.equals(id))opponentname=data.content;
                    Toast.makeText(getApplicationContext(),id+" : "+opponentname,Toast.LENGTH_SHORT).show();
                    bundle.putString("opponentname",opponentname);
                    bundle.putString("id",id);
                    bundle.putString("roomid",roomNumber);
                    Intent intent = new Intent(findroom.this, gameboard.class) ;
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    };
}