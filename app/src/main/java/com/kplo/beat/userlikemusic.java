package com.kplo.beat;

public class userlikemusic {
    int music_like_num;
    int music_idx;
    String id;

    public void setMusic_idx(int music_idx) {
        this.music_idx = music_idx;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMusic_like_num(int music_like_num) {
        this.music_like_num = music_like_num;
    }

    public int getMusic_idx() {
        return music_idx;
    }

    public String getId() {
        return id;
    }

    public int getMusic_like_num() {
        return music_like_num;
    }
}
