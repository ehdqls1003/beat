package com.kplo.beat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment implements Feed_Adapter.MyRecyclearViewClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "feed_id";
    private static final String ARG_PARAM2 = "param2";
    private RetrofitAPI retrofitAPI;
    private ArrayList<Feed_Item> postResponse;

    private RecyclerView recyclerView;
    private Feed_Adapter adapter;
    private String feed_id;
    private String id;
    private View v;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1) {

        Log.e("피드아이디","이거실행됨?");
        FirstFragment fragment = new FirstFragment();
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
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_first, container, false);

        //불러오기

        final Context context = getActivity();

        /*//저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Log.e("아이디","getfeed"+id);*/


        Log.e("피드아이디",""+feed_id);
        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        Call<ArrayList<Feed_Item>> call = retrofitAPI.getFeed(feed_id);


        call.enqueue(new Callback<ArrayList<Feed_Item>>() {
                @Override
                public void onResponse(Call<ArrayList<Feed_Item>> call, Response<ArrayList<Feed_Item>> response) {
                    if (!response.isSuccessful()) {

                        Log.i("onResponse", "1" + response.body().toString());
                        return;
                    }

                    Log.i("onResponse", "2" + response.body());
                    postResponse = response.body();
                    Collections.reverse(postResponse);

                    Log.e("아이디","이건"+postResponse);
                    Log.e("아이디","들어옴"+id);
                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                    recyclerView = v.findViewById(R.id.recent_music_recycler) ;

                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                    adapter = new Feed_Adapter(postResponse) ;
                    recyclerView.setAdapter(adapter) ;

                    //이거안해주면 리스너안먹힘
                    adapter.setOnClickListener(FirstFragment.this);



                }

                @Override
                public void onFailure(Call<ArrayList<Feed_Item>> call, Throwable t) {

                    Log.i("onResponse", "3" );

                }
        });


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public void onItemClicked(int position, int idx) {

        Log.e("프래그먼트","이거임?"+idx);
        Intent intent = new Intent(getContext(), Feed_Details.class);

        intent.putExtra("position",position); /*송신*/
        intent.putExtra("feed_id",feed_id); /*송신*/

        startActivity(intent);



    }

}