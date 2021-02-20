package com.kplo.beat;

/*
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class NewClient extends AppCompatActivity {    //메인 activity 시작!
    private Handler mHandler;
    Socket socket;
    private String ip = "15.164.220.153"; // IP 주소
    private int port = 3333; // PORT번호
    EditText et;
    TextView msgTV;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    AppCompatButton btn,btnCon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        mHandler = new Handler();

        et = (EditText) findViewById(R.id.EditText01);
        btn = findViewById(R.id.Button01);
        btnCon = findViewById(R.id.button02);
        final TextView tv = (TextView) findViewById(R.id.TextView01);
        msgTV = (TextView)findViewById(R.id.chatTV);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (et.getText().toString() != null || !et.getText().toString().equals("")) {
                    ConnectThread th =new ConnectThread();
                    th.start();
                }
            }
        });
    }

    class ConnectThread extends Thread{
        public void run(){
            try{
                //소켓 생성
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket =  new Socket(serverAddr,port);
                //입력 메시지
                String sndMsg = et.getText().toString();
                Log.d("=============", sndMsg);
                //데이터 전송
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                out.println(sndMsg);
                //데이터 수신
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String read = input.readLine();

                //화면 출력
                mHandler.post(new msgUpdate(read));
                Log.d("=============", read);
                socket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    // 받은 메시지 출력
    class msgUpdate implements Runnable {
        private String msg;
        public msgUpdate(String str) {
            this.msg = str;
        }
        public void run() {
            msgTV.setText(msgTV.getText().toString() + msg + "\n");
        }
    };

}*/


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewClient extends AppCompatActivity {
    private Handler mHandler;
    InetAddress serverAddr;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "15.164.220.153";
    private int port = 3333;

    TextView textView;
    String UserID;
    Button connectbutton;
    Button chatbutton;
    TextView chatView;
    EditText message;
    String sendmsg;
    String read;
    boolean abc;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    private DataInputStream clientIn;
    private DataOutputStream clientOut;
    String room_id,feed_id,feed_img;

    ArrayList<show_message> postResponse;
    ArrayList<MyImg> postResponse2;
    private RetrofitAPI retrofitAPI;
    NewClient_Adapter adapter;
    String my_img;
    RecyclerView recyclerView;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (oos != null){
                oos.close();
            }
            if (ois != null){
                ois.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);
        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.TextView01);
        /*chatView = (TextView) findViewById(R.id.chatTV);*/
        message = (EditText) findViewById(R.id.EditText01);





        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        UserID = sf.getString("id", "");

        textView.setText(UserID);
        chatbutton = (Button) findViewById(R.id.Button01);

        Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        room_id = intent.getExtras().getString("room_id"); /*int형*/
        feed_id = intent.getExtras().getString("feed_id"); /*int형*/
        feed_img = intent.getExtras().getString("feed_img"); /*int형*/
        getmy_img();


        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Meassage p = new Meassage();
                            p.setMeassage(sendmsg);
                            p.setUser_id(UserID);
                            p.setUser_img(my_img);

                            oos.writeObject(p);
                            oos.flush();
                            insert_message();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                message.setText("");
            }
        });




    }

    class msgUpdate implements Runnable {
        private Meassage msg;

        public msgUpdate(Meassage msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            show_message item = new show_message();
            item.setUser_id(msg.getUser_id());
            item.setMessage(msg.getMeassage());

            SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            SimpleDateFormat original_format2 = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
            SimpleDateFormat new_format = new SimpleDateFormat("a KK:mm", Locale.KOREA);
            original_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            original_format2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            new_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            Date time = new Date();
            String time1 = original_format.format(time);
            item.setTime(time1);


            item.setUser_img(msg.User_img);

            postResponse.add(item);
            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            adapter = new NewClient_Adapter(postResponse) ;
            recyclerView.setAdapter(adapter) ;
            recyclerView.scrollToPosition(adapter.getItemCount() -1);
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
                {
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                }
            });
            /*chatView.setText(chatView.getText().toString() + msg2 + " : " + msg1 + "\n");*/
        }
    }

    public void insert_message(){

        Call<Result2> call = retrofitAPI.insert_message(UserID,feed_id,sendmsg, Integer.parseInt(room_id));
        call.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                Log.e("메세지","저장됨");
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {
                Log.e("메세지","실패");

            }
        });

    }

    public void show_message(){

        Call<ArrayList<show_message>> call = retrofitAPI.getMessage(Integer.parseInt(room_id));
        call.enqueue(new Callback<ArrayList<show_message>>() {
            @Override
            public void onResponse(Call<ArrayList<show_message>> call, Response<ArrayList<show_message>> response) {
                postResponse = response.body();
                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                if(postResponse != null){
                    recyclerView = findViewById(R.id.recent_music_recycler) ;
                    recyclerView.setLayoutManager(new LinearLayoutManager(NewClient.this, LinearLayoutManager.VERTICAL,false)) ;

                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                    adapter = new NewClient_Adapter(postResponse) ;
                    recyclerView.setAdapter(adapter) ;
                    recyclerView.scrollToPosition(adapter.getItemCount() -1);
                    recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
                        {
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                        }
                    });
                }
                /*
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(NewClient.this);*/

            }

            @Override
            public void onFailure(Call<ArrayList<show_message>> call, Throwable t) {

            }
        });

    }

    public void getmy_img(){
        Call<ArrayList<MyImg>> call = retrofitAPI.getMyImg(UserID);
        call.enqueue(new Callback<ArrayList<MyImg>>() {
            @Override
            public void onResponse(Call<ArrayList<MyImg>> call, Response<ArrayList<MyImg>> response) {
                postResponse2 = response.body();
                Log.e("내이미지",postResponse2.get(0).getMy_img_url());
                my_img = postResponse2.get(0).getMy_img_url();
            }

            @Override
            public void onFailure(Call<ArrayList<MyImg>> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread() {
            public void run() {
                //데이터 수신
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);


                    OutputStream os = socket.getOutputStream();
                    oos = new ObjectOutputStream(os);
                    Log.e("통신", "receive : ");
                    InputStream in = socket.getInputStream();
                    ois = new ObjectInputStream(in);
                    Log.e("통신2", "receive : ");

                    clientOut = new DataOutputStream(socket.getOutputStream());
                    clientIn = new DataInputStream(socket.getInputStream());

                    //접속하자마자 닉네임 전송하면. 서버가 이걸 닉네임으로 인식을 하고서 맵에 집어넣겠지요?
                    clientOut.writeUTF(room_id);


                    Meassage getString;
                    while (true) {
                        getString = (Meassage) ois.readObject();
                        if (getString != null) {
                            mHandler.post(new msgUpdate(getString));
                        }
                    }

                } catch (EOFException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        show_message();
    }


}