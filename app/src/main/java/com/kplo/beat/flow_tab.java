package com.kplo.beat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class flow_tab extends AppCompatActivity {

    String id,feed_id;

    private RetrofitAPI retrofitAPI;
    public ViewpagerAdapter_flow adapter2;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_tab);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        feed_id = intent.getExtras().getString("feed_id"); /*int형*/

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //프레그먼트
        viewPager = findViewById(R.id.viewpager);
        adapter2 = new ViewpagerAdapter_flow(getSupportFragmentManager(),feed_id);
        viewPager.setAdapter(adapter2);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void refrash(){
        viewPager.setAdapter(adapter2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void refrash2(){
        viewPager.setAdapter(adapter2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(1).select();

    }


}