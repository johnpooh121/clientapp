package com.example.clientapp;

public class Status implements Comparable<Status>{
    int win,lose;
    String name;
    Status(){
        win=lose=0;
        name="noname";
    }

    @Override
    public int compareTo(Status status) {
        if(win<status.win)return 1;
        else if(win>status.win)return -1;
        return 0;
    }
}
