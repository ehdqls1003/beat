package com.kplo.beat;

public class highlight_comment {

    int highlight_comment_num;
    int idx;
    String comment_id;
    String comment;
    String my_img_url;

    public String getMy_img_url() {
        return my_img_url;
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

    public int getHighlight_comment_num() {
        return highlight_comment_num;
    }

    public void setHighlight_comment_num(int highlight_comment_num) {
        this.highlight_comment_num = highlight_comment_num;
    }
}
