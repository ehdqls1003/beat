package com.kplo.beat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Basket_add extends AppCompatActivity implements Basket_add_Adapter.MyRecyclearViewClickListener {



    Basket_add_Adapter adapter;
    AppCompatButton submit,new_b;
    private RetrofitAPI retrofitAPI;
    EditText edit_playlist;
    String title,id;
    ArrayList<Integer> insert_arr;

    ArrayList<playlist> postResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_add);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        submit = findViewById(R.id.submit);
        edit_playlist = findViewById(R.id.edit_playlist);
        new_b = findViewById(R.id.new_b);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        insert_arr = getStringArrayPref(getApplicationContext(),"basket"+id);

        Collections.reverse(insert_arr);

        new_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setVisibility(View.VISIBLE);
                edit_playlist.setVisibility(View.VISIBLE);
                new_b.setVisibility(View.INVISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();
                finish();

            }
        });

        Call<ArrayList<playlist>> call = retrofitAPI.my_play_list(id);
        call.enqueue(new Callback<ArrayList<playlist>>() {
            @Override
            public void onResponse(Call<ArrayList<playlist>> call, Response<ArrayList<playlist>> response) {

                postResponse = response.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Basket_add.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Basket_add_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Basket_add.this);

            }

            @Override
            public void onFailure(Call<ArrayList<playlist>> call, Throwable t) {

            }
        });

    }
    public void insert(){

        title = edit_playlist.getText().toString();

        Call<Result2> call = retrofitAPI.insert_my_play_list(title,id);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {

                Result2 postResponse = response.body();

                Log.e("받음",""+postResponse.getResult());



                    Call<Result2> call2 = retrofitAPI.insert_my_play_list_music_more(insert_arr, Integer.parseInt(postResponse.getResult()));
                    call2.enqueue(new Callback<Result2>() {
                        @Override
                        public void onResponse(Call<Result2> call, Response<Result2> response) {

                            Log.e("받음","성공");
                            Toast.makeText(getApplicationContext(), title+"생성"+insert_arr.size()+"개의 곡을 추가했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Result2> call, Throwable t) {

                        }
                    });

            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {


            }
        });

    }


    private ArrayList<Integer> getStringArrayPref(Context context, String key) {
        Log.e("어레이불러옴","불러옴");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<Integer> urls = new ArrayList<Integer>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    int url = Integer.parseInt(a.optString(i));
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    @Override
    public void onItemClicked(int position, final int playnum, String playlist_n) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Basket_add.this);

        builder.setTitle("추가").setMessage("" + insert_arr.size() + "개의 곡을"+playlist_n+"에 추가시키겠습니까?");

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {


                Call<Result2> call2 = retrofitAPI.insert_my_play_list_music_more(insert_arr, playnum);
                call2.enqueue(new Callback<Result2>() {
                    @Override
                    public void onResponse(Call<Result2> call, Response<Result2> response) {


                        Toast.makeText(getApplicationContext(), insert_arr.size()+"개의 곡을 추가했습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Result2> call, Throwable t) {

                    }
                });


            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();




    }

    public void insertmusic(){

    }
}