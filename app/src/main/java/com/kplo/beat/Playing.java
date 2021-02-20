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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Playing extends AppCompatActivity {

    AppCompatButton flow_b;

    private RetrofitAPI retrofitAPI;
    //음악재생 변수
    public String url;
    int position = 0;
    MediaPlayer player;
    ImageView p_next,p_play,p_pause,p_before,img,play_list,all_music,no_all_music,no_random_music,random_music,one_music,heart,heart_outline;
    TextView title,name,edit_t,time_e,time_n,heart_count;
    private Messenger mServiceMessenger = null;
    MyService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용
    ServiceConnection conn;
    String id;
    String feed_id;
    SeekBar seekBar;
    String music_idx;
    ArrayList<music_like_item> music_like_list = new ArrayList<>();




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
                        String value4 = msg.getData().getString("test4");
                        String value5 = msg.getData().getString("test5");
                        String value7 = msg.getData().getString("test7");
                        music_idx = value7;
                        music_like_count();
                        //좋아요 여부와 숫자 한번에 가져오기
                        Glide.with(Playing.this)
                                .load(value3)
                                .centerCrop()
                                .into(img);
                        title.setText(value2);
                        name.setText(value1);
                        feed_id = value1;
                        boolean isPlaying = msg.getData().getBoolean("isPlaying");
                        Log.e("isplaying", "" + isPlaying);
                        if (!isPlaying) {
                            p_play.setVisibility(View.VISIBLE);
                            p_pause.setVisibility(View.INVISIBLE);



                        } else {
                            p_play.setVisibility(View.INVISIBLE);
                            p_pause.setVisibility(View.VISIBLE);
                        }
                        int to = Integer.parseInt(value4);
                        int to2 = Integer.parseInt(value5);
                        seekBar.setProgress(to2);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                        time_e.setText(timeFormat.format(to));
                        time_n.setText(timeFormat.format(to2));
                        if (to < to2){
                            time_n.setText(timeFormat.format(to));
                        }
                        seekBar.setMax(to);
                    case MyService.time:
                        String value = msg.getData().getString("time");
                        if (value != null){
                            SimpleDateFormat timeFormat2 = new SimpleDateFormat("mm:ss");
                            int toss = Integer.parseInt(value);
                            time_n.setText(timeFormat2.format(toss));
                            seekBar.setProgress(toss);
                        }else {

                        }
                        break;
                }
            }
            return false;
        }
    }));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        flow_b = findViewById(R.id.flow_b);
        p_next = findViewById(R.id.p_next);
        p_play = findViewById(R.id.p_play);
        p_pause = findViewById(R.id.p_pause);
        p_before = findViewById(R.id.p_before);
        play_list = findViewById(R.id.play_list);
        img = findViewById(R.id.img);
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        seekBar = findViewById(R.id.seekBar);
        time_e = findViewById(R.id.time_e);
        time_n = findViewById(R.id.time_n);
        all_music = findViewById(R.id.all_music);
        no_all_music = findViewById(R.id.no_all_music);
        random_music = findViewById(R.id.random_music);
        no_random_music = findViewById(R.id.no_random_music);
        one_music = findViewById(R.id.one_music);
        heart = findViewById(R.id.heart);
        heart_outline = findViewById(R.id.heart_outline);
        heart_count = findViewById(R.id.heart_count);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        if (sf.getBoolean("allmusic"+id,false)){
            all_music.setVisibility(View.VISIBLE);
            no_all_music.setVisibility(View.INVISIBLE);
        }else{
            all_music.setVisibility(View.INVISIBLE);
            no_all_music.setVisibility(View.VISIBLE);

        }
        if (sf.getBoolean("onemusic"+id,false)){
            one_music.setVisibility(View.VISIBLE);
            no_all_music.setVisibility(View.INVISIBLE);
            all_music.setVisibility(View.INVISIBLE);
        }else{
        }
        if (sf.getBoolean("randommusic"+id,false)){
            random_music.setVisibility(View.INVISIBLE);
            no_random_music.setVisibility(View.VISIBLE);
        }else{
            random_music.setVisibility(View.VISIBLE);
            no_random_music.setVisibility(View.INVISIBLE);
        }
        play_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Playing.this, User_Play_list.class);
                startActivity(intent);
                finish();

            }
        });

        heart_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int music_idxs = Integer.parseInt(music_idx);
                //좋아요 오르게
                Call<Result2> call = retrofitAPI.insert_Music_like(music_idxs,id);
                call.enqueue(new Callback<Result2>() {
                    @Override
                    public void onResponse(Call<Result2> call, Response<Result2> response) {
                        Log.e("좋아요","상승");
                        heart_outline.setVisibility(View.INVISIBLE);
                        heart.setVisibility(View.VISIBLE);
                        String h_c_s = heart_count.getText().toString();
                        int h_c_i = Integer.parseInt(h_c_s) + 1;
                        heart_count.setText(String.valueOf(h_c_i));
                    }

                    @Override
                    public void onFailure(Call<Result2> call, Throwable t) {

                    }
                });
            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //좋아요 감소하게
                int music_idxs = Integer.parseInt(music_idx);
                //좋아요 오르게
                Call<Result2> call = retrofitAPI.delete_Music_like(music_idxs,id);
                call.enqueue(new Callback<Result2>() {
                    @Override
                    public void onResponse(Call<Result2> call, Response<Result2> response) {
                        Log.e("좋아요","감소");
                        heart.setVisibility(View.INVISIBLE);
                        heart_outline.setVisibility(View.VISIBLE);
                        String h_c_s = heart_count.getText().toString();
                        int h_c_i = Integer.parseInt(h_c_s) - 1;
                        heart_count.setText(String.valueOf(h_c_i));
                    }

                    @Override
                    public void onFailure(Call<Result2> call, Throwable t) {

                    }
                });
            }
        });

        one_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_all_music.setVisibility(View.VISIBLE);
                one_music.setVisibility(View.INVISIBLE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("allmusic"+id,false);// key, value를 이용하여 저장하는 형태
                editor.putBoolean("onemusic"+id,false);// key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.apply();
                editor.commit();
                Toast.makeText(getApplicationContext(), "노래가 반복되지 않습니다.", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                Message msg = Message.obtain(null, MyService.loop);
                msg.setData(bundle);
                try {
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

        all_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_music.setVisibility(View.INVISIBLE);
                one_music.setVisibility(View.VISIBLE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("onemusic"+id,true);// key, value를 이용하여 저장하는 형태
                editor.putBoolean("allmusic"+id,false);// key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.apply();
                editor.commit();
                Toast.makeText(getApplicationContext(), "노래가 한곡반복 됩니다.", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                Message msg = Message.obtain(null, MyService.loop);
                msg.setData(bundle);
                try {
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        no_all_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_music.setVisibility(View.VISIBLE);
                no_all_music.setVisibility(View.INVISIBLE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("allmusic"+id,true);// key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.apply();
                editor.commit();

                Toast.makeText(getApplicationContext(), "노래가 전체 반복됩니다.", Toast.LENGTH_SHORT).show();

            }
        });
        random_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                random_music.setVisibility(View.INVISIBLE);
                no_random_music.setVisibility(View.VISIBLE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("randommusic"+id,true);// key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.apply();
                editor.commit();
            }
        });
        no_random_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                random_music.setVisibility(View.VISIBLE);
                no_random_music.setVisibility(View.INVISIBLE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("randommusic"+id,false);// key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.apply();
                editor.commit();
            }
        });

        flow_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Playing.this, Feed.class);
                intent.putExtra("feed_id",feed_id); /*송신*/
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
                    p_play.setVisibility(View.INVISIBLE);
                    p_pause.setVisibility(View.VISIBLE);
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
                    p_play.setVisibility(View.INVISIBLE);
                    p_pause.setVisibility(View.VISIBLE);
                }
            }
        });
        p_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p_play.setVisibility(View.VISIBLE);
                p_pause.setVisibility(View.INVISIBLE);
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
                p_play.setVisibility(View.INVISIBLE);
                p_pause.setVisibility(View.VISIBLE);
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
                Bundle bundle = new Bundle();
                Message msg = Message.obtain(null, MyService.loop);
                msg.setData(bundle);
                try {
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        p_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p_play.setVisibility(View.INVISIBLE);
                p_pause.setVisibility(View.VISIBLE);
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
                Bundle bundle = new Bundle();
                Message msg = Message.obtain(null, MyService.loop);
                msg.setData(bundle);
                try {
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {// 사용자가 시크바를 움직이면
                    SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                    //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("mediaPlayer_current_position"+id,progress);// key, value를 이용하여 저장하는 형태
                    //최종 커밋
                    editor.apply();
                    editor.commit();

                    Bundle bundle = new Bundle();
                    bundle.putInt("seekto", progress);
                    Log.e("seekto",""+progress);
                    Message msg = Message.obtain(null, MyService.seekto);
                    msg.setData(bundle);
                    try {
                        mServiceMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat timeFormat3 = new SimpleDateFormat("mm:ss");
                    time_n.setText(timeFormat3.format(progress));

                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
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
                        Playing.this, // 현재 화면
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
                Playing.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);



    }

    public void music_like_count(){
        int music_idxs = Integer.parseInt(music_idx);
        Call<ArrayList<music_like_item>> call = retrofitAPI.getMusic_like_count(music_idxs,id);
        call.enqueue(new Callback<ArrayList<music_like_item>>() {
            @Override
            public void onResponse(Call<ArrayList<music_like_item>> call, Response<ArrayList<music_like_item>> response) {
                music_like_list = response.body();
                Log.e("불룬값",""+music_like_list.get(0).getHeart());
                if (music_like_list.get(0).getHeart() == 1){
                    heart.setVisibility(View.VISIBLE);
                    heart_outline.setVisibility(View.INVISIBLE);
                }else{
                    heart_outline.setVisibility(View.VISIBLE);
                    heart.setVisibility(View.INVISIBLE);
                }

                heart_count.setText(Integer.toString(music_like_list.get(0).getHeart_count()));
            }

            @Override
            public void onFailure(Call<ArrayList<music_like_item>> call, Throwable t) {

            }
        });

    }




}