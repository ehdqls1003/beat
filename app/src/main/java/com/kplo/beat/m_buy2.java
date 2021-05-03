package com.kplo.beat;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class m_buy2 extends AppCompatActivity implements m_buy2_Adapter.MyRecyclearViewClickListener {

    private RetrofitAPI retrofitAPI;
    m_buy2_Adapter adapter;
    ArrayList<m_buy_item> postResponse;
    String id;
    TextView buy1;

    // getSystemService(Context.DOWNLOAD_SERVICE);
    DownloadManager downloadManager;
    // long enqueue (DownloadManager.Request request)
    long enqueue = 0;

    BroadcastReceiver mDownComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_buy2);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        buy1 = findViewById(R.id.buy1);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Call<ArrayList<m_buy_item>> call = retrofitAPI.user_buy_list2(id);
        call.enqueue(new Callback<ArrayList<m_buy_item>>() {
            @Override
            public void onResponse(Call<ArrayList<m_buy_item>> call, Response<ArrayList<m_buy_item>> response) {

                postResponse = response.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(m_buy2.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new m_buy2_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(m_buy2.this);

            }

            @Override
            public void onFailure(Call<ArrayList<m_buy_item>> call, Throwable t) {

            }
        });


        buy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(m_buy2.this, m_buy.class);
                startActivity(intent);
                finish();

            }
        });



        //다운로드완료를 알리는 브로드케스트트
        mDownComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "다운로드 완료", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        //액티비티를 종료할 때 애니메이션 없애기
        overridePendingTransition(0,0);
        if (enqueue != 0) {
            unregisterReceiver(mDownComplete);
            enqueue = 0;
        }
    }

    public void downloadmusic(String music_url,String music_title){

        Uri uri = Uri.parse(music_url);
        // Request는 다운로드 할 파일의 위치와 로컬 저장 경로, 사용할 네트워크 종류 등의 정보를 가지는 객체임
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        //다운로드 경로, 파일명을 적어준다
        Log.e("다운로드",""+music_url);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/beats", music_title+".mp3");



                /* 다운로드시 사용할 네트워크 종류, 로밍시의 다운로드 허가 여부, 상태란에 나타날 제목과 설명, 다운로드 위치 등 지정
                Request setAllowedNetworkTypes (int flags)
                Request setAllowedOverRoaming (boolean allowed)
                Request setTitle (CharSequence title)
                Request setDescription (CharSequence description)
                Request setDestinationInExternalPublicDir (String dirType, String subPath)
                Request setDestinationUri (Uri uri)
                 */
        request.setTitle("테스트 다운로드");
        request.setDescription("음악 파일을 다운로드 받습니다.");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        enqueue = downloadManager.enqueue(request);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        // registerRecevier(BroadcastReceiver receiver, IntentFilter filter);
        registerReceiver(mDownComplete, filter);

    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {

    }

    @Override
    public void ondownloadClicked(String music_url,String music_title) {

        downloadmusic(music_url,music_title);

    }
}