package com.kplo.beat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity3 extends AppCompatActivity {

    TextView input_id, textViewResult;
    EditText input_pw;
    String pw, id;
    Button btn_next;

    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        Intent intent = getIntent(); /*데이터 수신*/

        id = intent.getExtras().getString("id"); /*String형*/

        btn_next = findViewById(R.id.btn_next);
        input_id = findViewById(R.id.input_id);
        input_pw = findViewById(R.id.inputpassword);
        textViewResult = (TextView) findViewById(R.id.textViewResult);


        input_id.setText(id);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build();
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPost();
            }
        });

    }

    private void createPost() {

        pw = input_pw.getText().toString();


        // 세 번째 방식(@FieldMap") 에서 사용한 내용
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("pw", pw);

        Call<Post> call = retrofitAPI.createPost(map);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setVisibility(View.INVISIBLE);
                    textViewResult.setText("code: " + response.code());
                    return;
                }

                Post postResponse = response.body();

                Log.i("뭐냐이거", "" + response.body());

                String content = "";
                content += "id: " + postResponse.getId() + "\n";
                content += "pw: " + postResponse.getPw() + "\n";
                textViewResult.setVisibility(View.INVISIBLE);
                textViewResult.setText(content);
                finish();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setVisibility(View.INVISIBLE);
                textViewResult.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity3.this, LoginActivity.class);
                startActivity(intent);
                finish();
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