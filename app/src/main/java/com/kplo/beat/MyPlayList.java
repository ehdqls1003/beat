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

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlayList extends AppCompatActivity implements My_PlayList_Adapter.MyRecyclearViewClickListener {


    My_PlayList_Adapter adapter;
    private RetrofitAPI retrofitAPI;
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
                        Glide.with(MyPlayList.this)
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

    private void sendMessageToService(String str) {
        if (isService) {
            if (mServiceMessenger != null) {
                try {

                    Bundle bundle = new Bundle();
                    bundle.putInt("item_position",position);
                    Message msg = Message.obtain(null, MyService.playAudio);
                    msg.setData(bundle);
                    mServiceMessenger.send(msg);      // msg 보내기
                    /*Message msg = Message.obtain(null, MyService.MSG_SEND_TO_SERVICE, str);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);*/
                } catch (RemoteException e) {
                }
            }
        }
    }

    int a;

    ArrayList<Recent_Main_Item> postResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_play_list);


        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        recent_music();

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
                Intent intent = new Intent(MyPlayList.this, Playing.class);
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


                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(MyPlayList.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new My_PlayList_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(MyPlayList.this);

                /*url = postResponse.get(0).getMusic_url();
                Log.e("뭐얌", "url " +url);
                Glide.with(MyPlayList.this)
                        .load(postResponse.get(0).getMy_img_url())
                        .apply(new RequestOptions().circleCrop().centerCrop())
                        .centerCrop()
                        .circleCrop()
                        .into(p_img);
                p_title.setText(postResponse.get(0).getTitle());
                p_id.setText(postResponse.get(0).getId());*/




            }

            @Override
            public void onFailure(Call<ArrayList<Recent_Main_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );
                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });

    }

    //음악재생
    private void playAudio() {
        try {
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(url);
            player.prepare();
            player.start();
            player.setOnCompletionListener(completionListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 현재 일시정지가 되었는지 중지가 되었는지 헷갈릴 수 있기 때문에 스위치 변수를 선언해 구분할 필요가 있다. (구현은 안했다.)
    private void pauseAudio() {
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();

        }
    }

    private void resumeAudio() {
        if (player != null && !player.isPlaying()) {
            player.seekTo(position);
            player.start();

        }
    }

    private void stopAudio() {
        if(player != null && player.isPlaying()){
            player.stop();

        }
    }

    /* 녹음 시 마이크 리소스 제한. 누군가가 lock 걸어놓으면 다른 앱에서 사용할 수 없음.
     * 따라서 꼭 리소스를 해제해주어야함. */
    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
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
                        MyPlayList.this, // 현재 화면
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
                MyPlayList.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);

        if (!MyService.data){
            p_img.setVisibility(View.INVISIBLE);
            p_title.setText("노래를 선택해");
            p_id.setText("주세요");
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClicked(int position,String Music_url) {

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

    public void next_music(){
        a++;
        if(a == postResponse.size()){
            a = 0;
        }
        this.url = postResponse.get(a).getMusic_url();
        playAudio();
        p_play.setVisibility(View.INVISIBLE);
        p_pause.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(postResponse.get(a).getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(p_img);
        p_title.setText(postResponse.get(a).getTitle());
        p_id.setText(postResponse.get(a).getId());
    }
    public void before_music(){
        a--;
        if(a < 0){
            a = postResponse.size() - 1;
        }

        this.url = postResponse.get(a).getMusic_url();
        playAudio();
        p_play.setVisibility(View.INVISIBLE);
        p_pause.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(postResponse.get(a).getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(p_img);
        p_title.setText(postResponse.get(a).getTitle());
        p_id.setText(postResponse.get(a).getId());
    }
    //노래 종료시 다음 음악 재생
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next_music();
        }
    };


}