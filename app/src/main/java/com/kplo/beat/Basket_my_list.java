package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Basket_my_list extends AppCompatActivity implements Basket_my_list_Adapter.MyRecyclearViewClickListener {


    ArrayList<playlist> postResponse;
    Basket_my_list_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    String title,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_my_list);


        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Call<ArrayList<playlist>> call = retrofitAPI.my_play_list(id);
        call.enqueue(new Callback<ArrayList<playlist>>() {
            @Override
            public void onResponse(Call<ArrayList<playlist>> call, Response<ArrayList<playlist>> response) {

                postResponse = response.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Basket_my_list.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Basket_my_list_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Basket_my_list.this);

            }

            @Override
            public void onFailure(Call<ArrayList<playlist>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onItemClicked(int position, int playnum, String playlist_n) {

        Intent intent = new Intent(this, MyPlayList2.class);

        intent.putExtra("title",playlist_n); /*송신*/
        intent.putExtra("playnum",playnum);

        startActivity(intent);
        /*Intent intent = getIntent(); *//*데이터 수신*//*


        //리사이클러뷰 포지션값이앙니라 이거 idx값
        feed_id = intent.getExtras().getString("feed_id"); *//*int형*/

    }
}