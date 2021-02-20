package com.kplo.beat;

public class Post {
    private String id;
    private String pw;

    public Post(String id, String pw){
        this.id = id;
        this.pw = pw;
    }

    public String getId(){
        return id;
    }

    public String getPw(){
        return pw;
    }

    public void setId(){
        this.id = id;
    }

    public void setPw(){
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", pw='" + pw +
                '}';
    }

}
