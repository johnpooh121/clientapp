package com.example.clientapp;

import android.widget.Toast;
// move,i,j,null
// wall,i,j,horizontal
public class MessageData {
    String username,roomnumber,content,mtype,move;
    MessageData(String username,String roomnumber,String content,String mtype,String move){
        this.username=username;
        this.roomnumber=roomnumber;
        this.content=content;
        this.mtype=mtype;
        this.move=move;
    }
}
