package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import java.util.ArrayList;


public class Ranking extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    private ArrayList<RankingItem> RankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_ranking);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        /* initiate adapter */
        rankingAdapter = new RankingAdapter();

        /* initiate recyclerview */
        recyclerView.setAdapter(rankingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));


        /* adapt data */
        // db에서 승 순서대로 가져오기. 사진은 drawable/knight 로.
        RankingList = new ArrayList<>();
        for(int i=1;i<=10;i++){
            if(i%2==0)
                RankingList.add(new RankingItem(R.mipmap.knight_foreground,i+"번째 사람",i+"번째 상태메시지"));
            else
                RankingList.add(new RankingItem(R.mipmap.knight_foreground,i+"번째 사람",i+"번째 상태메시지"));

        }
        rankingAdapter.setRankingList(RankingList);
    }

}