package com.kplo.beat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Use the {@link FirstFragment_flow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment_flow extends Fragment implements My_flow_Adapter.MyRecyclearViewClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "feed_id";
    private static final String ARG_PARAM2 = "param2";

    My_flow_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    private View v;
    private ArrayList<Flow_Item> postResponse;
    private ArrayList<Flow_Item> postResponse2;
    private String feed_id,id;
    RecyclerView recyclerView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment_flow() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FirstFragment_flow.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment_flow newInstance(String param1) {
        FirstFragment_flow fragment = new FirstFragment_flow();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feed_id = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_first_flow, container, false);
        final Context context = getActivity();


        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");


        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Call<ArrayList<Flow_Item>> call = retrofitAPI.getMy_flow(feed_id);

        call.enqueue(new Callback<ArrayList<Flow_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Flow_Item>> call, Response<ArrayList<Flow_Item>> response) {
                if (!response.isSuccessful()) {

                    return;
                }

                postResponse = response.body();

                Call<ArrayList<Flow_Item>> call2 = retrofitAPI.getall_flow(id);
                Log.e("호","프래그먼트아이디"+id);

                call2.enqueue(new Callback<ArrayList<Flow_Item>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Flow_Item>> call2, Response<ArrayList<Flow_Item>> response2) {
                        if (!response2.isSuccessful()) {

                            return;
                        }

                        Log.i("onResponse", "2" + response2.body());
                        postResponse2 = response2.body();

                        for (int i = 0; i < postResponse2.size(); i++){
                            Log.e("호","가져온데이터"+postResponse2.get(i).getMy_id());
                            Log.e("호","가져온데이터"+postResponse2.get(i).getFriend_id());
                        }

                        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                        recyclerView = v.findViewById(R.id.recent_music_recycler) ;
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false)) ;

                        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                        adapter = new My_flow_Adapter(postResponse,postResponse2) ;
                        recyclerView.setAdapter(adapter) ;

                        //이거안해주면 리스너안먹힘
                        adapter.setOnClickListener(FirstFragment_flow.this);


                    }

                    @Override
                    public void onFailure(Call<ArrayList<Flow_Item>> call2, Throwable t2) {

                        Log.i("onResponse", "3" );

                    }
                });


            }

            @Override
            public void onFailure(Call<ArrayList<Flow_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("호", "프레그먼트 재시작");
    }

    @Override
    public void onItemClicked(String feed_id) {

        Intent intent = new Intent(getContext(), Feed.class);
        intent.putExtra("feed_id",feed_id); /*송신*/
        startActivity(intent);
    }

    @Override
    public void onfollowClicked(int position, String setU_name) {

            Call<Result2> call = retrofitAPI.insert_flow(id,setU_name);

            call.enqueue(new Callback<Result2>() {
                @Override
                public void onResponse(Call<Result2> call, Response<Result2> response) {
                    if (!response.isSuccessful()) {

                        Log.i("onResponse", "1" + response.body().toString());
                        return;
                    }

                    Log.i("onResponse", "2" + response.body());

                    Log.e("팔로우성공",postResponse.toString());
                    Log.i("친구", "친구등록" );

                    Call<ArrayList<Flow_Item>> call3 = retrofitAPI.getMy_flow(feed_id);

                    call3.enqueue(new Callback<ArrayList<Flow_Item>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Flow_Item>> call3, Response<ArrayList<Flow_Item>> response3) {
                            if (!response3.isSuccessful()) {

                                return;
                            }

                            postResponse = response3.body();

                            Call<ArrayList<Flow_Item>> call2 = retrofitAPI.getall_flow(id);
                            Log.e("호","프래그먼트아이디"+id);

                            call2.enqueue(new Callback<ArrayList<Flow_Item>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Flow_Item>> call2, Response<ArrayList<Flow_Item>> response2) {
                                    if (!response2.isSuccessful()) {

                                        return;
                                    }

                                    Log.i("onResponse", "2" + response2.body());
                                    postResponse2 = response2.body();

                                    for (int i = 0; i < postResponse2.size(); i++){
                                        Log.e("호","가져온데이터"+postResponse2.get(i).getMy_id());
                                        Log.e("호","가져온데이터"+postResponse2.get(i).getFriend_id());
                                    }

                                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                                    RecyclerView recyclerView = v.findViewById(R.id.recent_music_recycler) ;
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false)) ;

                                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                                    adapter = new My_flow_Adapter(postResponse,postResponse2) ;
                                    recyclerView.setAdapter(adapter) ;

                                    //이거안해주면 리스너안먹힘
                                    adapter.setOnClickListener(FirstFragment_flow.this);
                                    refresh();

                                }

                                @Override
                                public void onFailure(Call<ArrayList<Flow_Item>> call2, Throwable t2) {

                                    Log.i("onResponse", "3" );

                                }
                            });


                        }

                        @Override
                        public void onFailure(Call<ArrayList<Flow_Item>> call3, Throwable t3) {

                            Log.i("onResponse", "3" );

                        }
                    });


                }

                @Override
                public void onFailure(Call<Result2> call, Throwable t) {

                    Log.i("onResponse", "3" );


                }
            });





    }

    @Override
    public void onfollowingClicked(int position, String setU_name) {

            Call<Result2> call = retrofitAPI.delete_flow(id,setU_name);

            call.enqueue(new Callback<Result2>() {
                @Override
                public void onResponse(Call<Result2> call, Response<Result2> response) {
                    if (!response.isSuccessful()) {

                        Log.i("onResponse", "1" + response.body().toString());
                        return;
                    }
                    Log.i("친구", "친구삭제" );

                    Log.i("onResponse", "2" + response.body());
                    Call<ArrayList<Flow_Item>> call3 = retrofitAPI.getMy_flow(feed_id);

                    call3.enqueue(new Callback<ArrayList<Flow_Item>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Flow_Item>> call3, Response<ArrayList<Flow_Item>> response3) {
                            if (!response3.isSuccessful()) {

                                return;
                            }

                            postResponse = response3.body();

                            Call<ArrayList<Flow_Item>> call2 = retrofitAPI.getall_flow(id);
                            Log.e("호","프래그먼트아이디"+id);

                            call2.enqueue(new Callback<ArrayList<Flow_Item>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Flow_Item>> call2, Response<ArrayList<Flow_Item>> response2) {
                                    if (!response2.isSuccessful()) {

                                        return;
                                    }

                                    Log.i("onResponse", "2" + response2.body());
                                    postResponse2 = response2.body();

                                    for (int i = 0; i < postResponse2.size(); i++){
                                        Log.e("호","가져온데이터"+postResponse2.get(i).getMy_id());
                                        Log.e("호","가져온데이터"+postResponse2.get(i).getFriend_id());
                                    }

                                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                                    RecyclerView recyclerView = v.findViewById(R.id.recent_music_recycler) ;
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false)) ;

                                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                                    adapter = new My_flow_Adapter(postResponse,postResponse2) ;
                                    recyclerView.setAdapter(adapter);

                                    //이거안해주면 리스너안먹힘
                                    adapter.setOnClickListener(FirstFragment_flow.this);

                                    refresh();


                                }

                                @Override
                                public void onFailure(Call<ArrayList<Flow_Item>> call2, Throwable t2) {

                                    Log.i("onResponse", "3" );

                                }
                            });


                        }

                        @Override
                        public void onFailure(Call<ArrayList<Flow_Item>> call3, Throwable t3) {

                            Log.i("onResponse", "3" );

                        }
                    });


                }




                @Override
                public void onFailure(Call<Result2> call, Throwable t) {

                    Log.i("onResponse", "3" );


                }
            });


    }

    public void refresh(){
        ((flow_tab)getActivity()).refrash();
    }




}