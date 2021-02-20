package com.kplo.beat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Server extends AppCompatActivity {

    ObjectOutputStream oos;
    ObjectInputStream ois;
    Socket socket;

    private Handler mHandler = new Handler();
    private EditText ipaddr, portno, message;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        ipaddr = (EditText)findViewById(R.id.ipaddr);
        portno = (EditText)findViewById(R.id.portnumber);
        message = (EditText)findViewById(R.id.sendserv);
    }

    public void OnClick(View v) throws Exception{
        switch(v.getId()){
            case R.id.tryconnect:
                (new Connect()).start();
                break;
        }
    }

    class Connect extends Thread {
        public void run() {
            String ip = ipaddr.getText().toString();
            int port = Integer.parseInt(portno.getText().toString());
            String output = message.getText().toString();

            Meassage p = new Meassage();

            p.setUser_img("img");
            p.setUser_id("id");
            p.setMeassage(output);


            try {
                //서버에 접속합니다.
                socket = new Socket(ip, port);
                //소켓으로부터 OutputStream을 설정합니다.
                //OutputStream을 Object 방식으로 전송하도록 설정합니다.
                oos = new ObjectOutputStream(socket.getOutputStream());
                //Object를 Socket을 통해 값을 전송합니다.
                oos.writeObject(p);

                ois = new ObjectInputStream(socket.getInputStream());
                //Socket로부터 받은 데이터를 Object로 수신합니다.
                Meassage getString = (Meassage)ois.readObject();
                System.out.println("receive : "+getString.getUser_id());


            } catch (Exception e) {
                // TODO Auto-generated catch block
                final String recvInput = "연결에 실패하였습니다.";
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(socket != null) { socket.close();}
            if(oos != null) { oos.close(); }
            if(ois != null) { ois.close(); }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void setToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
