package com.kplo.beat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment_Main extends Fragment implements ranklist_Adapter.MyRecyclearViewClickListener {

    Recent_main_Adapter adapter;
    ranklist_Adapter adapter2;

    private RetrofitAPI retrofitAPI;
    ImageView back_icon;
    TextView recent_music;
    ArrayList<Recent_Main_Item> postResponse;
    ArrayList<Recent_Main_Item> postResponse2;
    ArrayList<Recent_Main_Item> postResponse2_2;
    private RecyclerView recyclerView,recyclerView2;

    private String id;
    private View v;
    ServiceConnection conn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Messenger> MSG = new ArrayList<>();
    private Messenger mServiceMessenger;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    boolean isService = false; // 서비스 중인 확인용
    //서버에서데이터받기
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(!getActivity().isFinishing()) {
                switch (msg.what) {
                    case MyService.MSG_SEND_TO_ACTIVITY:
                        Log.e("뭐냐이건", "배달받음1");
                        String value1 = msg.getData().getString("test1");
                        Log.i("test", "act : value1 " + value1);
                        String value2 = msg.getData().getString("test2");
                        Log.i("test", "act : value1 " + value2);
                        String value3 = msg.getData().getString("test3");
                        Log.i("test", "act : value1 " + value3);
                        ((MainActivity)getActivity()).set_title(value2,value1);




                }
            }
            return false;
        }
    }));


    public FirstFragment_Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FirstFragment_Main.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment_Main newInstance(String param1, Messenger msg) {
        FirstFragment_Main fragment = new FirstFragment_Main();
        ArrayList<Messenger> Messenger = new ArrayList<>();
        Messenger.add(msg);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelableArrayList("Messenger",Messenger);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            MSG = getArguments().getParcelableArrayList("Messenger");
            mServiceMessenger = MSG.get(0);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_first__main, container, false);
        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        TextView recent_music = v.findViewById(R.id.recent_music);
        recent_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPlayList.class);
                startActivity(intent);
            }
        });

        TextView silsigan_txt = v.findViewById(R.id.silsigan_txt);
        silsigan_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), hitlist_more.class);
                startActivity(intent);
            }
        });


        final Context context = getActivity();
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
                        getActivity(), // 현재 화면
                        MyService.class); // 다음넘어갈 컴퍼넌트
                context.startService(intent);

                // 서비스쪽 객체를 전달받을수 있슴
                isService = true;



            }

            public void onServiceDisconnected(ComponentName name) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                isService = false;

            }
        };

        Intent intent = new Intent(
                getActivity(), // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        context.bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);





        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

            Call<ArrayList<Recent_Main_Item>> call = retrofitAPI.recent_music();
            call.enqueue(new Callback<ArrayList<Recent_Main_Item>>() {
                @Override
                public void onResponse(Call<ArrayList<Recent_Main_Item>> call, Response<ArrayList<Recent_Main_Item>> response) {
                    if (!response.isSuccessful()) {

                        return;
                    }

                    postResponse = response.body();

                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                    recyclerView = v.findViewById(R.id.recent_music_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                    adapter = new Recent_main_Adapter(postResponse);
                    recyclerView.setAdapter(adapter);


                }

                @Override
                public void onFailure(Call<ArrayList<Recent_Main_Item>> call, Throwable t) {

                    Log.i("onResponse", "3");

                }
            });

        Call<ArrayList<Recent_Main_Item>> call2 = retrofitAPI.recent_music2();
        call2.enqueue(new Callback<ArrayList<Recent_Main_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Recent_Main_Item>> call, Response<ArrayList<Recent_Main_Item>> response) {
                postResponse2 = response.body();

                postResponse2_2 = new ArrayList<Recent_Main_Item>();
                if (postResponse2.size() < 5){
                    for (int i = 0; i < postResponse2.size(); i++) {
                        if (postResponse2.get(i).getCount() != 0) {
                            postResponse2_2.add(postResponse2.get(i));
                        }
                    }
                }else {
                    for (int i = 0; i < 5; i++) {
                        if (postResponse2.get(i).getCount() != 0) {
                            postResponse2_2.add(postResponse2.get(i));
                        }
                    }
                }

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                recyclerView2 = v.findViewById(R.id.rank_music);
                recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter2 = new ranklist_Adapter(postResponse2_2);
                recyclerView2.setAdapter(adapter2);
                //이거안해주면 리스너안먹힘
                adapter2.setOnClickListener(FirstFragment_Main.this);


            }

            @Override
            public void onFailure(Call<ArrayList<Recent_Main_Item>> call, Throwable t) {

            }
        });




        return v;
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

                Toast.makeText(getContext(), "1곡을 재생 목록에 담았습니다.", Toast.LENGTH_SHORT).show();
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