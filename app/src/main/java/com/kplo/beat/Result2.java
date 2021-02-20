package com.kplo.beat;

import java.util.ArrayList;

public class Result2 {
    private String result;
    private String value;
    private ArrayList<String> a;

    /*public Result2(String result, String value){
        this.result = result;
        this.value = value;
    }*/

    public String getResult(){
        return result;
    }

    public String getValue(){
        return value;
    }

    public void setResult(){
        this.result = result;
    }

    public void setValue(){
        this.value = value;
    }

    public ArrayList<String> getA() {
        return a;
    }

    public void setA(ArrayList<String> a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + result + '\'' +
                ", pw='" + value +
                '}';
    }


}
