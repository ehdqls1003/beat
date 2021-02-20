package com.kplo.beat;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {


    private static OkHttpClient client =
            new OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor())
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();


    private static String BASE_URL = "http://15.164.220.153/";
    static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static HttpLoggingInterceptor httpLoggingInterceptor(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.e("MyGitHubData :", message + "");
            }
        });


        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}
