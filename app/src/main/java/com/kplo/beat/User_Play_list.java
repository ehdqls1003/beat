package com.kplo.beat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.preference.PreferenceManager;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Play_list extends AppCompatActivity implements User_Playlist_Adapter.MyRecyclearViewClickListener {



    User_Playlist_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    //음악재생 변수
    public String url;
    int position = 0;
    MediaPlayer player;
    ImageView p_next,p_play,p_pause,p_before,p_img;
    TextView p_title,p_id,edit_t;
    private Messenger mServiceMessenger = null;
    MyService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용
    ServiceConnection conn;
    String id,ids;
    int delete_music_num;
    String feed_id;
    int poss;

    String item_position;
    String positions;


    ArrayList<Integer> listsun;

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
                        String value6 = msg.getData().getString("test6");
                        adapter.playing(value6);
                        positions = value6;
                        Glide.with(User_Play_list.this)
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

    ArrayList<User_Music_List> postResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__play_list);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");
        ids = id;

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        recent_music();

        p_next = findViewById(R.id.p_next);
        p_play = findViewById(R.id.p_play);
        p_pause = findViewById(R.id.p_pause);
        p_before = findViewById(R.id.p_before);
        p_title = findViewById(R.id.p_title);
        p_id = findViewById(R.id.p_name);
        p_img = findViewById(R.id.p_img);
        edit_t = findViewById(R.id.edit_t);

        p_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Play_list.this, Playing.class);
                startActivity(intent);
                finish();
            }
        });

        edit_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Play_list.this, Edit_list.class);

                intent.putExtra("item_position",positions); /*송신*/

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
                        adapter.isplaying();
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
                        adapter.isplaying();
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

        Call<ArrayList<User_Music_List>> call = retrofitAPI.User_play_list(id);

        call.enqueue(new Callback<ArrayList<User_Music_List>>() {
            @Override
            public void onResponse(Call<ArrayList<User_Music_List>> call, Response<ArrayList<User_Music_List>> response) {

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                Log.e("리사이클러뷰사이즈",""+postResponse.size());

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(User_Play_list.this,LinearLayoutManager.VERTICAL,false)) ;


                listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);

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

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new User_Playlist_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;


                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(User_Play_list.this);





            }

            @Override
            public void onFailure(Call<ArrayList<User_Music_List>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void recent_music2() {

        Call<ArrayList<User_Music_List>> call = retrofitAPI.User_play_list(id);

        call.enqueue(new Callback<ArrayList<User_Music_List>>() {
            @Override
            public void onResponse(Call<ArrayList<User_Music_List>> call, Response<ArrayList<User_Music_List>> response) {

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                Log.e("리사이클러뷰사이즈",""+postResponse.size());

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(User_Play_list.this,LinearLayoutManager.VERTICAL,false)) ;

                listsun.clear();
                for (int i = 0; i<postResponse.size(); i++){
                    listsun.add(postResponse.get(i).getList_num());
                }
                setStringArrayPref(getApplicationContext(),"listsun"+ids,listsun);

                listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);
                Log.e("listsunsize",""+listsun.size());

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

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new User_Playlist_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;


                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(User_Play_list.this);

                adapter.playing(positions);


            }

            @Override
            public void onFailure(Call<ArrayList<User_Music_List>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });


    }

    /*//음악재생
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

    *//* 녹음 시 마이크 리소스 제한. 누군가가 lock 걸어놓으면 다른 앱에서 사용할 수 없음.
     * 따라서 꼭 리소스를 해제해주어야함. *//*
    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        recent_music();
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
                        User_Play_list.this, // 현재 화면
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
                User_Play_list.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);

        if (!MyService.data){
            p_img.setVisibility(View.INVISIBLE);
            p_title.setText("노래를 선택해");
            p_id.setText("주세요");
        }

        listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);

    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClicked(int position,String Music_url) {

        p_play.setVisibility(View.INVISIBLE);
        p_pause.setVisibility(View.VISIBLE);
        if (mServiceMessenger != null) {
            try {

                Bundle bundle = new Bundle();
                bundle.putInt("item_position",position);
                Message msg = Message.obtain(null, MyService.clickmusic);
                msg.setData(bundle);
                mServiceMessenger.send(msg);      // msg 보내기

            } catch (RemoteException e) {
            }
        }

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
    public void onMore_buttonClicked(int pos, String Music_id, int music_num) {

        delete_music_num = music_num;
        poss = pos;
        feed_id = Music_id;
        OnClickupload();
    }
    //게시물 작성 버튼클릭
    public void OnClickupload()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(R.array.user_play_list, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(final DialogInterface dialog, final int pos)
            {
                String[] items = getResources().getStringArray(R.array.user_play_list);
                if (items[pos].equals("아티스트 피드가기")){

                    Intent intent = new Intent(User_Play_list.this, Feed.class);
                    intent.putExtra("feed_id",feed_id); /*송신*/
                    startActivity(intent);

                }else if(items[pos].equals("삭제")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(User_Play_list.this);

                    builder.setTitle("삭제").setMessage("선택하신 1곡을 재생 목록에서 삭제시키겠습니까?");

                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                            Call<Result2> call5 = retrofitAPI.delete_play_list_music(delete_music_num);
                            call5.enqueue(new Callback<Result2>() {
                                @Override
                                public void onResponse(Call<Result2> call, Response<Result2> response) {
                                    Log.e("노래","삭제완료");

                                    recent_music2();

                                    Bundle bundle = new Bundle();
                                    Message msg = Message.obtain(null, MyService.getData2);
                                    msg.setData(bundle);
                                    try {
                                        mServiceMessenger.send(msg);      // msg 보내기
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(Call<Result2> call, Throwable t) {

                                }
                            });

                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                    });


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }


            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*public void next_music(){
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
    }*/
    //노래 종료시 다음 음악 재생
    /*MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next_music();
        }
    };*/


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    public void setStringArrayPref(Context context, String key, ArrayList<Integer> values) {
        Log.e("어레이저장함","저장함");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();

    }



    @Override
    protected void onStop() {
        super.onStop();
    }
}