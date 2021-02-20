package com.kplo.beat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Basket extends AppCompatActivity {

    TextView playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        playlist = findViewById(R.id.playlist);

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Basket.this, Basket_my_list.class);
                startActivity(intent);

            }
        });


    }
}