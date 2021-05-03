package com.kplo.beat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed_more extends AppCompatActivity implements Comment_Adapter.MyRecyclearViewClickListener {

    int idx;
    private RetrofitAPI retrofitAPI;
    private ArrayList<Feed_Item> postResponse;
    private ArrayList<Feed_comment> comments;
    ImageView u_img,f_img,heart,heart_outline,pancil;
    TextView u_name,heart_count,comment_count,f_story,comment;
    ViewPager mViewPager;
    Feed_ViewPagerAdapter mViewPagerAdapter;
    CircleIndicator indicator;
    boolean like = false;
    String id;
    String idxs;
    Comment_Adapter adapter;
    EditText comment_input;
    ScrollView scroll;
    String su_comment;
    int c_pos;

    int story_comment_num;

    private ArrayList<Feed_like_Item> mData2 = null ;
    boolean sugo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_more);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        mViewPager = findViewById(R.id.viewPagerMain);
        u_img = findViewById(R.id.u_img) ;
        mViewPager = findViewById(R.id.viewPagerMain);
        u_name = findViewById(R.id.u_name) ;
        heart_count = findViewById(R.id.heart_count) ;
        comment_count = findViewById(R.id.comment_count) ;
        f_story = findViewById(R.id.f_story) ;
        indicator = findViewById(R.id.indicator);
        heart = findViewById(R.id.heart);
        heart_outline = findViewById(R.id.heart_outline);
        comment = findViewById(R.id.comment);
        pancil = findViewById(R.id.pancil);
        comment_input = findViewById(R.id.comment_input);
        scroll = findViewById(R.id.scroll);

        retrofitAPI = AppConfig.getRetrofit().create(RetrofitAPI.class);

        Intent intent = getIntent(); /*데이터 수신*/
        //리사이클러뷰 포지션값이앙니라 이거 idx값
        idx = intent.getExtras().getInt("idx"); /*int형*/

        idxs = Integer.toString(idx);


        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<Result2> call = retrofitAPI.delete_Story_like(idxs,id);
                call.enqueue(new Callback<Result2>() {
                    @Override
                    public void onResponse(Call<Result2> call, Response<Result2> response) {

                        heart_count.setText(Integer.toString(Integer.parseInt(heart_count.getText().toString())-1));
                        heart.setVisibility(View.INVISIBLE);
                        heart_outline.setVisibility(View.VISIBLE);
                        Log.e("좋아요","좋아요해제");
                    }

                    @Override
                    public void onFailure(Call<Result2> call, Throwable t) {

                    }
                });
            }
        });

        heart_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Result2> call = retrofitAPI.insert_Story_like(idxs,id);
                call.enqueue(new Callback<Result2>() {
                    @Override
                    public void onResponse(Call<Result2> call, Response<Result2> response) {
                        heart_count.setText(Integer.toString(Integer.parseInt(heart_count.getText().toString())+1));
                        heart.setVisibility(View.VISIBLE);
                        heart_outline.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onFailure(Call<Result2> call, Throwable t) {

                    }
                });
            }
        });
        pancil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!sugo){

                    if (!comment_input.getText().toString().equals("")){

                        Call<Result2> call5 = retrofitAPI.insertcomment(idx,id,comment_input.getText().toString());
                        call5.enqueue(new Callback<Result2>() {
                            @Override
                            public void onResponse(Call<Result2> call5, Response<Result2> response5) {
                                comment_input.setText(null);
                                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                                commentcall2();
                                comment_count.setText(Integer.toString(Integer.parseInt(comment_count.getText().toString())+1));
                                Log.e("댓글 작성","완료");
                            }

                            @Override
                            public void onFailure(Call<Result2> call5, Throwable t5) {

                                Log.e("댓글 작성","실패");
                            }
                        });
                    }
                }else{
                    String value = comment_input.getText().toString();

                    Call<Result2> call6 = retrofitAPI.update_comment(story_comment_num,value);
                    call6.enqueue(new Callback<Result2>() {
                        @Override
                        public void onResponse(Call<Result2> call, Response<Result2> response) {
                            commentcall4();
                            Log.e("댓글","수정완료");
                            comment_input.setText("");
                            sugo = false;
                        }

                        @Override
                        public void onFailure(Call<Result2> call, Throwable t) {

                        }
                    });



                }

            }
        });

        u_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Feed_more.this, Feed.class);

                intent.putExtra("feed_id",postResponse.get(0).getId()); /*송신*/

                startActivity(intent);

            }
        });

        u_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Feed_more.this, Feed.class);

                intent.putExtra("feed_id",postResponse.get(0).getId()); /*송신*/

                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Call<ArrayList<Feed_Item>> call = retrofitAPI.getFeed_more(idx);
        call.enqueue(new Callback<ArrayList<Feed_Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_Item>> call, Response<ArrayList<Feed_Item>> response) {
                postResponse = response.body();
                /*postResponse.get(0).*/
                Call<ArrayList<Feed_like_Item>> call2 = retrofitAPI.getall_Feed_like();
                call2.enqueue(new Callback<ArrayList<Feed_like_Item>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Feed_like_Item>> call2, Response<ArrayList<Feed_like_Item>> response2) {
                        mData2= response2.body();
                        if (postResponse.get(0).getMy_img_url().equals("")) {
                            Glide.with(Feed_more.this)
                                    .load(R.drawable.black)
                                    .apply(new RequestOptions().circleCrop().centerCrop())
                                    .centerCrop()
                                    .circleCrop()
                                    .into(u_img);
                        } else {
                            Glide.with(Feed_more.this)
                                    .load(postResponse.get(0).getMy_img_url())
                                    .apply(new RequestOptions().circleCrop().centerCrop())
                                    .centerCrop()
                                    .circleCrop()
                                    .into(u_img);
                        }


                        heart_count.setText(Integer.toString(postResponse.get(0).getHeart_count()));
                        comment_count.setText(Integer.toString(postResponse.get(0).getCommnet_count()));
                        u_name.setText(postResponse.get(0).getId());
                        for (int i = 0; i < mData2.size(); i++) {
                            Log.e("하트", "mData2.get(i).getIdx()" + mData2.get(i).getIdx());
                            if (Integer.toString(postResponse.get(0).getIdx()).equals(mData2.get(i).getIdx())) {
                                Log.e("하트", "mData2.get(i).getId()" + mData2.get(i).getId());
                                if (mData2.get(i).getId().equals(id)) {
                                    like = true;
                                    break;
                                }
                            }
                        }
                        if (like) {
                            heart.setVisibility(View.VISIBLE);
                            heart_outline.setVisibility(View.INVISIBLE);

                            like = false;
                        } else {
                            heart.setVisibility(View.INVISIBLE);
                            heart_outline.setVisibility(View.VISIBLE);
                        }

                        if (postResponse.get(0).getStory().equals("")) {
                        } else {
                            f_story.setText(postResponse.get(0).getStory());
                        }

                        ArrayList<String> images = new ArrayList<>();
                        if (!postResponse.get(0).getStory_img_url().equals("")) {
                            images.add(postResponse.get(0).getStory_img_url());
                            if (!postResponse.get(0).getStory_img_url2().equals("")) {
                                images.add(postResponse.get(0).getStory_img_url2());
                                if (!postResponse.get(0).getStory_img_url3().equals("")) {
                                    images.add(postResponse.get(0).getStory_img_url3());
                                    if (!postResponse.get(0).getStory_img_url4().equals("")) {
                                        images.add(postResponse.get(0).getStory_img_url4());
                                        if (!postResponse.get(0).getStory_img_url5().equals("")) {
                                            images.add(postResponse.get(0).getStory_img_url5());
                                        }
                                    }
                                }
                            }
                        }
                        mViewPagerAdapter = new Feed_ViewPagerAdapter(Feed_more.this, images);
                        // Adding the Adapter to the ViewPager
                        mViewPager.setAdapter(mViewPagerAdapter);
                        indicator.setViewPager(mViewPager);

                        if (postResponse.get(0).getStory_img_url().equals("")) {
                            mViewPager.setVisibility(View.GONE);
                        } else {
                            mViewPager.setVisibility(View.VISIBLE);
                        }
                    }


                    @Override
                    public void onFailure(Call<ArrayList<Feed_like_Item>> call2, Throwable t2) {

                    }
                });

            }
            @Override
            public void onFailure(Call<ArrayList<Feed_Item>> call, Throwable t) {

            }
        });

        commentcall();

    }

    public void commentcall(){
        Call<ArrayList<Feed_comment>> call3 = retrofitAPI.getFeed_more_comment(idx);

        call3.enqueue(new Callback<ArrayList<Feed_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_comment>> cal3, Response<ArrayList<Feed_comment>> response3) {
                comments = response3.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Feed_more.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Comment_Adapter(comments) ;
                recyclerView.setAdapter(adapter) ;

                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Feed_more.this);

/*
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(MyPlayList.this);*/


            }

            @Override
            public void onFailure(Call<ArrayList<Feed_comment>> call3, Throwable t3) {

            }
        });

    }
    public void commentcall2(){
        Call<ArrayList<Feed_comment>> call3 = retrofitAPI.getFeed_more_comment(idx);

        call3.enqueue(new Callback<ArrayList<Feed_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_comment>> cal3, Response<ArrayList<Feed_comment>> response3) {
                comments = response3.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Feed_more.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Comment_Adapter(comments) ;
                recyclerView.setAdapter(adapter) ;
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Feed_more.this);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                Log.e("커맨트사이드",""+comments.size());

                /*
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(MyPlayList.this);*/


            }

            @Override
            public void onFailure(Call<ArrayList<Feed_comment>> call3, Throwable t3) {

            }
        });


    }
    public void commentcall3(){
        Call<ArrayList<Feed_comment>> call3 = retrofitAPI.getFeed_more_comment(idx);

        call3.enqueue(new Callback<ArrayList<Feed_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_comment>> cal3, Response<ArrayList<Feed_comment>> response3) {
                comments = response3.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Feed_more.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Comment_Adapter(comments) ;
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Feed_more.this);
                recyclerView.setAdapter(adapter) ;
                recyclerView.scrollToPosition(c_pos);
                Log.e("커맨트사이드",""+comments.size());

/*
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(MyPlayList.this);*/


            }

            @Override
            public void onFailure(Call<ArrayList<Feed_comment>> call3, Throwable t3) {

            }
        });


    }
    public void commentcall4(){
        Call<ArrayList<Feed_comment>> call3 = retrofitAPI.getFeed_more_comment(idx);

        call3.enqueue(new Callback<ArrayList<Feed_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Feed_comment>> cal3, Response<ArrayList<Feed_comment>> response3) {
                comments = response3.body();

                // 리사이클러뷰에 LinearLayoutManager 객체 지정.
                RecyclerView recyclerView = findViewById(R.id.recent_music_recycler) ;
                recyclerView.setLayoutManager(new LinearLayoutManager(Feed_more.this,LinearLayoutManager.VERTICAL,false)) ;

                // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                adapter = new Comment_Adapter(comments) ;
                //이거안해주면 리스너안먹힘
                adapter.setOnClickListener(Feed_more.this);
                recyclerView.setAdapter(adapter) ;
                Log.e("커맨트사이드",""+comments.size());



            }

            @Override
            public void onFailure(Call<ArrayList<Feed_comment>> call3, Throwable t3) {

            }
        });


    }

    @Override
    public void onItemClicked(int position, String setMusic_url) {

    }

    @Override
    public void onmore_buttonClicked(int story_comment_num,String comment,int c_pos) {
        this.story_comment_num = story_comment_num;
        this.su_comment = comment;
        this.c_pos = c_pos;
        OnClickupload();
    }

    @Override
    public void onIdClicked(String feed_id) {

        Intent intent = new Intent(Feed_more.this, Feed.class);

        intent.putExtra("feed_id",feed_id); /*송신*/

        startActivity(intent);
    }

    //게시물 작성 버튼클릭
    public void OnClickupload()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(R.array.comment_more, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(final DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.comment_more);
                if (items[pos].equals("수정하기")){
                    edittext();



                }else if(items[pos].equals("삭제하기")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(Feed_more.this);

                    builder.setTitle("댓글 삭제").setMessage("댓글을 완전히 삭제할까요?.");

                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                            Call<Result2> call5 = retrofitAPI.delete_comment(story_comment_num);
                            call5.enqueue(new Callback<Result2>() {
                                @Override
                                public void onResponse(Call<Result2> call, Response<Result2> response) {
                                    Log.e("댓글","삭제완료");
                                    commentcall3();
                                    comment_count.setText(Integer.toString(Integer.parseInt(comment_count.getText().toString())-1));
                                }

                                @Override
                                public void onFailure(Call<Result2> call, Throwable t) {

                                }
                            });

                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                    });


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }


            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void edittext(){
        sugo = true;

        comment_input.setText(su_comment);



        /*AlertDialog.Builder ad = new AlertDialog.Builder(Feed_more.this);

            ad.setTitle("수정하기");       // 제목 설정

        // EditText 삽입하기
        final EditText et = new EditText(Feed_more.this);
        ad.setView(et);
        et.setText(su_comment);

        // 확인 버튼 설정
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Text 값 받아서 로그 남기기
                String value = et.getText().toString();

                Call<Result2> call6 = retrofitAPI.update_comment(story_comment_num,value);
                call6.enqueue(new Callback<Result2>() {
                    @Override
                    public void onResponse(Call<Result2> call, Response<Result2> response) {
                        commentcall4();
                        Log.e("댓글","수정완료");
                    }

                    @Override
                    public void onFailure(Call<Result2> call, Throwable t) {

                    }
                });

                dialog.dismiss();     //닫기
                // Event
            }
        });

        // 취소 버튼 설정
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                // Event
            }
        });

// 창 띄우기
        ad.show();*/

    }
}