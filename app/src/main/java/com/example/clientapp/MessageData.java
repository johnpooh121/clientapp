package com.example.clientapp;

import android.widget.Toast;

public class MessageData {
    String username,roomnumber,content,mtype;
    MessageData(String username,String roomnumber,String content,String mtype){
        this.username=username;
        this.roomnumber=roomnumber;
        this.content=content;
        this.mtype=mtype;
    }
}
