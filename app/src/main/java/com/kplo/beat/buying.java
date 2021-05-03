package com.kplo.beat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kplo.beat.model.request.Cancel;
import com.kplo.beat.model.request.RemoteForm;
import com.kplo.beat.model.request.SubscribeBilling;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class buying extends AppCompatActivity {


    private RetrofitAPI retrofitAPI;

    String title,music_img_url;
    TextView m_title;
    ImageView img;
    Button button,button2;

    String receipt_id;
    int music_idx;
    String id;

    static BootpayApi api;

    private String application_id = "606fb8cb5b2948002e07b07f";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buying);

        m_title = findViewById(R.id.m_title);
        img = findViewById(R.id.img);
        button = findViewById(R.id.button);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        final Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        title = intent.getExtras().getString("m_title"); /*int형*/
        music_img_url = intent.getExtras().getString("music_img_url");
        music_idx = Integer.parseInt(intent.getExtras().getString("music_idx"));


        m_title.setText(title);
        //좋아요 여부와 숫자 한번에 가져오기
        Glide.with(buying.this)
                .load(music_img_url)
                .centerCrop()
                .into(img);


        BootpayAnalytics.init(this, application_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                /*new Thread() {
                    public void run() {

                        BootpayApi api = new BootpayApi(
                                "606fb8cb5b2948002e07b081",
                                "9aWZ+uSXjlHOY02sEW6RPcI7gXQReCkbgwaydYK46Zc="
                        );
                        try {
                            api.getAccessToken();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e("토큰","쓰레드 시작");

                    }
                }.start();*/


                BootUser bootUser = new BootUser().setPhone("010-6270-6082"); //자기 전화번호
                BootExtra bootExtra = new BootExtra().setQuotas(new int[]{0, 2, 3});


                Bootpay.init(getFragmentManager())
                        .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id값
                        .setPG(PG.KCP) // 결제할 PG 사
                        .setMethod(Method.KAKAO) // 결제수단
                        .setContext(buying.this)
                        .setBootUser(bootUser)
                        .setBootExtra(bootExtra)
                        .setUX(UX.PG_DIALOG)
//                      .setUserPhone("010-1234-5678") // 구매자 전화번호
                        .setName(title) // 결제할 상품명
                        .setOrderId("1234") // 결제 고유번호expire_month
                        .setPrice(100) // 결제할 금액

                        //개발자 메뉴얼에 가면 더 추가할 수 있음 .

                        .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                        .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                        .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                            @Override
                            public void onConfirm(@Nullable String message) {

                                if (0 < 10) Bootpay.confirm(message); // 재고가 있을 경우.
                                else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                                Log.d("confirm", message);
                            }
                        })
                        .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                            @Override
                            public void onDone(@Nullable String message) {
                                Log.d("done", message);
                                try {
                                    JSONObject jObject = new JSONObject(message);
                                    receipt_id = jObject.getString("receipt_id");
                                    Log.e("done : ",receipt_id);

                                    Call<Result2> call = retrofitAPI.insert_buy_music(music_idx,receipt_id,id);
                                    call.enqueue(new Callback<Result2>() {
                                        @Override
                                        public void onResponse(Call<Result2> call, Response<Result2> response) {
                                            finish();
                                            Toast.makeText(buying.this, "구매를 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<Result2> call, Throwable t) {

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                            @Override
                            public void onReady(@Nullable String message) {
                                Log.d("ready", message);
                            }
                        })
                        .onCancel(new CancelListener() { // 결제 취소시 호출
                            @Override
                            public void onCancel(@Nullable String message) {

                                Log.d("cancel", message);
                            }
                        })
                        .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                            @Override
                            public void onError(@Nullable String message) {
                                Log.d("error", message);
                            }
                        })
                        .onClose(
                                new CloseListener() { //결제창이 닫힐때 실행되는 부분
                                    @Override
                                    public void onClose(String message) {
                                        Log.d("close", "close");
                                    }
                                })
                        .request();

            }
        });


    }



    public void goGetToken() {
        Log.e("Test2","goGetToken");
        try {
            api.getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goVerfity() {
        Log.e("Test2","goVerfity");
        try {
            HttpResponse res = api.verify(receipt_id);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goCancel() {
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

    public void goSubscribeBilling() {
        Log.e("Test2","goSubscribeBilling");
        SubscribeBilling subscribeBilling = new SubscribeBilling();
        subscribeBilling.billing_key = "593f8febe13f332431a8ddae";
        subscribeBilling.item_name = "정기결제 테스트 아이템";
        subscribeBilling.price = 3000;
        subscribeBilling.order_id = "" + (System.currentTimeMillis() / 1000);


        try {
            HttpResponse res = api.subscribe_billing(subscribeBilling);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            System.out.println(str);
            System.out.println(new Gson().toJson(subscribeBilling));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goRemoteForm() {
        Log.e("Test2","goRemoteForm");

        RemoteForm form = new RemoteForm();
        form.pg = "kakao";
        form.fm = Arrays.asList("card", "phone");
        form.n = "테스트 결제";
        form.o_key = "unique_value_1234"; // 가맹점의 상품 고유 키
        form.is_r_n = false; // 구매자가 상품명 입력 허용할지 말지
        form.is_r_p = false; // 구매자가 가격 입력 허용할지 말지
        form.is_addr = false; // 주소창 추가 할지 말지
        form.is_da = false; // 배송비 추가 할지 말지
        form.is_memo = false;  // 구매자로부터 메모를 받을 지
        form.tfp = 0d; // 비과세 금액
        form.ip = 10000d; // 아이템 판매금액
        form.dp = 0d; // 디스플레이용 가격, 할인전 가격을 의미함, 쿠폰이나 프로모션에 의한 가격 디스카운트 개념 필요 - 페이코 때문에 생긴 개념
        form.dap = 0d;  // 기본배송비
        form.dap_jj = 0d; // 제주 배송비
        form.dap_njj = 0d; // 제주 외 지역 도서산간 추가비용


        try {
            HttpResponse res = api.remote_form(form);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}