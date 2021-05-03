package com.kplo.beat;

public class m_buy_item {
    int buy_num;
    int music_idx;
    String receipt_id;
    String purchase_id;
    String time;
    int state;
    String music_img_url;
    String user_id;
    String title;
    String user_img_url;
    String music_url;

    public String getUser_img_url() {
        return user_img_url;
    }

    public void setUser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getMusic_url() {
        return music_url;
    }

    public void setMusic_url(String music_url) {
        this.music_url = music_url;
    }

    public String getMusic_img_url() {
        return music_img_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setMusic_img_url(String music_img_url) {
        this.music_img_url = music_img_url;
    }

    public int getMusic_idx() {
        return music_idx;
    }

    public String getTime() {
        return time;
    }

    public int getBuy_num() {
        return buy_num;
    }

    public int getState() {
        return state;
    }

    public String getPurchase_id() {
        return purchase_id;
    }

    public String getReceipt_id() {
        return receipt_id;
    }

    public void setMusic_idx(int music_idx) {
        this.music_idx = music_idx;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBuy_num(int buy_num) {
        this.buy_num = buy_num;
    }

    public void setPurchase_id(String purchase_id) {
        this.purchase_id = purchase_id;
    }

    public void setReceipt_id(String receipt_id) {
        this.receipt_id = receipt_id;
    }

    public void setState(int state) {
        this.state = state;
    }
}
