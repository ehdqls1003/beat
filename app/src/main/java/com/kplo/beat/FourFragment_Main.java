package com.kplo.beat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FourFragment_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  FourFragment_Main extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "feed_id";
    private static final String ARG_PARAM2 = "param2";

    My_flowing_Adapter adapter;
    private RetrofitAPI retrofitAPI;
    private View v;
    private ArrayList<Flow_Item> postResponse;
    private ArrayList<Flow_Item> postResponse2;
    private String feed_id,id;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean go=false;

    public FourFragment_Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SecondFragment_flow.
     */
    // TODO: Rename and change types and number of parameters
    public static FourFragment_Main newInstance(String param1) {
        FourFragment_Main fragment = new FourFragment_Main();
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
            Log.e("세번","onCreate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_second__main, container, false);
        final Context context = getActivity();
        Log.e("세번","onCreateView");



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("세번","onstart");
        /*if (go){
            MainActivity main = (MainActivity)getActivity();
            main.adapter.getPageTitle(2);
            go = false;
        }else{
            go = true;
            Intent intent = new Intent(getActivity(), Show_highlight.class);
            startActivity(intent);
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("세번","onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("세번","onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("세번","onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("세번","onResume");
    }
}