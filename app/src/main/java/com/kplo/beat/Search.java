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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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

public class Search extends AppCompatActivity implements Search_Adapter.MyRecyclearViewClickListener, Search_Adapter2.MyRecyclearViewClickListener, My_PlayList_Adapter.MyRecyclearViewClickListener {

    EditText search;
    ImageView search_icon;
    private RetrofitAPI retrofitAPI;
    ArrayList<Search_Item> postResponse;
    ArrayList<Flow_Item> postResponse2;
    ArrayList<Recent_Main_Item> postResponse3,postResponse3_2;
    Search_Adapter adapter;
    Search_Adapter2 adapter2;
    My_PlayList_Adapter adapter3;
    RecyclerView recyclerView,recyclerView2,recyclerView3;

    View middleline;
    TextView gok,gok2,gok3;
    String s_search;

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
                        Glide.with(Search.this)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search = findViewById(R.id.search);
        search_icon = findViewById(R.id.search_icon);
        recyclerView = findViewById(R.id.recent_music_recycler);
        recyclerView2 = findViewById(R.id.artiest);
        recyclerView3 = findViewById(R.id.music);

        middleline = findViewById(R.id.middleline);
        gok = findViewById(R.id.gok);
        gok2 = findViewById(R.id.gok2);
        gok3 = findViewById(R.id.gok3);

        p_next = findViewById(R.id.p_next);
        p_play = findViewById(R.id.p_play);
        p_pause = findViewById(R.id.p_pause);
        p_before = findViewById(R.id.p_before);
        p_title = findViewById(R.id.p_title);
        p_id = findViewById(R.id.p_name);
        p_img = findViewById(R.id.p_img);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        search.setCursorVisible(false);


        p_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Playing.class);
                startActivity(intent);
            }
        });

        gok3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this,Search_more.class);
                intent.putExtra("search",s_search);
                startActivity(intent);
            }

        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setCursorVisible(true);
                search.requestFocus();

                gok.setVisibility(View.INVISIBLE);
                gok2.setVisibility(View.INVISIBLE);
                gok3.setVisibility(View.INVISIBLE);
                middleline.setVisibility(View.INVISIBLE);
                recyclerView2.setVisibility(View.INVISIBLE);
                recyclerView3.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("onTextChanged","charSequence : "+charSequence);
                s_search = charSequence.toString();
                search(s_search);

                gok.setVisibility(View.INVISIBLE);
                gok2.setVisibility(View.INVISIBLE);
                gok3.setVisibility(View.INVISIBLE);
                middleline.setVisibility(View.INVISIBLE);
                recyclerView2.setVisibility(View.INVISIBLE);
                recyclerView3.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //엔터키 입력했을때
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i){
                    case KeyEvent.KEYCODE_ENTER:
                        search.setCursorVisible(false);
                        search.clearFocus();
                        recyclerView.setVisibility(View.INVISIBLE);
                        search2(s_search);
                        gok.setVisibility(View.VISIBLE);
                        gok2.setVisibility(View.VISIBLE);
                        gok3.setVisibility(View.VISIBLE);
                        middleline.setVisibility(View.VISIBLE);
                        //event
                        return true;
                }
                return false;
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

    public void search(String search){
        Call<ArrayList<Search_Item>> call = retrofitAPI.search_music(search);
        call.enqueue(new Callback<ArrayList<Search_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Search_Item>> call, Response<ArrayList<Search_Item>> response) {
                postResponse = response.body();
                // 리사이클러뷰에 LinearLayoutManager 객체 지정.

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(Search.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Search_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Search.this);
            }

            @Override
            public void onFailure(Call<ArrayList<Search_Item>> call, Throwable t) {

            }
        });
    }

    public void search2(String search){
        Call<ArrayList<Flow_Item>> call = retrofitAPI.search_artiest(search);
        call.enqueue(new Callback<ArrayList<Flow_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Flow_Item>> call, Response<ArrayList<Flow_Item>> response) {
                postResponse2 = response.body();
                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                if (postResponse2 != null) {
                    recyclerView2.setVisibility(View.VISIBLE);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));

                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                    adapter2 = new Search_Adapter2(postResponse2);
                    recyclerView2.setAdapter(adapter2);

                    //이거안해주면 리스너안먹힘
                    adapter2.setOnClickListener(Search.this);
                }
                search3(s_search);


            }

            @Override
            public void onFailure(Call<ArrayList<Flow_Item>> call, Throwable t) {

            }
        });

    }

    public void search3(String search){
        Call<ArrayList<Recent_Main_Item>> call = retrofitAPI.search_music2(search);
        call.enqueue(new Callback<ArrayList<Recent_Main_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Recent_Main_Item>> call, Response<ArrayList<Recent_Main_Item>> response) {
                postResponse3 = response.body();
                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                if (postResponse3 != null) {
                    recyclerView3.setVisibility(View.VISIBLE);
                    recyclerView3.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
                    postResponse3_2 = new ArrayList<Recent_Main_Item>();
                    if (postResponse3.size() < 6){
                        for (int i = 0; i < postResponse3.size(); i++) {
                            postResponse3_2.add(postResponse3.get(i));
                        }
                    }else {
                        for (int i = 0; i < 5; i++) {
                            postResponse3_2.add(postResponse3.get(i));
                        }
                    }

                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                    adapter3 = new My_PlayList_Adapter(postResponse3_2);
                    recyclerView3.setAdapter(adapter3);
                    gok2.setText(Integer.toString(postResponse3.size()));
                    //이거안해주면 리스너안먹힘
                    adapter3.setOnClickListener(Search.this);
                }


            }

            @Override
            public void onFailure(Call<ArrayList<Recent_Main_Item>> call, Throwable t) {

            }
        });

    }
    //검색 클릭했을시
    @Override
    public void onItemClicked1(String setMusic_url) {

        search.setText(setMusic_url);

    }



    @Override
    public void onItemClicked2(String feed_id) {

        Intent intent = new Intent(Search.this, Feed.class);
        intent.putExtra("feed_id",feed_id); /*송신*/
        startActivity(intent);

    }


    @Override
    public void onItemClicked(int position, String setMusic_url) {

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
                        Search.this, // 현재 화면
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
                Search.this, // 현재 화면
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
}