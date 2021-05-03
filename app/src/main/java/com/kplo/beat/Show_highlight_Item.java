package com.kplo.beat;

public class Show_highlight_Item {

    String music_url;
    String music_img;
    String user_id;
    String user_img;
    String title;
    int heart;
    int heart_count;
    int m_idx;
    int music_h_num;

    int time_s;
    int time_e;

    public int getMusic_h_num() {
        return music_h_num;
    }

    public void setMusic_h_num(int music_h_num) {
        this.music_h_num = music_h_num;
    }
    public int getM_idx() {
        return m_idx;
    }


    public void setM_idx(int m_idx) {
        this.m_idx = m_idx;
    }

    public int getHeart() {
        return heart;
    }

    public String getMusic_url() {
        return music_url;
    }

    public String getMusic_img() {
        return music_img;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getHeart_count() {
        return heart_count;
    }

    public int getTime_s() {
        return time_s;
    }

    public int getTime_e() {
        return time_e;
    }

    public String getTitle() {
        return title;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public void setMusic_url(String music_url) {
        this.music_url = music_url;
    }

    public void setMusic_img(String music_img) {
        this.music_img = music_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setHeart_count(int heart_count) {
        this.heart_count = heart_count;
    }

    public void setTime_s(int time_s) {
        this.time_s = time_s;
    }

    public void setTime_e(int time_e) {
        this.time_e = time_e;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
