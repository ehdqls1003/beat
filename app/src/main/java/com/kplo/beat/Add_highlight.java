package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.apptik.widget.MultiSlider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_highlight extends AppCompatActivity {

    String id;

    int First_T_one = 0;
    int First_T_two = 0;
    int Secound_T_one = 15000;
    int Secound_T_two = 15;

    int t_s = 0;
    int t_e = 15000;

    TextView time_n, time_e,time_e_e,time_n_e,time_c,title_r,name,edit_t;
    ImageView p_play,p_pause,img;

    int seekbar_p;
    boolean on = false;

    private RetrofitAPI retrofitAPI;

    MediaPlayer mediaPlayer;
    SimpleDateFormat timeFormat;

    MultiSlider multiSlider5,multiSlider6;
    boolean isplaying = false;

    final Handler handler = new Handler();

    int idx;
    String title,music_url,music_img_url,id_i;
    boolean su;
    int su_idx;

    private ArrayList<Music_highlight> postResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_highlight);

        time_e = findViewById(R.id.time_e);
        time_n = findViewById(R.id.time_n);
        time_e_e = findViewById(R.id.time_e_e);
        time_n_e = findViewById(R.id.time_n_e);
        time_c = findViewById(R.id.time_c);

        title_r = findViewById(R.id.title);
        name = findViewById(R.id.name);

        p_play = findViewById(R.id.p_play);
        p_pause = findViewById(R.id.p_pause);
        img = findViewById(R.id.img);
        edit_t = findViewById(R.id.edit_t);

        Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        idx = intent.getExtras().getInt("music_idx"); /*int형*/
        title = intent.getExtras().getString("title");
        music_url = intent.getExtras().getString("music_url");
        music_img_url = intent.getExtras().getString("music_img_url");
        id_i = intent.getExtras().getString("id_i");
        su = intent.getExtras().getBoolean("su");




        Log.e("20210329","받아오기idx"+idx);
        Log.e("20210329","받아오기title"+title);
        Log.e("20210329","받아오기music_url"+music_url);
        Log.e("20210329","받아오기music_img_url"+music_img_url);
        Log.e("20210329","받아오기id_i"+id_i);


        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);



        title_r.setText(title);
        name.setText(id_i);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //좋아요 여부와 숫자 한번에 가져오기
        Glide.with(Add_highlight.this)
                .load(music_img_url)
                .centerCrop()
                .into(img);

        try {
            mediaPlayer.setDataSource(music_url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
            mediaPlayer.pause();
        } catch (IOException e) {
            e.printStackTrace();
        }


        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        multiSlider5 = (MultiSlider)findViewById(R.id.range_slider5);
        multiSlider6 = (MultiSlider)findViewById(R.id.range_slider6);
        int Duration = mediaPlayer.getDuration();
        timeFormat = new SimpleDateFormat("mm:ss");
        //SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

        Log.e("듀레이션",""+Duration);

        multiSlider5.setMax(Duration);
        multiSlider6.setMax(Duration);

        time_n.setText(timeFormat.format(0));
        time_e.setText(timeFormat.format(Duration));
        time_n_e.setText(timeFormat.format(0));
        time_e_e.setText(timeFormat.format(15000));
        time_c.setText(timeFormat.format(0));

        multiSlider5.addThumbOnPos(1,15000);
        multiSlider6.getThumb(0).setRange( new ColorDrawable(0x00FF0000));

        if (su){
                Call<ArrayList<Music_highlight>>call2 = retrofitAPI.all_hightlight();
                call2.enqueue(new Callback<ArrayList<Music_highlight>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Music_highlight>> call, Response<ArrayList<Music_highlight>> response) {
                        Log.e("하이라이트 가져오기","완료");

                        postResponse = response.body();
                        for (int i = 0; i < postResponse.size(); i++){
                            if (postResponse.get(i).getMusic_idx() == idx){
                                su_idx = i;
                                Log.e("su_idx",""+su_idx);
                                break;
                            }
                        }

                        multiSlider5.getThumb(1).setValue(postResponse.get(su_idx).getTime_e());
                        multiSlider5.getThumb(0).setValue(postResponse.get(su_idx).getTime_s());
                        multiSlider6.getThumb(0).setValue(postResponse.get(su_idx).getTime_s());

                        time_n_e.setText(timeFormat.format(postResponse.get(su_idx).getTime_s()));
                        time_e_e.setText(timeFormat.format(postResponse.get(su_idx).getTime_e()));
                        time_c.setText(timeFormat.format(postResponse.get(su_idx).getTime_s()));


                    }

                    @Override
                    public void onFailure(Call<ArrayList<Music_highlight>> call, Throwable t) {

                    }
                });

        }



        /*time_n.setText(String.valueOf(multiSlider5.getThumb(0).getValue()));
        time_e.setText(String.valueOf(multiSlider5.getThumb(1).getValue()));*/

        multiSlider6.setEnabled(false);

        sendtime();

        edit_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (su){

                    Call<Result2> call2 = retrofitAPI.delete_highlight(postResponse.get(su_idx).getMusic_h_num());
                    call2.enqueue(new Callback<Result2>() {
                        @Override
                        public void onResponse(Call<Result2> call2, Response<Result2> response) {

                            Call<Result2> call = retrofitAPI.insert_hightlight(idx,t_s,t_e);
                            call.enqueue(new Callback<Result2>() {
                                @Override
                                public void onResponse(Call<Result2> call, Response<Result2> response) {

                                    Toast.makeText(getApplicationContext(), "하이라이트 수정이 완료되었습니다..", Toast.LENGTH_SHORT).show();

                                    finish();

                                }

                                @Override
                                public void onFailure(Call<Result2> call, Throwable t) {

                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<Result2> call2, Throwable t) {

                        }
                    });



            }else{

                    Call<Result2> call = retrofitAPI.insert_hightlight(idx,t_s,t_e);
                    call.enqueue(new Callback<Result2>() {
                        @Override
                        public void onResponse(Call<Result2> call, Response<Result2> response) {

                            Toast.makeText(getApplicationContext(), "하이라이트 등록이 완료되었습니다..", Toast.LENGTH_SHORT).show();

                            finish();

                        }

                        @Override
                        public void onFailure(Call<Result2> call, Throwable t) {

                        }
                    });

                }

            }
        });

        multiSlider5.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider,
                                       MultiSlider.Thumb thumb,
                                       int thumbIndex,
                                       int value)
            {

                Log.i("멀티1",""+thumbIndex);
                Log.i("멀티2",""+value);

                if (thumbIndex == 0) {
                    //왼쪽꺼움직였을때
                    First_T_one = value;
                    time_n_e.setText(timeFormat.format(value));
                    Log.e("0","움직임");
                    Log.e("0","움직임"+First_T_one);
                    if (Secound_T_one - First_T_one > 60000){
                        multiSlider5.getThumb(1).setValue(value + 60000);
                    }
                    First_T_two = First_T_one;
                    multiSlider6.getThumb(0).setValue(value);

                    mediaPlayer.seekTo(First_T_one);

                    t_s = value;

                } else {

                    Secound_T_one = value;
                    time_e_e.setText(timeFormat.format(value));
                    if(Secound_T_one - First_T_one > 60000){
                        multiSlider5.getThumb(0).setValue(value - 60000);
                        multiSlider6.getThumb(0).setValue(value - 60000);

                        First_T_two = value - 60000;
                        mediaPlayer.seekTo(First_T_one);

                    }
                    t_e = value;

                    //오른쪽꺼 움직였을때*/

                }
            }
        });



        multiSlider6.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider,
                                       MultiSlider.Thumb thumb,
                                       int thumbIndex,
                                       int value)
            {

                time_c.setText(timeFormat.format(value));
                if (thumbIndex == 0) {
                    //왼쪽꺼움직였을때
                    if (on){
                        //왼쪽거 이상
                        if(First_T_one > value){
                            time_c.setText(timeFormat.format(First_T_two));
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(First_T_one);
                            multiSlider6.getThumb(0).setValue(First_T_one);
                            p_play.setVisibility(View.VISIBLE);
                            p_pause.setVisibility(View.INVISIBLE);
                            multiSlider5.setEnabled(true);
                            multiSlider6.setEnabled(false);
                            isplaying = false;

                        }else if(Secound_T_one < value){
                            time_c.setText(timeFormat.format(First_T_two));
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(First_T_one);
                            multiSlider6.getThumb(0).setValue(First_T_one);
                            p_play.setVisibility(View.VISIBLE);
                            p_pause.setVisibility(View.INVISIBLE);
                            multiSlider5.setEnabled(true);
                            multiSlider6.setEnabled(false);
                            isplaying = false;
                        }else {
                            //mediaPlayer.seekTo(value);
                        }
                    }
                } else {
                    //오른쪽꺼 움직였을때*/
                }
            }
        });

        multiSlider6.setOnTrackingChangeListener(new MultiSlider.OnTrackingChangeListener() {
            @Override
            public void onStartTrackingTouch(MultiSlider multiSlider, MultiSlider.Thumb thumb, int value) {
                mediaPlayer.seekTo(value);
            }

            @Override
            public void onStopTrackingTouch(MultiSlider multiSlider, MultiSlider.Thumb thumb, int value) {
                mediaPlayer.seekTo(value);
            }
        });



        p_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                p_play.setVisibility(View.INVISIBLE);
                p_pause.setVisibility(View.VISIBLE);
                /*
                multiSlider5.addThumbOnPos(2, First_T_one);*/
                multiSlider5.setEnabled(false);
                multiSlider6.setEnabled(true);
                on = true;
                isplaying =true;

            }
        });

        p_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                p_play.setVisibility(View.VISIBLE);
                p_pause.setVisibility(View.INVISIBLE);
                multiSlider5.setEnabled(true);
                multiSlider6.setEnabled(false);
                mediaPlayer.seekTo(First_T_one);
                on = false;
                isplaying = false;
                multiSlider6.getThumb(0).setValue(First_T_one);
            }
        });

    }

    public void sendtime(){


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("테스트",""+mediaPlayer.isPlaying());
                if (mediaPlayer.isPlaying()) {
                    Log.e("테스트","시간흐름");
                    if (isplaying){
                        multiSlider6.getThumb(0).setValue(mediaPlayer.getCurrentPosition());
                    }


                }handler.postDelayed(this,1000);
            }
        },1000);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null){
            handler.removeMessages(0);
        }

        mediaPlayer.release();


    }
}