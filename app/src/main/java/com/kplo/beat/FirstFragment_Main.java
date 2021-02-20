package com.kplo.beat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment_Main extends Fragment {

    Recent_main_Adapter adapter;

    private RetrofitAPI retrofitAPI;
    ImageView back_icon;
    TextView recent_music;
    ArrayList<Recent_Main_Item> postResponse;

    private RecyclerView recyclerView;

    private String id;
    private View v;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment_Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment_Main.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment_Main newInstance(String param1, String param2) {
        FirstFragment_Main fragment = new FirstFragment_Main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

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


            Call<ArrayList<Recent_Main_Item>> call = retrofitAPI.recent_music();
            final Context context = getActivity();
            call.enqueue(new Callback<ArrayList<Recent_Main_Item>>() {
                @Override
                public void onResponse(Call<ArrayList<Recent_Main_Item>> call, Response<ArrayList<Recent_Main_Item>> response) {
                    if (!response.isSuccessful()) {

                        return;
                    }

                    postResponse = response.body();

                    // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                    RecyclerView recyclerView = v.findViewById(R.id.recent_music_recycler);
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




        return v;
    }
}