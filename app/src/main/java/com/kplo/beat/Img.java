package com.kplo.beat;

public class Img {
    private String id;
    private String my_img_url;

    public Img(String id, String my_img_url){
        this.id = id;
        this.my_img_url = my_img_url;
    }

    public String getId(){
        return id;
    }

    public String getMy_img_url(){
        return my_img_url;
    }

    public void setId(){
        this.id = id;
    }

    public void setMy_img_url(){
        this.my_img_url = my_img_url;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", my_img_url='" + my_img_url +
                '}';
    }

}
