package com.kplo.beat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service {

    MediaPlayer mp3;
    /*String url = "http://15.164.220.153/music/Forget Your Love.mp3";*/
    //음악재생 변수
    public String url;
    private int item_position;
    ImageView p_next, p_play, p_pause, p_before, p_img;
    TextView p_title, p_id;
    private RetrofitAPI retrofitAPI;

    static String MESSAGE_KEY = ""; // intent로 넘어오는 값

    private MediaPlayer mediaPlayer;
    public static boolean isPlaying = false;
    public static boolean data = false;
    ArrayList<User_Music_List> postResponse;
    ArrayList<String> playList = new ArrayList<>();
    public static int current_position;
    public static final int MSG_SEND_TO_ACTIVITY = 4;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int playAudio = 101;
    public static final int pauseAudio = 102;
    public static final int resumeAudio = 103;
    public static final int next_music = 104;
    public static final int before_music = 105;
    public static final int update = 106;
    public static final int unbind = 107;
    public static final int clickmusic = 108;
    public static final int getData = 109;
    public static final int getData2 = 110;
    public static final int time = 111;
    public static final int seekto = 112;
    public static final int loop = 113;
    public static final int give = 114;

    private Messenger mClient = null;
    RemoteViews remoteViews;
    NotificationManager notificationManager;
    NotificationTarget notificationTarget2;
    Notification a;
    boolean close = false;
    boolean nextclick = false;


    private ArrayList<Integer> listsun = new ArrayList<>();

    String id;
    public MyService() {
    }

    IBinder mBinder = new MyBinder();

    class MyBinder extends Binder {
        MyService getService() { // 서비스 객체를 리턴
            return MyService.this;
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("흠","onCreate");
        Log.e("마이서비스", "onCreate");
        /*retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        getData();*/

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        if(sf.getInt("mediaPlayer_current_position"+id,0) != 0){

        }
        if(sf.getInt("mediaPlayer_item_position"+id,0) != 0){
            item_position = sf.getInt("mediaPlayer_item_position"+id,0);
            //임시임시
            Log.e("흠","처음 itemposition 들고옴"+item_position);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.util.Log.i("테스트", "onStartCommand() 호출");
        Log.e("흠","onStartCommand");


        Log.e("흠","겟데이터전"+item_position);
        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        getData();
        Log.e("흠","겟데이터후"+item_position);
        Log.e("마이서비스", "onStartCommand");

        if (intent != null) {
            String action = intent.getAction();
            if (CommandActions.TOGGLE_PLAY.equals(action)) {
                changeplay();
                if (isPlaying) {
                    pauseAudio();
                    Log.e("마이서비스","isplaying"+isPlaying);

                } else {

                    if (current_position != 0){
                        resumeAudio();
                        Log.e("마이서비스","isplaying"+isPlaying);
                    }else{
                        playAudio();
                    }
                }
            } else if (CommandActions.REWIND.equals(action)) {
                before_music();
            } else if (CommandActions.FORWARD.equals(action)) {
                next_music();
            } else if (CommandActions.CLOSE.equals(action)) {
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int mediaPlayer_current_position = mediaPlayer.getCurrentPosition(); // 사용자가 입력한 저장할 데이터
                int mediaPlayer_item_position = item_position;
                int mediaPlayer_Duration = mediaPlayer.getDuration();
                editor.putInt("mediaPlayer_current_position"+id,mediaPlayer_current_position);
                editor.putInt("mediaPlayer_item_position"+id,mediaPlayer_item_position);// key, value를 이용하여 저장하는 형태
                editor.putInt("mediaPlayer_Duration"+id,mediaPlayer_Duration);// key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.apply();
                editor.commit();
            closePlayer();
            current_position = 0;
            stopForeground(true);
            /*notificationManager.cancel(1);*/
        }
        }
        sendtime();


        return START_NOT_STICKY;

    /*return super.onStartCommand(intent, flags, startId);*/
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next_music();
            sendMsgToActivity();
            nextclick = false;
        }
    };

    @Override
    public void onDestroy() {
        Log.e("흠","onDestroy");
        android.util.Log.i("테스트", "onDestory()호출");

        Log.e("마이서비스", "onDestroy");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        Log.e("마이서비스", "onBind");
        /*mBinder*/
        return mMessenger.getBinder(); // 서비스 객체를 리턴
    }


    public void getData() {
        Log.e("마이서비스", "getData");

        Call<ArrayList<User_Music_List>> call = retrofitAPI.User_play_list(id);

        call.enqueue(new Callback<ArrayList<User_Music_List>>() {
            @Override
            public void onResponse(Call<ArrayList<User_Music_List>> call, Response<ArrayList<User_Music_List>> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();

                listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);

                Log.e("리스트사이즈?","+"+listsun.size());

                for (int i = 0; i < listsun.size(); i++){
                    for(int j = 0; j < postResponse.size(); j++){
                        if (listsun.get(i).equals(postResponse.get(j).getList_num())){
                            User_Music_List person = postResponse.get(j);
                            //이동할 객체 삭제
                            postResponse.remove(j);
                            //이동하고 싶은 position에 추가
                            postResponse.add(i,person);
                            //Adapter에 데이터 이동알림
                        }
                    }
                }

                    SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
                    //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
                    int playing_m = sf.getInt("playing_m"+id,0);
                    for (int i = 0; i <postResponse.size(); i++){
                        if(postResponse.get(i).getList_num() == playing_m){
                            item_position = i;
                        }
                    }



                if (postResponse.size() != 0){
                    data = true;
                }
                playList.clear();
                Log.e("마이서비스", "postResponse받음");
                for (int i = 0; i < postResponse.size(); i++) {
                    playList.add(postResponse.get(i).getMusic_url());
                }

                sendMsgToActivity();

            }

            @Override
            public void onFailure(Call<ArrayList<User_Music_List>> call, Throwable t) {

                Log.i("onResponse", "3");
                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });


    }
    public void getData2() {
        Log.e("마이서비스", "getData");

        Call<ArrayList<User_Music_List>> call = retrofitAPI.User_play_list(id);

        call.enqueue(new Callback<ArrayList<User_Music_List>>() {
            @Override
            public void onResponse(Call<ArrayList<User_Music_List>> call, Response<ArrayList<User_Music_List>> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();

                listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);

                Log.e("리스트사이즈?","+"+listsun.size());

                for (int i = 0; i < listsun.size(); i++){
                    for(int j = 0; j < postResponse.size(); j++){
                        if (listsun.get(i).equals(postResponse.get(j).getList_num())){
                            User_Music_List person = postResponse.get(j);
                            //이동할 객체 삭제
                            postResponse.remove(j);
                            //이동하고 싶은 position에 추가
                            postResponse.add(i,person);
                            //Adapter에 데이터 이동알림
                        }
                    }
                }

                playList = new ArrayList<>();

                Log.e("마이서비스", "postResponse받음");
                for (int i = 0; i < postResponse.size(); i++) {
                    playList.add(postResponse.get(i).getMusic_url());
                }
                sendMsgToActivity();
                item_position = postResponse.size()-1;
                closePlayer();
                playAudio();
                sendMsgToActivity();


            }

            @Override
            public void onFailure(Call<ArrayList<User_Music_List>> call, Throwable t) {

                Log.i("onResponse", "3");
                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });


    }
    public void getData3() {
        Log.e("마이서비스", "getData");

        Call<ArrayList<User_Music_List>> call = retrofitAPI.User_play_list(id);

        call.enqueue(new Callback<ArrayList<User_Music_List>>() {
            @Override
            public void onResponse(Call<ArrayList<User_Music_List>> call, Response<ArrayList<User_Music_List>> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }
                String delplay = playList.get(item_position);

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                playList = new ArrayList<>();

                Log.e("마이서비스", "postResponse받음");
                for (int i = 0; i < postResponse.size(); i++) {
                    playList.add(postResponse.get(i).getMusic_url());
                }


                SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);

                //현재 재생중인 노래 삭제
                if (item_position != playList.size()){
                    if (item_position > playList.size() - 1){
                        item_position = 0;
                        closePlayer();
                        if (sf.getBoolean("allmusic"+id,false)){
                            playAudio();
                        }else {
                            playAudio();
                            pauseAudio();
                        }
                    }else{
                        if(!playList.get(item_position).equals(delplay)){
                            closePlayer();
                            playAudio();
                            Log.e("현재 재생중인거 삭제함","ㅎㅎ");
                        }
                    }

                }else{
                    closePlayer();
                    item_position = 0;

                    if (playList != null){
                        if (sf.getBoolean("allmusic"+id,false)){
                            playAudio();
                        }else {
                            playAudio();
                            pauseAudio();
                        }
                    }
                }


                sendMsgToActivity();


            }

            @Override
            public void onFailure(Call<ArrayList<User_Music_List>> call, Throwable t) {

                Log.i("onResponse", "3");
                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });


    }
    //음악재생
    public void playAudio() {
        if (postResponse.size() != 0){
            startForegroundService();


        mediaPlayer = new MediaPlayer();
        Log.e("흠","미디어플레이어에 적용 itemposition 들고옴"+item_position);
        try {
            mediaPlayer.setDataSource(playList.get(item_position));

            SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
            //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String text = playList.get(item_position); // 사용자가 입력한 저장할 데이터
            int text2 = postResponse.get(item_position).getList_num();
            editor.putInt("playing_m"+id,text2); // key, value를 이용하여 저장하는 형태
            //최종 커밋
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        if(sf.getInt("mediaPlayer_current_position"+id,0) != 0){
            SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
            mediaPlayer.seekTo(sf.getInt("mediaPlayer_current_position"+id,0));

            //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("mediaPlayer_current_position"+id,0);// key, value를 이용하여 저장하는 형태
            //최종 커밋
            editor.apply();
            editor.commit();
        }

        mediaPlayer.start(); // 음원 재생
        mediaPlayer.setOnCompletionListener(completionListener);
        isPlaying = mediaPlayer.isPlaying();

        sendtime();
        changeplay();

        }
    }

    // 현재 일시정지가 되었는지 중지가 되었는지 헷갈릴 수 있기 때문에 스위치 변수를 선언해 구분할 필요가 있다. (구현은 안했다.)
    public void pauseAudio() {

        if (postResponse.size() != 0) {
            if (mediaPlayer != null) {
                current_position = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                isPlaying = mediaPlayer.isPlaying();
                changeplay();

                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int mediaPlayer_current_position = mediaPlayer.getCurrentPosition(); // 사용자가 입력한 저장할 데이터
                int mediaPlayer_Duration = mediaPlayer.getDuration();
                editor.putInt("mediaPlayer_current_position" + id, mediaPlayer_current_position);
                editor.putInt("mediaPlayer_Duration" + id, mediaPlayer_Duration);// key, value를 이용하여 저장하는 형태

                //최종 커밋
                editor.apply();
                editor.commit();
            }
        }
    }

    int getCurrent_position() {
        return current_position;
    }

    public void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
            //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
            current_position = sf.getInt("mediaPlayer_current_position"+id,0);
            sendtime();
            mediaPlayer.seekTo(current_position);
            mediaPlayer.start();
            isPlaying = mediaPlayer.isPlaying();
            changeplay();
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            isPlaying = mediaPlayer.isPlaying();


        }
    }

    /* 녹음 시 마이크 리소스 제한. 누군가가 lock 걸어놓으면 다른 앱에서 사용할 수 없음.
     * 따라서 꼭 리소스를 해제해주어야함. */
    public void closePlayer() {
        if (mediaPlayer != null) {
            isPlaying = false;
            mediaPlayer.release();
        }
    }

    public void next_music() {
        item_position++;
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환

        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mediaPlayer_current_position"+id,0);// key, value를 이용하여 저장하는 형태
        //최종 커밋
        editor.apply();
        editor.commit();
        closePlayer();
        if (item_position == postResponse.size()) {
            boolean a = sf.getBoolean("allmusic"+id,false);
            Log.e("불룬",""+a);
            if(!sf.getBoolean("allmusic"+id,false)){
                if (sf.getBoolean("onemusic"+id,false)){
                    if(nextclick){
                        item_position = 0;
                        playAudio();
                        nextclick =false;
                    }else{
                        item_position--;
                        playAudio();
                    }
                }else{
                    item_position = 0;
                    playAudio();
                    pauseAudio();
                }
            }else{
                item_position = 0;
                playAudio();
            }
        }else{
            playAudio();
        }
        this.url = postResponse.get(item_position).getMusic_url();


    }

    public void before_music() {
        item_position--;
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환

        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mediaPlayer_current_position"+id,0);// key, value를 이용하여 저장하는 형태
        //최종 커밋
        editor.apply();
        editor.commit();
        closePlayer();
        if (item_position < 0) {
            item_position = postResponse.size() - 1;
        }

        this.url = postResponse.get(item_position).getMusic_url();
        playAudio();
/*        p_play.setVisibility(View.INVISIBLE);
        p_pause.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(postResponse.get(a).getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(p_img);
        p_title.setText(postResponse.get(a).getTitle());
        p_id.setText(postResponse.get(a).getId());*/
    }



    public String getId() {
        return postResponse.get(item_position).getMusic_id();
    }

    public String getTitle() {
        return postResponse.get(item_position).getMusic_title();
    }

    public String getMy_img_url() {
        Log.e("마이서비스", "이미지 보내줘");
        return postResponse.get(item_position).getMusic_img();
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, MyPlayList.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        remoteViews = createRemoteView(R.layout.notification_player);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snwodeer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(true);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            /*notificationManager.createNotificationChannel(channel);*/

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        a = builder.build();
        startForeground(1, builder.build());
        NotificationTarget notificationTarget = new NotificationTarget(
                        this,
                R.id.img_albumart,
                remoteViews,
                        a,
                        1);

        notificationTarget2 = new NotificationTarget(
                this,
                R.id.btn_play_pause,
                remoteViews,
                a,
                1);


        String title = getTitle();
        remoteViews.setTextViewText(R.id.txt_title, title);



        Glide.with(this)
                .asBitmap()
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .load(getMy_img_url())
                .into(notificationTarget);
        if(!isPlaying){
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.play)
                    .into(notificationTarget2);
        }else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.pause)
                    .into(notificationTarget2);
        }



    }

    void changeplay(){
        if(!isPlaying){
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.play)
                .into(notificationTarget2);
        }else{
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.pause)
                .into(notificationTarget2);
        }


    }


    private RemoteViews createRemoteView(int layoutId) {
        RemoteViews remoteView = new RemoteViews(this.getPackageName(), layoutId);
        Intent actionTogglePlay = new Intent(this,MyService.class);
        actionTogglePlay.setAction(CommandActions.TOGGLE_PLAY);
        Intent actionForward = new Intent(this,MyService.class);
        actionForward.setAction(CommandActions.FORWARD);
        Intent actionRewind = new Intent(this,MyService.class);
        actionRewind.setAction(CommandActions.REWIND);
        Intent actionClose = new Intent(this,MyService.class);
        actionClose.setAction(CommandActions.CLOSE);
        PendingIntent togglePlay = PendingIntent.getService(MyService.this, 0, actionTogglePlay, 0);
        PendingIntent forward = PendingIntent.getService(MyService.this, 0, actionForward, 0);
        PendingIntent rewind = PendingIntent.getService(MyService.this, 0, actionRewind, 0);
        PendingIntent close = PendingIntent.getService(MyService.this, 0, actionClose, 0);

        remoteView.setOnClickPendingIntent(R.id.btn_play_pause, togglePlay);
        remoteView.setOnClickPendingIntent(R.id.btn_forward, forward);
        remoteView.setOnClickPendingIntent(R.id.btn_rewind, rewind);
        remoteView.setOnClickPendingIntent(R.id.btn_close, close);
        Log.e("마이서비스","리모트뷰생성");
        return remoteView;
    }




    /**
     * activity로부터 binding 된 Messenger
     */
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;  // activity로부터 가져온
                    break;
                case playAudio:
                    if (postResponse.size() != 0){
                        playAudio();
                        sendMsgToActivity();

                    }
                    break;
                case pauseAudio:
                    if (postResponse.size() != 0) {
                        pauseAudio();
                        sendMsgToActivity();
                    }
                    break;
                case resumeAudio:
                    if (postResponse.size() != 0) {
                        resumeAudio();
                        sendMsgToActivity();
                    }
                    break;
                case next_music:
                    if (postResponse.size() != 0) {
                        nextclick = true;
                        next_music();
                        sendMsgToActivity();
                    }
                    break;
                case before_music:
                    if (postResponse.size() != 0) {
                        before_music();
                        sendMsgToActivity();
                    }
                    break;
                case update:
                    Log.e("업데이트받음","업데이트받음");
                    sendMsgToActivity();
                    break;
                case clickmusic:
                    closePlayer();
                    item_position = msg.getData().getInt("item_position");
                    playAudio();
                    sendMsgToActivity();
                    break;
                case getData:
                    Log.e("겟데이타","들어옴");
                    getData2();
                    break;
                case getData2:
                    getData3();
                    break;
                case seekto:
                    int clickseekto;
                    clickseekto = msg.getData().getInt("seekto");
                    SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                    //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("mediaPlayer_current_position"+id,clickseekto);
                    //최종 커밋
                    editor.apply();
                    editor.commit();
                    if (isPlaying){

                        mediaPlayer.seekTo(clickseekto);
                    }
                    break;
                case loop:
                    SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
                    if (sf.getBoolean("onemusic"+id,false)){
                        mediaPlayer.setLooping(true); // true 연속재생, false 한번 재생
                        Log.e("한곡재생","한다"+sf.getBoolean("onemusic"+id,false));
                    }else{
                        mediaPlayer.setLooping(false); // true 연속재생, false 한번 재생
                        Log.e("한곡재생","안한다"+sf.getBoolean("onemusic"+id,false));
                    }
                    break;

            }
            return false;
        }
    }));

    private void sendMsgToActivity() {
        try {
            if (postResponse.size() != 0) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isPlaying", isPlaying);
                bundle.putString("test1", "" + postResponse.get(item_position).getMusic_id());
                bundle.putString("test2", "" + postResponse.get(item_position).getMusic_title());
                bundle.putString("test3", "" + postResponse.get(item_position).getMusic_img());
                bundle.putString("test6", "" + item_position);
                bundle.putString("test7",""+postResponse.get(item_position).getMusic_idx());
                if (mediaPlayer != null && isPlaying == true) {
                    bundle.putString("test4", "" + mediaPlayer.getDuration());
                } else {
                    SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                    bundle.putString("test4", "" + sf.getInt("mediaPlayer_Duration" + id, 0));
                }
                if (mediaPlayer != null && isPlaying == true) {
                    bundle.putString("test5", "" + mediaPlayer.getCurrentPosition());
                } else {
                    SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                    bundle.putString("test5", "" + sf.getInt("mediaPlayer_current_position" + id, 0));
                }
                Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
                msg.setData(bundle);
                mClient.send(msg);
            }else{

            }     // msg 보내기
            } catch(RemoteException e){
            }

    }

    private void sendtime() {
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    Bundle bundle = new Bundle();
                    bundle.putString("time", "" + mediaPlayer.getCurrentPosition());
                    Message msg = Message.obtain(null, time);
                    msg.setData(bundle);
                    Log.e("시간","흐르는중"+mediaPlayer.getCurrentPosition());
                    try {
                        mClient.send(msg);      // msg 보내기
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this,50);
                }
            }
        },50);
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


}







