package com.kplo.beat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  SecondFragment extends Fragment implements My_Music_Adapter.MyRecyclearViewClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "feed_id";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RetrofitAPI retrofitAPI;
    private ArrayList<Recent_Main_Item> postResponse;
    private ArrayList<Music_highlight> postResponse2;

    private RecyclerView recyclerView;
    private My_Music_Adapter adapter;

    private String feed_id;
    private String id_f;
    private View v;

    private ArrayList<Messenger> MSG = new ArrayList<>();
    private Messenger mServiceMessenger;

    int music_h_num;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, Messenger msg) {
        SecondFragment fragment = new SecondFragment();
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
        Log.e("20210329","MSG.get(0) 들어옴?" + getArguments());
        if (getArguments() != null) {
            feed_id = getArguments().getString(ARG_PARAM1);
            MSG = getArguments().getParcelableArrayList("Messenger");
            mServiceMessenger = MSG.get(0);

            Log.e("20210329","MSG.get(0) 들어옴?" + MSG.size());
            Log.e("20210329","MSG.get(0) 들어옴?" + MSG.get(0));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_first, container, false);



        Context context = getActivity();

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id_f = sf.getString("id","");




        Log.e("피드아이디",""+feed_id);
        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Call<ArrayList<Music_highlight>>call2 = retrofitAPI.all_hightlight();
        call2.enqueue(new Callback<ArrayList<Music_highlight>>() {
            @Override
            public void onResponse(Call<ArrayList<Music_highlight>> call, Response<ArrayList<Music_highlight>> response) {
                Log.e("하이라이트 가져오기","완료");

                postResponse2 = response.body();

            }

            @Override
            public void onFailure(Call<ArrayList<Music_highlight>> call, Throwable t) {

            }
        });

        Call<ArrayList<Recent_Main_Item>> call = retrofitAPI.getmymusic(feed_id);

        call.enqueue(new Callback<ArrayList<Recent_Main_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Recent_Main_Item>> call, Response<ArrayList<Recent_Main_Item>> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                Collections.reverse(postResponse);

                Log.e("아이디","이건"+postResponse);

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = v.findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new My_Music_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(SecondFragment.this);



            }

            @Override
            public void onFailure(Call<ArrayList<Recent_Main_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });

        return v;
    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {
        Log.e("클릭","클릭함"+position);
    }

    @Override
    public void onm_PlayClicked(int position, int idx) {
        Call<Result2> call = retrofitAPI.insert_user_play_list(idx,id_f);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {

                Toast.makeText(getContext(), "1곡을 재생 목록에 담았습니다.", Toast.LENGTH_SHORT).show();
                Log.e("20210329","mServiceMessenger 들어옴?" + mServiceMessenger);
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

    @Override
    public void onMore_button(final int idx, final String title, final String music_url, final String music_img_url) {



            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            boolean yes = false;
            for (int i = 0; i < postResponse2.size(); i++){
                if(postResponse2.get(i).getMusic_idx() == idx){
                    yes = true;
                    music_h_num = postResponse2.get(i).getMusic_h_num();
                }
            }
            if (!yes) {
                builder.setItems(R.array.f_song_u, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        String[] items = getResources().getStringArray(R.array.f_song_u);
                        if (items[pos].equals("하이라이트 등록")) {

                                Intent intent = new Intent(getContext(), Add_highlight.class);
                                intent.putExtra("music_idx", idx);
                                intent.putExtra("title", title);
                                intent.putExtra("music_url", music_url);
                                intent.putExtra("music_img_url", music_img_url);
                                intent.putExtra("id_i", id_f);
                                intent.putExtra("su",false);
                                startActivity(intent);

                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                builder.setItems(R.array.f_song_u2, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        String[] items = getResources().getStringArray(R.array.f_song_u2);
                        if (items[pos].equals("하이라이트 수정")) {

                            Intent intent = new Intent(getContext(), Add_highlight.class);
                            intent.putExtra("music_idx", idx);
                            intent.putExtra("title", title);
                            intent.putExtra("music_url", music_url);
                            intent.putExtra("music_img_url", music_img_url);
                            intent.putExtra("id_i", id_f);
                            intent.putExtra("su",true);
                            startActivity(intent);

                        }else if(items[pos].equals("하이라이트 삭제")){


                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("삭제").setMessage("하이라이트를 삭제 하시겠습니까?");

                            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {



                                Call<Result2> call2 = retrofitAPI.delete_highlight(music_h_num);
                                call2.enqueue(new Callback<Result2>() {
                                    @Override
                                    public void onResponse(Call<Result2> call2, Response<Result2> response) {
                                        for (int i = 0; i < postResponse2.size(); i++){
                                            if(postResponse2.get(i).getMusic_h_num() == music_h_num){
                                                postResponse2.remove(i);
                                            }
                                        }
                                        Toast.makeText(getContext(), "하이라이트가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onFailure(Call<Result2> call2, Throwable t) {

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

        }


}