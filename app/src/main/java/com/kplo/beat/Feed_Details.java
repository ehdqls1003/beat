package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed_Details extends AppCompatActivity implements Feed_Details_Adapter.MyRecyclearViewClickListener {


    Feed_Details_Adapter adapter;
    String id,feed_id;
    TextView title;
    private RetrofitAPI retrofitAPI;
    private ArrayList<Feed_Item> postResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed__details);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Intent intent = getIntent(); /*데이터 수신*/


        //리사이클러뷰 포지션값이앙니라 이거 idx값
        feed_id = intent.getExtras().getString("feed_id"); /*int형*/

        title = findViewById(R.id.title);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Call<ArrayList<Feed_Item>> call = retrofitAPI.getFeed_details(feed_id);


        call.enqueue(new Callback<ArrayList<Feed_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_Item>> call, Response<ArrayList<Feed_Item>> response) {
                if (!response.isSuccessful()) {

                    return;
                }

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                Collections.reverse(postResponse);

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Feed_Details.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Feed_Details_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;
                recyclerView.addItemDecoration(new RecyclerViewDecoration(20));

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Feed_Details.this);
                Intent intent = getIntent(); /*데이터 수신*/

                //리사이클러뷰 포지션값이앙니라 이거 idx값
                int position = intent.getExtras().getInt("position"); /*int형*/
                recyclerView.scrollToPosition(position);

            }

            @Override
            public void onFailure(Call<ArrayList<Feed_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Call<ArrayList<Feed_Item>> call = retrofitAPI.getFeed_details(feed_id);


        call.enqueue(new Callback<ArrayList<Feed_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_Item>> call, Response<ArrayList<Feed_Item>> response) {
                if (!response.isSuccessful()) {

                    return;
                }

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                Collections.reverse(postResponse);

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Feed_Details.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Feed_Details_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;
                recyclerView.addItemDecoration(new RecyclerViewDecoration(20));

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Feed_Details.this);
                Intent intent = getIntent(); /*데이터 수신*/


            }

            @Override
            public void onFailure(Call<ArrayList<Feed_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });

    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {

    }

    @Override
    public void onCommentClicked(int idx) {
        Log.e("프래그먼트","이거임?"+idx);
        Intent intent = new Intent(this, Feed_more.class);

        intent.putExtra("idx",idx); /*송신*/

        startActivity(intent);
    }
}