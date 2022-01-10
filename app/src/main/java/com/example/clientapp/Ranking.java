package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Ranking extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    private ArrayList<RankingItem> RankingList;
    Gson gson = new Gson();
    //ArrayList<Status> stats = new ArrayList<Status>();
    Status[] stats;
    String id;
    Socket rankSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        setContentView(R.layout.activity_ranking);


//        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

        try {
            rankSocket = IO.socket("http://192.249.18.147:80");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        rankSocket.connect();

        rankSocket.emit("getranking",gson.toJson(new MessageData(
                id,"","","",""
        )));
        rankSocket.on("yourranking",receiveranking);

        /* adapt data */
        // db에서 승 순서대로 가져오기. 사진은 drawable/knight 로.
    }

    public Emitter.Listener receiveranking = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    rankingAdapter = new RankingAdapter();
                    recyclerView.setAdapter(rankingAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Ranking.this));

//                    Toast.makeText(getApplicationContext(),"receive ranking",Toast.LENGTH_SHORT).show();
                    MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
                    if(!id.equals(data.username))return;
                    stats=data.stats;
                    ArrayList<Status> arrstats = new ArrayList<Status>();
                    for(int i=0;i< data.length;i++){
                        arrstats.add(stats[i]);
                    }
                    Collections.sort(arrstats);
                    RankingList = new ArrayList<RankingItem>();
                    for(Status st : arrstats){
                        String pname = st.name;
                        if(pname.contains("@")){
                            String rname;
                            int idx = pname.indexOf("@");
                            rname = pname.substring(0,idx);
                            RankingList.add(new RankingItem(R.mipmap.knight_foreground,rname,st.win+" 승 "+st.lose+"패"));
                        }
                        else{
                            RankingList.add(new RankingItem(R.mipmap.knight_foreground,pname,st.win+" 승 "+st.lose+"패"));
                        }
                   }
                    rankingAdapter.setRankingList(RankingList);
                    rankSocket.disconnect();
                }
            });
        }
    };


}