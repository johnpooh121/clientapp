package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//
//        /* initiate adapter */
//        rankingAdapter = new RankingAdapter();
//
//        /* initiate recyclerview */
//        recyclerView.setAdapter(rankingAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
//
//        /* adapt data */
//        init();
//
//        rankingAdapter.setFriendList(RankingList);
    }

    //db에서 승 순서대로 로그인 정보 넣기
    private void init(){

    }
}