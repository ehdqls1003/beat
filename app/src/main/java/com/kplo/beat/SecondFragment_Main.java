package com.kplo.beat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment_Main extends Fragment implements allFeed_Details_Adapter.MyRecyclearViewClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    allFeed_Details_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    private View v;
    private ArrayList<Feed_Item> postResponse;
    private ArrayList<Feed_like_Item> postResponse2;
    RecyclerView recyclerView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayoutManager mLayoutManager;
    Parcelable state;
    int mScrollPosition ;

    public SecondFragment_Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment_Main.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment_Main newInstance(String param1, String param2) {
        SecondFragment_Main fragment = new SecondFragment_Main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("프레그먼트상태","onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("프레그먼트상태","onCreateView");


        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_second__main, container, false);


        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);


        return v;
    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {


    }

    @Override
    public void onu_nameClicked(int position, String u_name) {

        Intent intent = new Intent(getActivity(), Feed.class);
        intent.putExtra("feed_id",u_name); /*송신*/
        startActivity(intent);

    }

    @Override
    public void onheart_outlineClicked(int position, String id, String idx) {

        Call<Result2> call = retrofitAPI.insert_Story_like(idx,id);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
            }
            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

            }
        });


    }

    @Override
    public void onCommentClicked(int idx) {
        Log.e("프래그먼트","이거임?"+idx);
        Intent intent = new Intent(getContext(), Feed_more.class);

        intent.putExtra("idx",idx); /*송신*/

        startActivity(intent);
    }

    @Override
    public void onheartClicked(int position, String id, String idx) {

        Call<Result2> call = retrofitAPI.delete_Story_like(idx,id);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {

                Log.e("좋아요","좋아요해제");
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("프레그먼트상태","onStart");
        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Call<ArrayList<Feed_Item>> call = retrofitAPI.getall_Feed_details();

        call.enqueue(new Callback<ArrayList<Feed_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_Item>> call, Response<ArrayList<Feed_Item>> response) {
                if (!response.isSuccessful()) {

                    return;
                }



                Log.i("onResponse", "2" + response.body());
                postResponse = response.body();
                Collections.reverse(postResponse);

                Call<ArrayList<Feed_like_Item>> call2 = retrofitAPI.getall_Feed_like();

                call2.enqueue(new Callback<ArrayList<Feed_like_Item>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Feed_like_Item>> call, Response<ArrayList<Feed_like_Item>> response) {


                        postResponse2 = response.body();
                        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                        recyclerView = v.findViewById(R.id.recent_music_recycler) ;
                        recyclerView.setLayoutManager(mLayoutManager) ;

                        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                        adapter = new allFeed_Details_Adapter(postResponse,postResponse2) ;
                        recyclerView.setAdapter(adapter);
                        if (mScrollPosition != 0){
                            mLayoutManager.scrollToPosition(mScrollPosition);
                        }




                        recyclerView.addItemDecoration(new RecyclerViewDecoration(20));
                        //이거안해주면 리스너안먹힘
                        adapter.setOnClickListener(SecondFragment_Main.this);

                    }

                    @Override
                    public void onFailure(Call<ArrayList<Feed_like_Item>> call, Throwable t) {

                    }
                });



            }

            @Override
            public void onFailure(Call<ArrayList<Feed_Item>> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("프레그먼트상태","onResume");
        if (state != null){
            mLayoutManager.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScrollPosition = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        Log.e("프레그먼트상태","onPause");
    }



}