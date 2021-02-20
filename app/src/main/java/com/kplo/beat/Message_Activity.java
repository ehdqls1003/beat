package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Message_Activity extends AppCompatActivity implements Message_Adapter.MyRecyclearViewClickListener {

    Message_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    ArrayList<Room_List> postResponse;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);


    }

    private void room_list(){

        Call<ArrayList<Room_List>> call = retrofitAPI.getMy_roomlist(id);
        call.enqueue(new Callback<ArrayList<Room_List>>() {
            @Override
            public void onResponse(Call<ArrayList<Room_List>> call, Response<ArrayList<Room_List>> response) {
                postResponse = response.body();
                Log.e("룸리스트",""+postResponse.size());

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Message_Activity.this, LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Message_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Message_Activity.this);
            }

            @Override
            public void onFailure(Call<ArrayList<Room_List>> call, Throwable t) {

            }
        });

    }



    @Override
    public void onItemClicked(int position, int room_id,String feed_id,String feed_img) {

        Intent intent = new Intent(Message_Activity.this, NewClient.class);
        String r_id = Integer.toString(room_id);
        intent.putExtra("room_id",r_id); /*송신*/
        intent.putExtra("feed_id",feed_id);
        intent.putExtra("feed_img",feed_img);
        startActivity(intent);

    }

    @Override
    public void onmore_buttonClicked(int story_comment_num, String comment, int c_pos) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        room_list();
    }
}