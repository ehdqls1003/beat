package com.kplo.beat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Basket extends AppCompatActivity {

    TextView playlist,likelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        playlist = findViewById(R.id.playlist);
        likelist = findViewById(R.id.likelist);

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Basket.this, Basket_my_list.class);
                startActivity(intent);

            }
        });

        likelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Basket.this, User_like_playlist.class);
                startActivity(intent);

            }
        });


    }
}