package com.example.clientapp;

import static com.example.clientapp.findroom.mSocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

public class gameboard extends AppCompatActivity {
    String opponentname="",id="",roomnumber="";
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        opponentname=bundle.getString("opponentname");
        id=bundle.getString("id");
        roomnumber=bundle.getString("roomid");
        Toast.makeText(getApplicationContext(),"op : "+opponentname+", id: "+id,Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_gameboard);
        mSocket.emit("newMessage",gson.toJson(new MessageData(
                id,
                roomnumber,
                opponentname,
                ""
        )));

    }
}