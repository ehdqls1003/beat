package com.kplo.beat;

public class User_Music_List {
    int list_num;
    int music_idx;
    String music_img;
    String music_title;
    String music_url;
    String music_id;

    public int getList_num() {
        return list_num;
    }

    public int getMusic_idx() {
        return music_idx;
    }

    public String getMusic_img() {
        return music_img;
    }

    public String getMusic_title() {
        return music_title;
    }

    public String getMusic_url() {
        return music_url;
    }

    public void setList_num(int list_num) {
        this.list_num = list_num;
    }

    public void setMusic_img(String music_img) {
        this.music_img = music_img;
    }

    public void setMusic_idx(int music_idx) {
        this.music_idx = music_idx;
    }

    public void setMusic_title(String music_title) {
        this.music_title = music_title;
    }

    public void setMusic_url(String music_url) {
        this.music_url = music_url;
    }

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

}
