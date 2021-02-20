package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main_manu extends AppCompatActivity {

    AppCompatButton my_feed,store,basket;
    ImageView iv_view;
    TextView title;

    String id;

    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manu);


        my_feed = findViewById(R.id.my_feed);
        title = findViewById(R.id.title);
        iv_view = findViewById(R.id.iv_view);
        store = findViewById(R.id.store);
        basket = findViewById(R.id.basket);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        title.setText(id);


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build();
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);
        //파일 생성

        my_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main_manu.this, Feed.class);
                intent.putExtra("feed_id",id); /*송신*/
                startActivity(intent);

            }
        });
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main_manu.this, Server.class);
                startActivity(intent);

            }
        });

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main_manu.this, Basket.class);
                startActivity(intent);
                finish();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        getImg();
/*

        Glide.with(this)
                .load(img_url)
                .centerCrop()
                .circleCrop()
                .into(iv_view);
*/

    }
    private void getImg() {

        Log.i("데이터전송", "" + id);

        Call<Img> call = retrofitAPI.getImg(id);

        call.enqueue(new Callback<Img>() {
            @Override
            public void onResponse(Call<Img> call, Response<Img> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body().toString());
                Img postResponse = response.body();

                String content = "";
                content += "id: " + postResponse.getId() + "\n";
                content += "pw: " + postResponse.getMy_img_url() + "\n";

                if (postResponse.getMy_img_url().equals("")){
                    Glide.with(Main_manu.this)
                            .load(R.drawable.gibon)
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);

                }else{
                    Glide.with(Main_manu.this)
                            .load(postResponse.getMy_img_url())
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);
                }

            }

            @Override
            public void onFailure(Call<Img> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });

    }


    private HttpLoggingInterceptor httpLoggingInterceptor(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.e("MyGitHubData :", message + "");
            }
        });

        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


}