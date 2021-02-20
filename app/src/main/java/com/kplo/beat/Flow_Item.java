package com.kplo.beat;

public class Flow_Item {

    private int flow_num;
    private String my_id;
    private String friend_id;
    private String my_img_url;

    public void setFlow_num(int flow_num) {
        this.flow_num = flow_num;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public void setMy_id(String my_id) {
        this.my_id = my_id;
    }

    public void setMy_img_url(String my_img_url) {
        this.my_img_url = my_img_url;
    }

    public String getMy_img_url() {
        return my_img_url;
    }

    public int getFlow_num() {
        return flow_num;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public String getMy_id() {
        return my_id;
    }

}
