package com.kplo.beat;

public class Room_List {
    int r_num;
    String user_id;
    String message;
    String img;
    String time;

    public int getR_num() {
        return r_num;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTime() {
        return time;
    }

    public String getImg() {
        return img;
    }

    public String getMessage() {
        return message;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setR_num(int room_num) {
        this.r_num = room_num;
    }

}
