package com.kplo.beat;

public class Feed_comment {

    int story_comment_num;
    int idx;
    String comment_id;
    String comment;
    String my_img_url;

    public String getMy_img_url() {
        return my_img_url;
    }

    public int getStory_comment_num() {
        return story_comment_num;
    }

    public String getComment() {
        return comment;
    }

    public String getComment_id() {
        return comment_id;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setMy_img_url(String my_img_url) {
        this.my_img_url = my_img_url;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setStory_comment_num(int story_comment_num) {
        this.story_comment_num = story_comment_num;
    }
}
