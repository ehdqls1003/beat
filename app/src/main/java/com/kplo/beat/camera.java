package com.kplo.beat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Result;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class camera extends AppCompatActivity {

    private static final int MY_PERMISSION_STORAGE = 1111;
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    Button btn_capture, btn_album,agree;
    ImageView iv_view,img_add;

    String mCurrentPhotoPath;

    TextView tv_message;
    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;
    Uri imageUri;
    Uri photoURI, albumURI;
    String id;
    int a;
    String my_img_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
/*
        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_album = (Button) findViewById(R.id.btn_album);*/

        agree = (Button) findViewById(R.id.agree);
        iv_view = (ImageView) findViewById(R.id.iv_view);
        img_add = (ImageView) findViewById(R.id.img_add);

        tv_message = (TextView)findViewById(R.id.tv_message);

        a = 1;
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");


            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //수정완료버튼

                    if(a == 1){
                        finish();
                    }if (a == 2){
                        upload_img();
                    }

                }
            });




        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build();
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);
        //파일 생성


/*
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureCamera();
            }
        });

        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum();
            }
        });*/

        checkPermission();

        getImg();

    }


    private void upload_img2() {

        Call<Img> call = retrofitAPI.updateImage(id,BASE_URL+my_img_url);

        call.enqueue(new Callback<Img>() {
            @Override
            public void onResponse(Call<Img> call, Response<Img> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body().toString());
                Img postResponse = response.body();

                String content = "";
                content += "id: " + postResponse.getId() + "\n";
                content += "pw: " + postResponse.getMy_img_url() + "\n";

                Toast.makeText(getApplicationContext(), "프로필 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Img> call, Throwable t) {

                Log.i("onResponse", "3" );
                tv_message.setText(t.getMessage());

            }
        });
    }

    private void upload_img() {
                    //img_url은 이미지의 경로
                    Log.i("성공", "upload_img: "+mCurrentPhotoPath);
                    File file = new File(mCurrentPhotoPath);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
                    Call<Result2> resultCall = retrofitAPI.uploadImage(body);

                    resultCall.enqueue(new Callback<Result2>() {
                        @Override
                        public void onResponse(Call<Result2> call, Response<Result2> response) {
                            Log.i("성공","성공");
                            Result2 postResponse = response.body();

                String content = "";
                content += "id: " + postResponse.getResult() + "\n";
                my_img_url = postResponse.getResult();
                content += "pw: " + postResponse.getValue() + "\n";
                upload_img2();
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {
                Log.i("실패","실패");
                tv_message.setText(t.getMessage());
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

    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        // 외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "gyeom");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void getAlbum(){
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){

        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    // 카메라 전용 크랍
    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", false);
        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        a = 2;
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();

                        Glide.with(camera.this)
                                .load(imageUri)
                                .centerCrop()
                                .circleCrop()
                                .into(iv_view);

                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();
                    Glide.with(camera.this)
                            .load(albumURI)
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);

                }
                break;
        }
    }





    //권한요청 등록하는 부분
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(camera.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

    //버튼클릭
    public void OnClickHandler(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(R.array.LAN, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.LAN);
                if (items[pos].equals("카메라")){
                    captureCamera();
                }else if(items[pos].equals("갤러리")){
                    getAlbum();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
    private void getImg() {

        Log.i("데이터전송", "" + id);

        Call<Img> call = retrofitAPI.getImg(id);

        call.enqueue(new Callback<Img>() {
            @Override
            public void onResponse(Call<Img> call, Response<Img> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());
                    return;
                }

                Log.i("onResponse", "2" + response.body().toString());
                Img postResponse = response.body();

                String content = "";
                content += "id: " + postResponse.getId() + "\n";
                content += "pw: " + postResponse.getMy_img_url() + "\n";

                if (postResponse.getMy_img_url().equals("")){
                    Glide.with(camera.this)
                            .load(R.drawable.gibon)
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);

                }else{
                    Glide.with(camera.this)
                            .load(postResponse.getMy_img_url())
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);
                }


            }

            @Override
            public void onFailure(Call<Img> call, Throwable t) {

                Log.i("onResponse", "3" );

            }
        });

    }



}