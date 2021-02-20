package com.kplo.beat;

public class show_message {
    String user_id;
    String user_img;
    String message;
    String time;

    public String getMessage() {
        return message;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }
}

