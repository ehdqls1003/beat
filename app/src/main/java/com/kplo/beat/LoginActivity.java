package com.kplo.beat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {


    TextView findid,findpw;
    TextView textViewResult;
    AppCompatButton loginbutton,signup;
    EditText inputid,inputpw;
    String id,pw;

    private Messenger mServiceMessenger = null;
    MyService ms; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용
    ServiceConnection conn;
    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;
    //서버에서데이터받기
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("test","act : what "+msg.what);
            Log.e("피니시",""+isFinishing());
            if(!isFinishing()) {
                switch (msg.what) {
                    case MyService.MSG_SEND_TO_ACTIVITY:
                        Log.e("뭐냐이건", "배달받음123123");
                        String value1 = msg.getData().getString("test1");
                        Log.i("test", "act : value1 " + value1);
                        String value2 = msg.getData().getString("test2");
                        Log.i("test", "act : value1 " + value2);
                        String value3 = msg.getData().getString("test3");
                        Log.i("test", "act : value1 " + value3);
                        String value6 = msg.getData().getString("test6");
                }
            }
            return false;
        }
    }));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbutton = findViewById(R.id.loginbutton);
        signup = findViewById(R.id.signup);
        inputid = findViewById(R.id.inputid);
        inputpw = findViewById(R.id.inputpw);
        textViewResult = findViewById(R.id.textViewResult);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build();
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity1.class);
                startActivity(intent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginPost();

            }
        });




    }

    private void loginPost() {

        id = inputid.getText().toString();
        pw = inputpw.getText().toString();

        Log.i("데이터전송", "" + id);


        Call<Post> call = retrofitAPI.loginPost(id,pw);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body().toString());
                Post postResponse = response.body();

                String content = "";
                content += "id: " + postResponse.getId() + "\n";
                content += "pw: " + postResponse.getPw() + "\n";
                Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                // Activity가 종료되기 전에 저장한다.
                //SharedPreferences를 sFile이름, 기본모드로 설정
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String text = inputid.getText().toString(); // 사용자가 입력한 저장할 데이터
                editor.putString("id",text); // key, value를 이용하여 저장하는 형태
                //최종 커밋
                editor.commit();



                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                Log.i("onResponse", "3" );
                Toast.makeText(getApplicationContext(), "회원정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private HttpLoggingInterceptor httpLoggingInterceptor(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.e("MyGitHubData :", message + "");
            }
        });

        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {

                mServiceMessenger = new Messenger(service);
                try {
                    Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);


                } catch (RemoteException e) {
                }
                Intent intent = new Intent(
                        LoginActivity.this, // 현재 화면
                        MyService.class); // 다음넘어갈 컴퍼넌트
                startService(intent);

                // 서비스쪽 객체를 전달받을수 있슴
                isService = true;

                Log.e("마이서비스", "conn" + isService);


            }

            public void onServiceDisconnected(ComponentName name) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                isService = false;

            }
        };

        Intent intent = new Intent(
                LoginActivity.this, // 현재 화면
                MyService.class); // 다음넘어갈 컴퍼넌트
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);

    }

}