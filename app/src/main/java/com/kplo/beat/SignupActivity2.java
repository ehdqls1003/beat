package com.kplo.beat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SignupActivity2 extends AppCompatActivity {

    private Button email_in,btn_code,btn_next;
    private EditText email_ad,code_in;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        email_ad = findViewById(R.id.email_ad); //받는 사람의 이메일
        code_in = findViewById(R.id.code_in);
        btn_code = findViewById(R.id.btn_code);
        btn_next = findViewById(R.id.btn_next);


        email_in = (Button) findViewById(R.id.email_in);

        email_in.setVisibility(View.INVISIBLE);/*
        email_ad.setFocusable(false);
        email_ad.setClickable(false);*/
        code_in.setVisibility(View.VISIBLE);
        btn_code.setVisibility(View.VISIBLE);
        btn_code.setVisibility(View.INVISIBLE);
        btn_next.setClickable(true);
        btn_next.setBackgroundColor(Color.BLACK);
        btn_next.setEnabled(true);
        email_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GMailSender gMailSender = new GMailSender("ehdqls1003@gmail.com", "ehdqls12");
                    //코드생성
                    code = gMailSender.getEmailCode();
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("제목입니다", "인증번호 : "+code ,email_ad.getText().toString());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                email_in.setVisibility(View.INVISIBLE);
                email_ad.setFocusable(false);
                email_ad.setClickable(false);
                code_in.setVisibility(View.VISIBLE);
                btn_code.setVisibility(View.VISIBLE);

            }
        });

        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (code.equals(code_in.getText().toString())){
                    Toast.makeText(getApplicationContext(), "인증완료.", Toast.LENGTH_SHORT).show();
                    btn_code.setVisibility(View.INVISIBLE);
                    btn_next.setClickable(true);
                    btn_next.setBackgroundColor(Color.BLACK);
                    btn_next.setEnabled(true);
                }else{
                    Toast.makeText(getApplicationContext(), "인증실패.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignupActivity2.this, SignupActivity3.class);
                String id = email_ad.getText().toString();
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });



    }
}