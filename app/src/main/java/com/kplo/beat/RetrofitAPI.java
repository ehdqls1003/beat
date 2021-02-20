package com.kplo.beat;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {

    @FormUrlEncoded
    @POST("Signup.php")
    Call<Post> createPost(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("Login.php")
    Call<Post> loginPost(
            @Field("id") String id,
            @Field("pw") String pw
    );

    @FormUrlEncoded
    @POST("update_img.php")
    Call<Img> updateImage(
            @Field("id") String id,
            @Field("my_img_url") String my_img_url
    );

    @FormUrlEncoded
    @POST("insert_music.php")
    Call<music> insertMusic(
            @Field("id") String id,
            @Field("my_img_url") String my_img_url,
            @Field("title") String title,
            @Field("music_url") String music_url
    );
    @FormUrlEncoded
    @POST("insert_story.php")
    Call<Feed_Item> insertStory(
            @Field("id") String id,
            @Field("my_img_url") String my_img_url,
            @Field("my_img_url2") String my_img_url2,
            @Field("my_img_url3") String my_img_url3,
            @Field("my_img_url4") String my_img_url4,
            @Field("my_img_url5") String my_img_url5,
            @Field("story") String story
    );

    @FormUrlEncoded
    @POST("insert_feed_like.php")
    Call<Result2> insert_Story_like(
            @Field("idx") String idx,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("insert_music_like.php")
    Call<Result2> insert_Music_like(
            @Field("music_idx") int music_idx,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("insert_comment.php")
    Call<Result2> insertcomment(
            @Field("idx") int idx,
            @Field("comment_id") String comment_id,
            @Field("comment") String comment
    );

    @FormUrlEncoded
    @POST("delete_feed_like.php")
    Call<Result2> delete_Story_like(
            @Field("idx") String idx,
            @Field("id") String id
    );
    @FormUrlEncoded
    @POST("delete_music_like.php")
    Call<Result2> delete_Music_like(
            @Field("music_idx") int music_idx,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("insert_flow.php")
    Call<Result2> insert_flow(
            @Field("my_id") String my_id,
            @Field("friend_id") String friend_id
    );

    @FormUrlEncoded
    @POST("delete_flow.php")
    Call<Result2> delete_flow(
            @Field("my_id") String my_id,
            @Field("friend_id") String friend_id
    );

    @FormUrlEncoded
    @POST("delete_comment.php")
    Call<Result2> delete_comment(
            @Field("story_comment_num") int story_comment_num
    );

    @FormUrlEncoded
    @POST("delete_play_list_music.php")
    Call<Result2> delete_play_list_music(
            @Field("music_num") int music_num
    );

    @FormUrlEncoded
    @POST("delete_play_list_music_more.php")
    Call<Result2> delete_play_list_music_more(
            @Field("music_num[]") ArrayList<Integer> music_num
    );

    @FormUrlEncoded
    @POST("insert_my_play_list_music_more.php")
    Call<Result2> insert_my_play_list_music_more(
            @Field("music_num[]") ArrayList<Integer> music_num,
            @Field("playnum") int playnum
    );

    @FormUrlEncoded
    @POST("update_comment.php")
    Call<Result2> update_comment(
            @Field("story_comment_num") int story_comment_num,
            @Field("comment") String comment
    );


    //채팅방정보 불러오기
    @FormUrlEncoded
    @POST("my_roomlist.php")
    Call<ArrayList<Room_List>> getMy_roomlist(
            @Field("id") String id
    );
    //채팅방추가
    @FormUrlEncoded
    @POST("insert_room.php")
    Call<Result2> insert_room(
            @Field("user1_id") String id,
            @Field("user2_id") String feed_id
    );
    //채팅저장
    @FormUrlEncoded
    @POST("insert_message.php")
    Call<Result2> insert_message(
            @Field("user_id") String id,
            @Field("receive_user") String feed_id,
            @Field("message") String message,
            @Field("room_num") int room_num

    );

    //채팅보여주기
    @FormUrlEncoded
    @POST("show_message.php")
    Call<ArrayList<show_message>> getMessage(
            @Field("room_num") int room_num
    );

    //내이미지 들고오기
    @FormUrlEncoded
    @POST("get_myimg.php")
    Call<ArrayList<MyImg>> getMyImg(
            @Field("id") String id
    );


    @FormUrlEncoded
    @POST("my_flow.php")
    Call<ArrayList<Flow_Item>> getMy_flow(
            @Field("my_id") String id
    );

    @FormUrlEncoded
    @POST("all_flow.php")
    Call<ArrayList<Flow_Item>> getall_flow(
            @Field("my_id") String id
    );

    @FormUrlEncoded
    @POST("my_flowing.php")
    Call<ArrayList<Flow_Item>> getMy_flowing(
            @Field("my_id") String id
    );

    @FormUrlEncoded
    @POST("count_flow.php")
    Call<Result2> getcount_flow(
            @Field("my_id") String id
    );

    @FormUrlEncoded
    @POST("count_flowing.php")
    Call<Result2> getcount_flowing(
            @Field("my_id") String id
    );

    @FormUrlEncoded
    @POST("getjson.php")
    Call<Img> getImg(
            @Field("id") String id
    );

    @Multipart
    @POST("upload_img.php")
    Call<Result2> uploadImage(@Part MultipartBody.Part File);

    @Multipart
    @POST("upload_img2.php")
        Call<ArrayList<Result3>> uploadImage2(@Part ArrayList<MultipartBody.Part> File);

    @Multipart
    @POST("uploads.php")
    Call<Result2> uploadFile(@Part MultipartBody.Part File);

    @POST("recent_music.php")
    Call<ArrayList<Recent_Main_Item>> recent_music();
    //유저 재생목록 들고오기
    @FormUrlEncoded
    @POST("user_play_list.php")
    Call<ArrayList<User_Music_List>> User_play_list(
            @Field("id") String id
    );
    //유저 재생목록 들고오기
    @FormUrlEncoded
    @POST("user_play_list3.php")
    Call<ArrayList<playlistmusic>> User_play_list3(
            @Field("playnum") int playnum
    );
    //유저 재생목록 추가하기
    @FormUrlEncoded
    @POST("insert_user_play_list.php")
    Call<Result2> insert_user_play_list(
            @Field("idx") int idx,
            @Field("id") String id
    );

    //유저 플레이리스트
    @FormUrlEncoded
    @POST("insert_my_play_list.php")
    Call<Result2> insert_my_play_list(
            @Field("playlist_n") String playlist_n,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("my_feed.php")
    Call<ArrayList<Feed_Item>> getFeed(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("my_feed_details.php")
    Call<ArrayList<Feed_Item>> getFeed_details(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("music_like_count.php")
    Call<ArrayList<music_like_item>> getMusic_like_count(
            @Field("music_idx") int music_idx,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("my_feed_more.php")
    Call<ArrayList<Feed_Item>> getFeed_more(
            @Field("idx") int idx
    );

    @FormUrlEncoded
    @POST("my_feed_more_comment.php")
    Call<ArrayList<Feed_comment>> getFeed_more_comment(
            @Field("idx") int idx
    );

    @POST("all_feed_details.php")
    Call<ArrayList<Feed_Item>> getall_Feed_details();

    @POST("all_feed_like.php")
    Call<ArrayList<Feed_like_Item>> getall_Feed_like();

    @FormUrlEncoded
    @POST("my_music.php")
    Call<ArrayList<Recent_Main_Item>> getmymusic(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("my_play_list.php")
    Call<ArrayList<playlist>> my_play_list(
            @Field("id") String id
    );

    //@FormUrlEncoded
    //@POST("/auth/overlapChecker")
    //Call<Model__CheckAlready> postOverlapCheck(@Field("phone") String phoneNum, @Field("message") String message); //이건 요청시 사용하는거 (*데이터를 보낼때)

}
