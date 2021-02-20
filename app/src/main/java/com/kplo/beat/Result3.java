package com.kplo.beat;

public class Result3 {
    private String result;
    private String value;

    public Result3(String result){
        this.result = result;
    }

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

    @Override
    public String toString() {
        return "Post{" +
                "id='" + result + '\'' +
                ", pw='" + value +
                '}';
    }

}
