package com.kplo.beat;

public class Music_highlight {

    private int music_h_num;
    private int music_idx;
    private int time_s;
    private int time_e;

    public void setMusic_h_num(int music_h_num) {
        this.music_h_num = music_h_num;
    }

    public void setTime_e(int time_e) {
        this.time_e = time_e;
    }

    public void setTime_s(int time_s) {
        this.time_s = time_s;
    }

    public void setMusic_idx(int music_idx) {
        this.music_idx = music_idx;
    }

    public int getMusic_idx() {
        return music_idx;
    }

    public int getMusic_h_num() {
        return music_h_num;
    }

    public int getTime_e() {
        return time_e;
    }

    public int getTime_s() {
        return time_s;
    }

}
