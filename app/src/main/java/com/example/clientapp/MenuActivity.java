package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        Button ranking = this.findViewById(R.id.ranking);
        //Button test_server = this.findViewById(R.id.test_server);
        Button findroom = this.findViewById(R.id.findroom);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");
        bundle.putString("id",id);

        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Ranking.class) ;
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
//        test_server.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MenuActivity.this, TestServer.class) ;
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
        findroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, findroom.class) ;
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}