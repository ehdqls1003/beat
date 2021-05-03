package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Music_highlight_comment extends AppCompatActivity implements Comment_Adapter.MyRecyclearViewClickListener, Comment_highlight_Adapter.MyRecyclearViewClickListener {

    private RetrofitAPI retrofitAPI;
    private ArrayList<highlight_comment> comments;
    Comment_highlight_Adapter adapter;

    ImageView pancil;
    EditText comment_input;

    int idx;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_highlight_comment);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        idx = intent.getExtras().getInt("idx"); /*int형*/
        Log.e("긴급",": "+idx);

        pancil = findViewById(R.id.pancil);
        comment_input = findViewById(R.id.comment_input);

        commentcall();

        pancil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (!comment_input.getText().toString().equals("")){

                        Call<Result2> call5 = retrofitAPI.insert_highlight_comment(idx,id,comment_input.getText().toString());
                        call5.enqueue(new Callback<Result2>() {
                            @Override
                            public void onResponse(Call<Result2> call5, Response<Result2> response5) {
                                comment_input.setText(null);
                                Log.e("댓글 작성","완료");

                                commentcall();

                            }

                            @Override
                            public void onFailure(Call<Result2> call5, Throwable t5) {

                                Log.e("댓글 작성","실패");
                            }
                        });
                    }



            }
        });


    }

    public void commentcall(){
        Log.e("긴급",": "+idx);
        Call<ArrayList<highlight_comment>> call3 = retrofitAPI.getm_h_c(idx);

        call3.enqueue(new Callback<ArrayList<highlight_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<highlight_comment>> cal3, Response<ArrayList<highlight_comment>> response3) {
                comments = response3.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Music_highlight_comment.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Comment_highlight_Adapter(comments) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Music_highlight_comment.this);



            }

            @Override
            public void onFailure(Call<ArrayList<highlight_comment>> call3, Throwable t3) {

            }
        });

    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {

    }

    @Override
    public void onmore_buttonClicked(int story_comment_num, String comment, int c_pos) {

    }

    @Override
    public void onIdClicked(String feed_id) {
        Intent intent = new Intent(Music_highlight_comment.this, Feed.class);

        intent.putExtra("feed_id",feed_id); /*송신*/

        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}