package com.example.clientapp;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectThread extends Thread{
    String wts;
    ConnectThread(String wts){
        this.wts=wts;
    }
    public void run(){
        try{
            Socket soc = new Socket("172.20.41.101",80);
            ObjectOutputStream ostream = new ObjectOutputStream(soc.getOutputStream());
            ostream.writeObject((Object)wts);
            ostream.flush();
            soc.close();
        } catch (UnknownHostException e) {
            Log.d("SOCKET", "shit!!");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("SOCKET", "no!!");
            e.printStackTrace();
        }
    }
}
