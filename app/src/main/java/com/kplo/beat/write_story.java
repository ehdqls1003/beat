package com.kplo.beat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class write_story extends AppCompatActivity implements write_story_Adapter.MyRecyclearViewClickListener{
    private static final int MY_PERMISSION_STORAGE = 1111;
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;
    private static final String BASE_URL = "http://15.164.220.153/";
    String mCurrentPhotoPath;
    Uri imageUri;
    Uri photoURI, albumURI;
    ImageView iv_view;
    String id,story;
    ArrayList<String> my_img_url;
    EditText e_story;
    AppCompatButton add_feed;
    boolean imgs = false;

    ArrayList<String> img_arr;
    ArrayList<String> img_arr2 = new ArrayList<>();
    write_story_Adapter adapter;

    String paths;
    //서버연결

    private RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_story);
        img_arr = new ArrayList<>();
        e_story = findViewById(R.id.e_story);
        add_feed = findViewById(R.id.add_feed);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        id = sf.getString("id","");



        add_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgs){
                    upload_img();
                }else{
                    if(e_story.getText().equals("")){
                        Toast.makeText(write_story.this, "스토리를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        upload_story3();
                    }

                }

            }
        });


    }


    //버튼클릭
    public void OnClickHandler(View view)
    {
        if(img_arr.size() < 5) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            builder.setItems(R.array.LAN, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    String[] items = getResources().getStringArray(R.array.LAN);
                    if (items[pos].equals("카메라")) {
                        captureCamera();
                    } else if (items[pos].equals("갤러리")) {
                        getAlbum();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else{
            Toast.makeText(write_story.this, "이미지는 최대 5장까지 추가 가능합니다.", Toast.LENGTH_SHORT).show();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();/*
                        iv_view.setVisibility(View.VISIBLE);*/
                        imgs = true;/*
                        Glide.with(write_story.this)
                                .load(imageUri)
                                .centerCrop()
                                .circleCrop()
                                .into(iv_view);*/

                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {


                            //the selected audio.
                            photoURI = data.getData();/*
                            iv_view.setVisibility(View.VISIBLE);*/
                            /*Glide.with(write_story.this)
                                    .load(photoURI)
                                    .centerCrop()
                                    .circleCrop()
                                    .into(iv_view);*/
                            paths = getRealPathFromURI(photoURI);
                            img_arr.add(paths);

/*
                            paths = getRealPathFromURI(photoURI);*/

                            /*File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);*/
                            imgs = true;



                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();/*
                    iv_view.setVisibility(View.VISIBLE);*/
                    imgs = true;/*
                    Glide.with(write_story.this)
                            .load(albumURI)
                            .centerCrop()
                            .circleCrop()
                            .into(iv_view);*/

                }
                break;
        }
    }
    //서버

    private void upload_img() {
                    //img_url은 이미지의 경로
        /*File file = new File(paths);
        Log.e("paths2",""+paths);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);*/
/*
                    MultipartBody.Part[] body = new MultipartBody.Part[img_arr.size()];*/
                    ArrayList<MultipartBody.Part> body =  new ArrayList<MultipartBody.Part>();

                    Log.e("파일즈","안드1"+img_arr.size());

                    for (int index = 0; index < img_arr.size(); index++) {
                        File file = new File(img_arr.get(index));
                        Log.e("파일즈","path"+img_arr.get(index));
                        RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
                        body.add(MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), surveyBody));
                        Log.e("파일즈","body"+body.get(index));

                    }

                    Log.e("파일즈","바디"+body.size());
                    Call<ArrayList<Result3>> resultCall = retrofitAPI.uploadImage2(body);

                    resultCall.enqueue(new Callback<ArrayList<Result3>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Result3>> call, Response<ArrayList<Result3>> response) {
                            Log.i("성공","성공");
                            ArrayList<Result3> postResponse = response.body();

                            for (int i=0; i < postResponse.size();i++){

                                Log.e("파일즈","서버"+postResponse.get(i).getResult());
                                img_arr2.add(postResponse.get(i).getResult());
                            }

                            upload_story2();


                            }

                            @Override
                            public void onFailure(Call<ArrayList<Result3>> call, Throwable t) {
                                Log.i("실패","실패");

                            }
        });
    }

    private void upload_story2() {
        story = e_story.getText().toString();
        Call<Feed_Item> call;
        if(img_arr2.size() == 1){
            call = retrofitAPI.insertStory(id,BASE_URL+img_arr2.get(0),"","","","",story);

            call.enqueue(new Callback<Feed_Item>() {
                @Override
                public void onResponse(Call<Feed_Item> call, Response<Feed_Item> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    Feed_Item postResponse = response.body();

                    Toast.makeText(getApplicationContext(), "스토리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();


                }

                @Override
                public void onFailure(Call<Feed_Item> call, Throwable t) {

                    Log.i("onResponse", "3" );
                }
            });
        }else if(img_arr.size() == 2){
            call = retrofitAPI.insertStory(id,BASE_URL+img_arr2.get(0),BASE_URL+img_arr2.get(1),"","","",story);

            call.enqueue(new Callback<Feed_Item>() {
                @Override
                public void onResponse(Call<Feed_Item> call, Response<Feed_Item> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    Feed_Item postResponse = response.body();

                    Toast.makeText(getApplicationContext(), "스토리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();


                }

                @Override
                public void onFailure(Call<Feed_Item> call, Throwable t) {

                    Log.i("onResponse", "3" );
                }
            });

        }else if(img_arr.size() == 3){
            call = retrofitAPI.insertStory(id,BASE_URL+img_arr2.get(0),BASE_URL+img_arr2.get(1),BASE_URL+img_arr2.get(2),"","",story);

            call.enqueue(new Callback<Feed_Item>() {
                @Override
                public void onResponse(Call<Feed_Item> call, Response<Feed_Item> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    Feed_Item postResponse = response.body();

                    Toast.makeText(getApplicationContext(), "스토리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();


                }

                @Override
                public void onFailure(Call<Feed_Item> call, Throwable t) {

                    Log.i("onResponse", "3" );
                }
            });

        }else if(img_arr.size() == 4){
            call = retrofitAPI.insertStory(id,BASE_URL+img_arr2.get(0),BASE_URL+img_arr2.get(1),BASE_URL+img_arr2.get(2),BASE_URL+img_arr2.get(3),"",story);

            call.enqueue(new Callback<Feed_Item>() {
                @Override
                public void onResponse(Call<Feed_Item> call, Response<Feed_Item> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    Feed_Item postResponse = response.body();

                    Toast.makeText(getApplicationContext(), "스토리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();


                }

                @Override
                public void onFailure(Call<Feed_Item> call, Throwable t) {

                    Log.i("onResponse", "3" );
                }
            });
        }else if(img_arr.size() == 5){
            call = retrofitAPI.insertStory(id,BASE_URL+img_arr2.get(0),BASE_URL+img_arr2.get(1),BASE_URL+img_arr2.get(2),BASE_URL+img_arr2.get(3),BASE_URL+img_arr2.get(4),story);

            call.enqueue(new Callback<Feed_Item>() {
                @Override
                public void onResponse(Call<Feed_Item> call, Response<Feed_Item> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    Feed_Item postResponse = response.body();

                    Toast.makeText(getApplicationContext(), "스토리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();


                }

                @Override
                public void onFailure(Call<Feed_Item> call, Throwable t) {

                    Log.i("onResponse", "3" );
                }
            });
        }
    }

    private void upload_story3() {
        story = e_story.getText().toString();
        Call<Feed_Item> call = retrofitAPI.insertStory(id,"","","","","",story);

        call.enqueue(new Callback<Feed_Item>() {
            @Override
            public void onResponse(Call<Feed_Item> call, Response<Feed_Item> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                Feed_Item postResponse = response.body();

                Toast.makeText(getApplicationContext(), "스토리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();


            }

            @Override
            public void onFailure(Call<Feed_Item> call, Throwable t) {

                Log.i("onResponse", "3" );
            }
        });
    }


    public String getRealPathFromURI(Uri uri) {
        int index = 0; String[] proj = {MediaStore.Images.Media.DATA};
        // 이미지 경로로 해당 이미지에 대한 정보를 가지고 있는 cursor 호출
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        // 데이터가 있으면(가장 처음에 위치한 레코드를 가리킴)
        if (cursor.moveToFirst()) {
            // 해당 필드의 인덱스를 반환하고, 존재하지 않을 경우 예외를 발생시킨다.
            index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        Log.d("getRealPathFromURI", "getRealPathFromURI: " + cursor.getString(index));
        return cursor.getString(index);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(write_story.this,LinearLayoutManager.HORIZONTAL,false)) ;

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new write_story_Adapter(img_arr) ;
        recyclerView.setAdapter(adapter) ;

        //이거안해주면 리스너안먹힘
        adapter.setOnClickListener(write_story.this);

    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {

    }
}