package com.kplo.beat;

import java.io.Serializable;

public class Meassage implements Serializable {

    private static final long serialVersionUID = 1234567890L;

    String User_id;
    String Meassage;
    String User_img;

    public String getMeassage() {
        return Meassage;
    }

    public String getUser_id() {
        return User_id;
    }

    public String getUser_img() {
        return User_img;
    }

    public void setMeassage(String meassage) {
        Meassage = meassage;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public void setUser_img(String user_img) {
        User_img = user_img;
    }


    @Override
    public String toString() {
        return "BoardVO [idx=" + Meassage +"]";
    }


}
