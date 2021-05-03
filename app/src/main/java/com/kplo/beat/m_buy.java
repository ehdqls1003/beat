package com.kplo.beat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kplo.beat.model.request.Cancel;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class m_buy extends AppCompatActivity implements m_buy_Adapter.MyRecyclearViewClickListener {

    private RetrofitAPI retrofitAPI;
    m_buy_Adapter adapter;
    ArrayList<m_buy_item> postResponse;
    String id;
    TextView buy1,buy2;
    BootpayApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_buy);


        new Thread() {
            public void run() {
                api = new BootpayApi("606fb8cb5b2948002e07b081", "9aWZ+uSXjlHOY02sEW6RPcI7gXQReCkbgwaydYK46Zc=");
                goGetToken();
                        /*goVerfity();
                        goCancel();
                        goSubscribeBilling();
                        goRemoteForm();*/
            }
        }.start();
        buy1 = findViewById(R.id.buy1);
        buy2 = findViewById(R.id.buy2);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Call<ArrayList<m_buy_item>> call = retrofitAPI.user_buy_list(id);
        call.enqueue(new Callback<ArrayList<m_buy_item>>() {
            @Override
            public void onResponse(Call<ArrayList<m_buy_item>> call, Response<ArrayList<m_buy_item>> response) {

                postResponse = response.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(m_buy.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new m_buy_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;



                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(m_buy.this);

            }

            @Override
            public void onFailure(Call<ArrayList<m_buy_item>> call, Throwable t) {

            }
        });


        buy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(m_buy.this, m_buy2.class);
                startActivity(intent);
                finish();

            }
        });




    }

    @Override
    protected void onPause() {
        super.onPause();
        //액티비티를 종료할 때 애니메이션 없애기
        overridePendingTransition(0,0);

    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {

    }

    @Override
    public void buyClicked(final int buy_num) {
        //다이얼로그 하나띄우자

        final AlertDialog.Builder builder = new AlertDialog.Builder(m_buy.this);

        builder.setTitle("구매확정").setMessage("구매확정 하시겠습니까?");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {


        Call<Result2> call = retrofitAPI.buy_buy_music(buy_num);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                reflash();
                Toast.makeText(m_buy.this, "구매확정 하셨습니다.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

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

    @Override
    public void cancelClicked(final int buy_num, final String receipt_id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(m_buy.this);

        builder.setTitle("구매취소").setMessage("구매취소 하시겠습니까?");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
        Call<Result2> call = retrofitAPI.delete_buy_music(buy_num);
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                reflash();


                Toast.makeText(m_buy.this, "구매취소 하셨습니다.", Toast.LENGTH_SHORT).show();

                        new Thread() {
                            public void run() {
                                goCancel(receipt_id);
                            }
                        }.start();


            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {

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

    public void reflash(){
        Call<ArrayList<m_buy_item>> call = retrofitAPI.user_buy_list(id);
        call.enqueue(new Callback<ArrayList<m_buy_item>>() {
            @Override
            public void onResponse(Call<ArrayList<m_buy_item>> call, Response<ArrayList<m_buy_item>> response) {

                postResponse = response.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(m_buy.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new m_buy_Adapter(postResponse) ;
                recyclerView.setAdapter(adapter) ;



                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(m_buy.this);

            }

            @Override
            public void onFailure(Call<ArrayList<m_buy_item>> call, Throwable t) {

            }
        });
    }

    public void goCancel(String receipt_id) {

        Log.e("Test2","goCancel");
        Cancel cancel = new Cancel();
        cancel.receipt_id = receipt_id;
        cancel.name = "관리자 홍길동";
        cancel.reason = "택배 지연에 의한 구매자 취소요청";

        try {
            HttpResponse res = api.cancel(cancel);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goGetToken() {
        Log.e("Test2","goGetToken");
        try {
            api.getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}