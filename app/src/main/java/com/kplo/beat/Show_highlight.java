package com.kplo.beat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Show_highlight extends AppCompatActivity implements Show_highlight_Adapter.MyRecyclearViewClickListener {

    ImageView img,img_blur;

    private RetrofitAPI retrofitAPI;
    private ArrayList<Show_highlight_Item> postResponse;

    Show_highlight_Adapter adapter;
    LinearLayoutManager mManager;

    String id;

    String music_url_a;
    int time_s_a;
    int time_e_a;

    int nowposition;
    int m_getcurrent;


    MediaPlayer mediaPlayer;
    final Handler handler = new Handler();

    private Messenger mServiceMessenger = null;
    MyService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용
    ServiceConnection conn;
    private static final String BASE_URL = "http://15.164.220.153/";
    //서버에서데이터받기
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("test","act : what "+msg.what);
            Log.e("피니시",""+isFinishing());
            if(!isFinishing()) {
                switch (msg.what) {
                    case MyService.MSG_SEND_TO_ACTIVITY:
                        Log.e("뭐냐이건", "배달받음123123");
                        String value1 = msg.getData().getString("test1");
                        Log.i("test", "act : value1 " + value1);
                        String value2 = msg.getData().getString("test2");
                        Log.i("test", "act : value1 " + value2);
                        String value3 = msg.getData().getString("test3");
                        Log.i("test", "act : value1 " + value3);
                        String value6 = msg.getData().getString("test6");
                }
            }
            return false;
        }
    }));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_highlight);

        img = findViewById(R.id.img);
        img_blur = findViewById(R.id.img_blur);




        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);




        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Call<ArrayList<Show_highlight_Item>> call = retrofitAPI.all_show_highlight(id);
        call.enqueue(new Callback<ArrayList<Show_highlight_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Show_highlight_Item>> call, Response<ArrayList<Show_highlight_Item>> response) {
                postResponse = response.body();
                Collections.reverse(postResponse);
                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recycler_showhigihlight) ;

                mManager = new LinearLayoutManager(Show_highlight.this,LinearLayoutManager.VERTICAL,false);
                SnapHelper snapHelper = new PagerSnapHelper();
                recyclerView.setLayoutManager(mManager);

                RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastVisible != -1){
                            Log.d("20210401", "카운트"+lastVisible);
                            nextmusic();
                            nowposition = lastVisible;
                            adapter.get_music(lastVisible);
                            sendtime();
                        }
                        if (lastVisible >= totalItemCount - 1) {
                            Log.d("20210401", "lastVisibled");
                        }
                    }
                };

                recyclerView.addOnScrollListener(onScrollListener);

                snapHelper.attachToRecyclerView(recyclerView);
                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Show_highlight_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Show_highlight.this);


            }

           @Override
            public void onFailure(Call<ArrayList<Show_highlight_Item>> call, Throwable t) {

            }
        });
/*


        //이미지
        Glide.with(Show_highlight.this)
                .load("http://15.164.220.153/photo/JPEG_20210329_164252.jpg")
                .centerCrop()
                .into(img);

        //이미지 배경 - 블러
        Glide.with(Show_highlight.this)
                .load("http://15.164.220.153/photo/JPEG_20210329_164252.jpg")
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3)))
                .into(img_blur);
*/


    }

    @Override
    public void onItemClicked() {

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            m_getcurrent = mediaPlayer.getCurrentPosition();
            adapter.get_play(false);
        }else{
            mediaPlayer.seekTo(m_getcurrent);
            mediaPlayer.start();
            adapter.get_play(true);
        }

    }

    @Override
    public void getMusic_url(String music_url, int time_s, int time_e) {

        music_url_a = music_url;
        time_e_a = time_e;
        time_s_a = time_s;
        Log.d("20210401", "music_url_a"+music_url_a);
        Log.d("20210401", "time_e_a"+time_e_a);
        Log.d("20210401", "time_s_a"+time_s_a);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(music_url_a);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
            mediaPlayer.seekTo(time_s);
            mediaPlayer.start();


    }

    @Override
    public void user_id_Clicked(String feed_id) {

        Intent intent = new Intent(this, Feed.class);
        intent.putExtra("feed_id",feed_id); /*송신*/
        startActivity(intent);
    }

    @Override
    public void user_img_Cliked(String feed_id) {

        Intent intent = new Intent(this, Feed.class);
        intent.putExtra("feed_id",feed_id); /*송신*/
        startActivity(intent);
    }

    @Override
    public void heart_Cliked(int m_idx) {
        Call<Result2> call = retrofitAPI.delete_Music_like(m_idx,id);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                Log.e("좋아요","감소");
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

            }
        });
    }

    @Override
    public void heart_outline_Cliked(int m_idx) {

        Call<Result2> call = retrofitAPI.insert_Music_like(m_idx,id);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                Log.e("좋아요","상승");
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

            }
        });
    }

    @Override
    public void comment_cliked(int music_h_num) {

        Intent intent = new Intent(this, Music_highlight_comment.class);
        Log.e("긴급","+"+music_h_num);
        intent.putExtra("idx",music_h_num); /*송신*/
        startActivity(intent);
    }

    public void sendtime(){


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    Log.e("테스트","시간흐름");

                    if(mediaPlayer.getCurrentPosition() >= time_e_a){
                        nextmusic();
                        adapter.get_music(nowposition);
                    }


                }handler.postDelayed(this,1000);
            }
        },1000);
    }

    public void nextmusic(){

        if (handler != null){
            handler.removeMessages(0);
        }

        if(mediaPlayer != null){
            mediaPlayer.release();
            Log.e("뭐됨?","mediaPlayer"+mediaPlayer);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        m_getcurrent = mediaPlayer.getCurrentPosition();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mediaPlayer.seekTo(m_getcurrent);
        mediaPlayer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mServiceMessenger != null) {
            try {
                Bundle bundle = new Bundle();
                Message msg = Message.obtain(null, MyService.resumeAudio);
                msg.setData(bundle);
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {

                mServiceMessenger = new Messenger(service);
                try {
                    Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);

                    if (mServiceMessenger != null) {
                        try {
                            Bundle bundle = new Bundle();
                            Message msg2 = Message.obtain(null, MyService.pauseAudio);
                            msg2.setData(bundle);
                            mServiceMessenger.send(msg2);
                        } catch (RemoteException e) {
                        }
                    }

                } catch (RemoteException e) {
                }
                Intent intent = new Intent(
                        Show_highlight.this, // 현재 화면
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
                Show_highlight.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);



    }
}