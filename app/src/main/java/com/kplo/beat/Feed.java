package com.kplo.beat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Feed extends AppCompatActivity implements Feed_Adapter.MyRecyclearViewClickListener {

    AppCompatButton change_button,add_feed,flow,send_msg,un_flow;
    ImageView iv_view;
    TextView title,follow_count,following_count;
    Feed_Adapter adapter;

    ViewPager viewPager;
    ArrayList<Flow_Item> my_flow_array;
    String id;
    String feed_id;
    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;
    boolean f_f = false;
    ArrayList<Room_List> postResponse;
    String feed_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        change_button = findViewById(R.id.change_button);
        add_feed = findViewById(R.id.add_feed);
        title = findViewById(R.id.title);
        iv_view = findViewById(R.id.iv_view);
        flow = findViewById(R.id.flow);
        send_msg = findViewById(R.id.send_msg);
        un_flow = findViewById(R.id.un_flow);
        follow_count = findViewById(R.id.follow_count);
        following_count = findViewById(R.id.following_count);



        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build();
        Gson gson = new GsonBuilder().setLenient().create();

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Intent intent = getIntent(); /*데이터 수신*/


        //리사이클러뷰 포지션값이앙니라 이거 idx값
        feed_id = intent.getExtras().getString("feed_id"); /*int형*/
        Log.e("피드아이디",""+feed_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);

        get_myflow();
        get_flowing();
        getflow();

        if (!id.equals(feed_id)){

            add_feed.setVisibility(View.INVISIBLE);
            change_button.setVisibility(View.INVISIBLE);
            send_msg.setVisibility(View.VISIBLE);
        }

        title.setText(feed_id);







        //파일 생성

        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Feed.this, camera.class);
                startActivity(intent);

            }
        });

        flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //팔로우 보내기
                insert_flow();

            }
        });

        un_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delete_flow();

            }
        });

        //채팅방 입장
        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                room_list();

            }
        });


        follow_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Feed.this, flow_tab.class);
                intent.putExtra("feed_id",feed_id); /*송신*/
                startActivity(intent);
            }
        });
        following_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Feed.this, flow_tab.class);
                intent.putExtra("feed_id",feed_id); /*송신*/
                startActivity(intent);
            }
        });



    }

    //게시물 작성 버튼클릭
    public void OnClickupload(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(R.array.upload, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.upload);
                if (items[pos].equals("스토리")){
                    Intent intent = new Intent(Feed.this, write_story.class);
                    startActivity(intent);
                }else if(items[pos].equals("비트 업로드")){
                    Intent intent = new Intent(Feed.this, upload_music.class);
                    startActivity(intent);
                }else if(items[pos].equals("커버 업로드")){
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();

        getImg();
        get_flowing();
        getflow();




        //프레그먼트
        viewPager = findViewById(R.id.viewpager);
        ViewpagerAdapter adapter2 = new ViewpagerAdapter(getSupportFragmentManager(),feed_id);

        viewPager.setAdapter(adapter2);


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        /*get_Feed();*/
/*

        Glide.with(this)
                .load(img_url)
                .centerCrop()
                .circleCrop()
                .into(iv_view);
*/

    }
    private void getImg() {


        Call<Img> call = retrofitAPI.getImg(feed_id);

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
                    Glide.with(Feed.this)
                            .load(R.drawable.gibon)
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);

                }else{
                    Glide.with(Feed.this)
                            .load(postResponse.getMy_img_url())
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);
                }
                feed_img = postResponse.getMy_img_url();

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


    private void insert_flow() {

        Call<Result2> call = retrofitAPI.insert_flow(id,feed_id);

        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body());
                Result2 postResponse = response.body();
                Log.e("팔로우성공",postResponse.toString());
                Log.i("친구", "친구등록" );
                get_myflow();
                getflow();
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

                Log.i("onResponse", "3" );


            }
        });

    }

    private void delete_flow() {

        Call<Result2> call = retrofitAPI.delete_flow(id,feed_id);

        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }
                Log.i("친구", "친구삭제" );

                Log.i("onResponse", "2" + response.body());
                Result2 postResponse = response.body();
                get_myflow();
                getflow();
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

                Log.i("onResponse", "3" );


            }
        });

    }

    private void get_myflow() {

        Call<ArrayList<Flow_Item>> call = retrofitAPI.getMy_flowing(id);

        call.enqueue(new Callback<ArrayList<Flow_Item>>() {
                @Override
                public void onResponse(Call<ArrayList<Flow_Item>> call, Response<ArrayList<Flow_Item>> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("친구", "친구가져옴" );
                my_flow_array = response.body();

                if (!id.equals(feed_id)){
                    for (int i = 0; i < my_flow_array.size(); i++)
                    {
                        if(my_flow_array.get(i).getFriend_id().equals(feed_id)){
                            f_f = true;
                        }
                    }
                    if (f_f){
                        Log.e("친구","친구임");
                        flow.setVisibility(View.INVISIBLE);
                        un_flow.setVisibility(View.VISIBLE);
                        f_f = false;
                    }else{
                        Log.e("친구","친구아님");
                        flow.setVisibility(View.VISIBLE);
                        un_flow.setVisibility(View.INVISIBLE);
                    }
                }




            }

            @Override
            public void onFailure(Call<ArrayList<Flow_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );


            }
        });

    }


    @Override
    public void onItemClicked(int position, int setMusic_url) {

    }

    private void getflow() {

        Call<Result2> call = retrofitAPI.getcount_flow(feed_id);

        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }
                Log.i("친구", "친구삭제" );

                Log.i("onResponse", "2" + response.body());
                Result2 postResponse = response.body();
                follow_count.setText(postResponse.getResult());
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

                Log.i("onResponse", "3" );


            }
        });

    }

    private void get_flowing() {

        Call<Result2> call = retrofitAPI.getcount_flowing(feed_id);

        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }
                Log.i("친구", "친구삭제" );

                Log.i("onResponse", "2" + response.body());
                Result2 postResponse = response.body();
                following_count.setText(postResponse.getResult());
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

                Log.i("onResponse", "3" );


            }
        });

    }

    private void room_list() {

        Call<ArrayList<Room_List>> call = retrofitAPI.getMy_roomlist(id);
        call.enqueue(new Callback<ArrayList<Room_List>>() {
            @Override
            public void onResponse(Call<ArrayList<Room_List>> call, Response<ArrayList<Room_List>> response) {
                postResponse = response.body();
                Log.e("룸리스트", "" + postResponse.size());
                boolean is=false;
                int room_num = 0;
                for (int i =0; i < postResponse.size(); i++){
                    if(postResponse.get(i).getUser_id().equals(feed_id)){
                        is = true;
                        room_num = postResponse.get(i).getR_num();
                        break;
                    }
                }
                if(is){
                    Intent intent = new Intent(Feed.this, NewClient.class);
                    String r_id = Integer.toString(room_num);
                    intent.putExtra("room_id",r_id); /*송신*/
                    intent.putExtra("feed_id",feed_id);
                    intent.putExtra("feed_img",feed_img);
                    startActivity(intent);
                }else{
                    //룸추가

                    Call<Result2> call2 = retrofitAPI.insert_room(id,feed_id);
                    call2.enqueue(new Callback<Result2>() {
                        @Override
                        public void onResponse(Call<Result2> call, Response<Result2> response) {
                            //추가성공
                            room_list();
                        }

                        @Override
                        public void onFailure(Call<Result2> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Room_List>> call, Throwable t) {

            }
        });
    }


}