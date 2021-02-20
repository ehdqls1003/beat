package com.kplo.beat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class upload_music extends AppCompatActivity {

    private static final int MY_PERMISSION_STORAGE = 1111;
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;
    private static final String BASE_URL = "http://15.164.220.153/";
    private RetrofitAPI retrofitAPI;
    TextView tv_message;
    Dialog progressDialog;
    Button button;
    String paths;
    String result;
    Button btn_capture, btn_album,agree;
    ImageView iv_view,img_add;
    EditText inputtitle;
    String mCurrentPhotoPath;
    Uri imageUri;
    Uri photoURI, albumURI;
    String id;
    String my_img_url;
    String music_url;
    String title;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        id = sf.getString("id","");
        agree = (Button) findViewById(R.id.agree);
        iv_view = (ImageView) findViewById(R.id.iv_view);
        img_add = (ImageView) findViewById(R.id.img_add);
        inputtitle = findViewById(R.id.inputtitle);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getMusic();

            }
        });
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadFile();

            }
        });




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


    private void getMusic(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        Log.i("getAlbum", "Call");
        i.setType("audio/*");  //여러가지 Type은 아래 표로 정리해두었습니다.
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i,1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                uri = data.getData();
                Log.e("uri:",""+uri);
                paths = getRealPathFromURI(uri);
                Log.e("Path:",""+paths);
                File file = new File(paths);
                button.setText(file.getName());

            }
        }else{

            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Log.i("REQUEST_TAKE_PHOTO", "OK");
                            galleryAddPic();

                            Glide.with(upload_music.this)
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
                        Glide.with(upload_music.this)
                                .load(albumURI)
                                .centerCrop()
                                .circleCrop()
                                .into(iv_view);

                    }
                    break;
            }
        }
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

    private void galleryAddPic(){

        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void getAlbum(){
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally { cursor.close();
        }
        return null;
    }


    private void uploadFile() {
        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(paths);
        Log.e("paths2",""+paths);

        // Parsing any Media type file
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
        Log.e("file.getName()",""+file.getName());
        Log.e("requestFile",""+requestFile);

        Call<Result2> resultCall = retrofitAPI.uploadFile(body);

        resultCall.enqueue(new Callback<Result2>() {
            @Override
            public void onResponse(Call<Result2> call, Response<Result2> response) {
                Log.i("성공","성공");
                Result2 postResponse = response.body();

                result = postResponse.getResult();
                music_url = postResponse.getResult();
                Log.e("result",""+result);
                upload_img();
            }

            @Override
            public void onFailure(Call<Result2> call, Throwable t) {
                Log.i("실패","실패");

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
            }
        });
    }

    private void upload_img2() {
        title = inputtitle.getText().toString();
        Call<music> call = retrofitAPI.insertMusic(id,BASE_URL+my_img_url,title,BASE_URL+music_url);

        call.enqueue(new Callback<music>() {
            @Override
            public void onResponse(Call<music> call, Response<music> response) {
                if (!response.isSuccessful()) {

                    Log.i("onResponse", "1" + response.body().toString());

                    return;
                }

                Log.i("onResponse", "2" + response.body().toString());
                music postResponse = response.body();



            }

            @Override
            public void onFailure(Call<music> call, Throwable t) {

                Log.i("onResponse", "3" );
                Toast.makeText(getApplicationContext(), "곡 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}