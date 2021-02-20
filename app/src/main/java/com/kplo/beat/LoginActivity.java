package com.kplo.beat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;

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
}