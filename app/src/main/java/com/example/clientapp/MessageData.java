package com.example.clientapp;

import android.widget.Toast;
// move,i,j,null
// wall,i,j,horizontal
public class MessageData {
    String username,roomnumber,content,mtype,move,detail;
    int win,lose;
    MessageData(String username,String roomnumber,String content,String detail,String move){
        this.username=username;
        this.roomnumber=roomnumber;
        this.content=content;
        this.detail=detail;
        this.move=move;
        win=lose=0;
    }
}
