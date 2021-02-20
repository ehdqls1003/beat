package com.kplo.beat;

public class music {
    private String id;
    private String my_img_url;
    private String title;
    private String music_url;

    public music(String id, String my_img_url,String title){
        this.id = id;
        this.my_img_url = my_img_url;
        this.title = title;
        this.music_url = music_url;

    }

    public String getId(){
        return id;
    }

    public String getMy_img_url(){
        return my_img_url;
    }

    public String getTitle(){
        return  title;
    }
    public String getMusic_url(){
        return music_url;
    }

    public void setId(){
        this.id = id;
    }

    public void setMy_img_url(){
        this.my_img_url = my_img_url;
    }

    public void setTitle(){
        this.title = title;
    }
    public void setMusic_url(){
        this.music_url = music_url;
    }


}
