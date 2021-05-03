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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Edit_list extends AppCompatActivity implements Edit_list_Adapter.MyRecyclearViewClickListener {

    Edit_list_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    ArrayList<User_Music_List> postResponse;
    String id;
    TextView all_list,all_list2,count,count2,submit_b;
    AppCompatButton remove_list,add_my_list;
    ArrayList<Integer> delete_arr;
    ArrayList<Integer> listsun;
    ArrayList<Integer> insert;
    ItemTouchHelper helper;
    int su = 0;
    boolean basketgo;

    //음악재생 변수
    public String url;
    int position = 0;
    MediaPlayer player;
    ImageView p_next,p_play,p_pause,p_before,img,play_list,all_music,no_all_music,no_random_music,random_music,one_music;
    TextView title,name,edit_t,time_e,time_n;
    private Messenger mServiceMessenger = null;
    MyService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용
    ServiceConnection conn;

    String item_position;

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
                        Log.i("test", "act : value1 " + value3);
                    case 9999:
                        String gugu = msg.getData().getString("gugu");
                        Log.i("testgugu", "act : value1 " + gugu);
                        if (gugu !=null){
                            item_position = gugu;
                            adapter.playing(item_position);
                        }
                }
            }
            return false;
        }
    }));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        all_list = findViewById(R.id.all_list);
        all_list2 = findViewById(R.id.all_list2);
        count = findViewById(R.id.count);
        count2 = findViewById(R.id.count2);
        remove_list = findViewById(R.id.remove_list);
        submit_b = findViewById(R.id.submit_b);
        add_my_list = findViewById(R.id.add_my_list);

        Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        item_position = intent.getExtras().getString("item_position"); /*int형*/
        Log.e("item_position",item_position);


        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        all_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.all();
                all_list.setVisibility(View.INVISIBLE);
                all_list2.setVisibility(View.VISIBLE);
            }
        });

        all_list2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.all2();
                su = 0;
                all_list.setVisibility(View.VISIBLE);
                all_list2.setVisibility(View.INVISIBLE);
                count2.setVisibility(View.INVISIBLE);
                count.setVisibility(View.INVISIBLE);
            }
        });

        add_my_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                basket();


            }
        });

        submit_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        remove_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (su > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Edit_list.this);

                    builder.setTitle("삭제").setMessage("선택하신 " + su + "곡을 재생 목록에서 삭제시키겠습니까?");

                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                            public void onClick(DialogInterface dialog, int ids) {

                            for (int i = 0; i < delete_arr.size();i++){
                                Log.e("delete_arr",i+" : "+delete_arr.get(i));
                                for (int j = 0; j < listsun.size();j++) {
                                    if (listsun.get(j).equals(delete_arr.get(i))) {
                                        listsun.remove(j);
                                    }
                                }
                            }
                            for (int j = 0; j < listsun.size();j++) {
                                Log.e("listsun변경후", j + id+" : " + listsun.get(j));
                            }
                            setStringArrayPref(getApplicationContext(),"listsun"+id,listsun);

                            listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);
                            Log.e("이름", "listsun"+id);
                            for (int j = 0; j < listsun.size();j++) {
                                Log.e("listsun변경후 다시가져오기", j + " : " + listsun.get(j));
                            }

                            remove_button();
                            count.setVisibility(View.INVISIBLE);
                            count2.setVisibility(View.INVISIBLE);
                            all_list2.setVisibility(View.INVISIBLE);
                            all_list.setVisibility(View.VISIBLE);
                            delete_arr.clear();
                            adapter.all2();
                            su = 0;

                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    Toast.makeText(getApplicationContext(), "음악을 선택해 주세요", Toast.LENGTH_SHORT).show();
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
                if (postResponse.size() == 0){
                    MyService.data = false;
                }
                Log.e("리사이클러뷰사이즈",""+postResponse.size());

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                if (listsun != null) {

                    for (int j = 0; j < listsun.size(); j++) {
                        Log.e("listsun 리센트 불러오기전", j +id+" : " + listsun.get(j));
                    }
                }
                Log.e("이름2", "listsun"+id);
                listsun = getStringArrayPref(getApplicationContext(),"listsun"+id);
                for (int j = 0; j < listsun.size();j++) {
                    Log.e("listsun 리센트뮤직", j + " : " + listsun.get(j));
                }
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Edit_list.this,LinearLayoutManager.VERTICAL,false)) ;






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

                for (int j = 0; j < postResponse.size();j++) {
                    Log.e("20210429", "recent_music" + " : " + postResponse.get(j));
                }
                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Edit_list_Adapter(postResponse) ;



                //ItemTouchHelper 생성
                helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
                //RecyclerView에 ItemTouchHelper 붙이기
                helper.attachToRecyclerView(recyclerView);

                recyclerView.setAdapter(adapter);

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Edit_list.this);


                adapter.playing(item_position);



            }

            @Override
            public void onFailure(Call<ArrayList<User_Music_List>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
        public void onItemClicked(int position, String setMusic_url,int count) {
            setcount(count);
            su = count;

        }

        @Override
        public void counted(int counts) {
            count.setText(""+counts);
            su = counts;
            count.setVisibility(View.VISIBLE);
            count2.setVisibility(View.VISIBLE);

        }

    @Override
    public void Arraylisted(ArrayList<Integer> delete,ArrayList<Integer> list,ArrayList<Integer> insert) {
        delete_arr = delete;
        listsun = list;
        this.insert = insert;
        Log.e("어레이",""+delete_arr.size());
    }


    public void setcount(int counts){
        count.setText(""+counts);
        su = counts;
        count.setVisibility(View.VISIBLE);
        count2.setVisibility(View.VISIBLE);
    }

    public void remove_button(){
        for (int j = 0; j < postResponse.size();j++) {
            Log.e("20210429", "remove_button" + " : " + postResponse.get(j));
        }
        Call<Result2> call = retrofitAPI.delete_play_list_music_more(delete_arr);

        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                Result2 god;
                god = response.body();
                Log.e("삭제버튼","결과:"+god.toString());

                for (int j = 0; j < listsun.size();j++) {
                    Log.e("listsun리무부", j + " : " + listsun.get(j));
                }
                recent_music();

                Bundle bundle = new Bundle();
                Message msg = Message.obtain(null, MyService.getData4);
                msg.setData(bundle);

                for (int j = 0; j < listsun.size();j++) {
                    Log.e("listsun번드,ㄹ", j + " : " + listsun.get(j));
                }
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

    public void basket(){
        if (insert != null){
            setStringArrayPref(getApplicationContext(),"basket"+id,insert);
        }
        basketgo = true;
        Intent intent = new Intent(Edit_list.this, Basket_add.class);
        startActivity(intent);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        recent_music();

        if (basketgo){
            adapter.all2();
            su = 0;

            all_list.setVisibility(View.VISIBLE);
            all_list2.setVisibility(View.INVISIBLE);
            count2.setVisibility(View.INVISIBLE);
            count.setVisibility(View.INVISIBLE);

            basketgo = false;
        }

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
                        Edit_list.this, // 현재 화면
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
                Edit_list.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);

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
        editor.commit();    //최종 커밋. 커밋을 해야 저장이 된다.
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void setListsun(){

        listsun = getStringArrayPref(Edit_list.this,"listsun"+id);
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
    }

}