package com.kplo.beat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_like_playlist extends AppCompatActivity implements User_like_playlist_Adapter.MyRecyclearViewClickListener {

    private RetrofitAPI retrofitAPI;
    User_like_playlist_Adapter adapter;

    //음악재생 변수
    public String url;
    int position = 0;
    MediaPlayer player;
    ImageView p_next,p_play,p_pause,p_before,p_img;
    TextView p_title,p_id;
    private Messenger mServiceMessenger = null;
    MyService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용
    ServiceConnection conn;
    String id;

    ArrayList<Recent_Main_Item> postResponse;
    ArrayList<Recent_Main_Item> postResponse2 = new ArrayList<Recent_Main_Item>();
    ArrayList<userlikemusic> list;

    //서버에서데이터받기
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("test","act : what "+msg.what);
            Log.e("피니시",""+isFinishing());
            if(!isFinishing()) {
                switch (msg.what) {
                    case MyService.MSG_SEND_TO_ACTIVITY:
                        String value1 = msg.getData().getString("test1");
                        Log.i("test", "act : value1 " + value1);
                        String value2 = msg.getData().getString("test2");
                        Log.i("test", "act : value1 " + value2);
                        String value3 = msg.getData().getString("test3");
                        Log.i("test", "act : value1 " + value3);
                        Glide.with(User_like_playlist.this)
                                .load(value3)
                                .apply(new RequestOptions().circleCrop().centerCrop())
                                .centerCrop()
                                .circleCrop()
                                .into(p_img);
                        p_title.setText(value2);
                        p_id.setText(value1);
                        boolean isPlaying = msg.getData().getBoolean("isPlaying");
                        Log.e("isplaying", "" + isPlaying);
                        if (!isPlaying) {
                            p_play.setVisibility(View.VISIBLE);
                            p_pause.setVisibility(View.INVISIBLE);
                        } else {
                            p_play.setVisibility(View.INVISIBLE);
                            p_pause.setVisibility(View.VISIBLE);
                        }
                        p_img.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }
    }));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_like_playlist);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        p_next = findViewById(R.id.p_next);
        p_play = findViewById(R.id.p_play);
        p_pause = findViewById(R.id.p_pause);
        p_before = findViewById(R.id.p_before);
        p_title = findViewById(R.id.p_title);
        p_id = findViewById(R.id.p_name);
        p_img = findViewById(R.id.p_img);

        p_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_like_playlist.this, Playing.class);
                startActivity(intent);
            }
        });

        p_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyService.current_position != 0) {
                    if (mServiceMessenger != null) {
                        try {

                            Bundle bundle = new Bundle();
                            Message msg = Message.obtain(null, MyService.resumeAudio);
                            msg.setData(bundle);
                            mServiceMessenger.send(msg);
                        } catch (RemoteException e) {
                        }
                    }
                    if (MyService.data) {
                        p_play.setVisibility(View.INVISIBLE);
                        p_pause.setVisibility(View.VISIBLE);
                    }
                }else{
                    if (mServiceMessenger != null) {
                        try {

                            Bundle bundle = new Bundle();
                            Message msg = Message.obtain(null, MyService.playAudio);
                            msg.setData(bundle);
                            mServiceMessenger.send(msg);
                        } catch (RemoteException e) {
                        }
                    }
                    if (MyService.data) {
                        p_play.setVisibility(View.INVISIBLE);
                        p_pause.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        p_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyService.data) {
                    p_play.setVisibility(View.VISIBLE);
                    p_pause.setVisibility(View.INVISIBLE);
                }
                if (mServiceMessenger != null) {
                    try {

                        Bundle bundle = new Bundle();
                        Message msg = Message.obtain(null, MyService.pauseAudio);
                        msg.setData(bundle);
                        mServiceMessenger.send(msg);
                    } catch (RemoteException e) {
                    }
                }
            }
        });
        p_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*before_music();*/
                if (MyService.data) {
                    p_play.setVisibility(View.INVISIBLE);
                    p_pause.setVisibility(View.VISIBLE);
                }
                if (mServiceMessenger != null) {
                    try {

                        Bundle bundle = new Bundle();
                        bundle.putInt("item_position",position);
                        Message msg = Message.obtain(null, MyService.before_music);
                        msg.setData(bundle);
                        mServiceMessenger.send(msg);      // msg 보내기
                    } catch (RemoteException e) {
                    }
                }
            }
        });
        p_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyService.data) {
                    p_play.setVisibility(View.INVISIBLE);
                    p_pause.setVisibility(View.VISIBLE);
                }
                if (mServiceMessenger != null) {
                    try {

                        Bundle bundle = new Bundle();
                        bundle.putInt("item_position",position);
                        Message msg = Message.obtain(null, MyService.next_music);
                        msg.setData(bundle);
                        mServiceMessenger.send(msg);
                    } catch (RemoteException e) {
                    }
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("뭐냐이건", "isService" + isService);
        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {

                mServiceMessenger = new Messenger(service);
                try {
                    Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);


                } catch (RemoteException e) {
                }
                Intent intent = new Intent(
                        User_like_playlist.this, // 현재 화면
                        MyService.class); // 다음넘어갈 컴퍼넌트
                startService(intent);

                // 서비스쪽 객체를 전달받을수 있슴
                isService = true;

                Log.e("마이서비스", "conn" + isService);


            }

            public void onServiceDisconnected(ComponentName name) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                isService = false;

            }
        };

        Intent intent = new Intent(
                User_like_playlist.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);

        if (!MyService.data){
            p_img.setVisibility(View.INVISIBLE);
            p_title.setText("노래를 선택해");
            p_id.setText("주세요");
        }

        recent_music();

    }

    private void recent_music() {

        Call<ArrayList<Recent_Main_Item>> call = retrofitAPI.recent_music();

        call.enqueue(new Callback<ArrayList<Recent_Main_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Recent_Main_Item>> call, Response<ArrayList<Recent_Main_Item>> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();


                Call<ArrayList<userlikemusic>> call2 = retrofitAPI.userlikemusic(id);

                call2.enqueue(new Callback<ArrayList<userlikemusic>>() {
                    @Override
                    public void onResponse(Call<ArrayList<userlikemusic>> call, Response<ArrayList<userlikemusic>> response) {

                        list = response.body();
                        postResponse2.clear();
                        for (int i = 0; i < list.size(); i++){
                            for (int j = 0; j < postResponse.size(); j++){
                                if(list.get(i).getMusic_idx() == postResponse.get(j).getIdx()){
                                    Recent_Main_Item item = new Recent_Main_Item();
                                    item.setMusic_url(postResponse.get(j).getMusic_url());
                                    item.setMy_img_url(postResponse.get(j).getMy_img_url());
                                    item.setId(postResponse.get(j).getId());
                                    item.setTitle(postResponse.get(j).getTitle());
                                    item.setIdx(postResponse.get(j).getIdx());
                                    postResponse2.add(item);
                                    Log.e("추가함","추가함");

                                }
                            }
                        }

                        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                        RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                        recyclerView.setLayoutManager(new LinearLayoutManager(User_like_playlist.this,LinearLayoutManager.VERTICAL,false)) ;

                        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                        adapter = new User_like_playlist_Adapter(postResponse2) ;
                        recyclerView.setAdapter(adapter) ;

                        //이거안해주면 리스너안먹힘
                        adapter.setOnClickListener(User_like_playlist.this);

                    }

                    @Override
                    public void onFailure(Call<ArrayList<userlikemusic>> call, Throwable t) {

                    }
                });







            }

            @Override
            public void onFailure(Call<ArrayList<Recent_Main_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );
                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClicked(int position,String Music_url) {


        /*Intent intent = new Intent(this, MyService.class);
        bindService(intent,    // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);

        intent.putExtra(MyService.MESSAGE_KEY, true); // 설정에서 true로 바꿈과 동시에 재생 시작
        intent.putExtra("position",position); *//*송신*//*

        startForegroundService(intent);*/

        /*
        ms.setposition(position);
        Glide.with(this)
                .load(postResponse.get(position).getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(p_img);
        p_title.setText(postResponse.get(position).getTitle());
        p_id.setText(postResponse.get(position).getId());
*/
    }

    @Override
    public void onm_PlayClicked(int position, int idx) {

        Call<Result2> call = retrofitAPI.insert_user_play_list(idx,id);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {

                Toast.makeText(getApplicationContext(), "1곡을 재생 목록에 담았습니다.", Toast.LENGTH_SHORT).show();
                if (mServiceMessenger != null) {
                    try {
                        Bundle bundle = new Bundle();
                        Message msg = Message.obtain(null, MyService.getData);
                        msg.setData(bundle);
                        mServiceMessenger.send(msg);
                    } catch (RemoteException e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

            }
        });
    }

}