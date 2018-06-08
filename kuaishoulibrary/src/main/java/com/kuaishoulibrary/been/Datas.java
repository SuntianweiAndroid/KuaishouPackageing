package com.kuaishoulibrary.been;

/**
 * Created by LuZheng on 2018/5/31.
 */



public class Datas {
    private double Length;
    private double Wide;
    private double Height;


    public Datas(double L, double W, double H) {
        this.Length = L;
        this.Wide = W;
        this.Height = H;
    }




    public void setLength(double Length){
        this.Length = Length;
    }

    public void setWide(double Wide){
        this.Wide = Wide;
    }

    public void setHeight(double Height){
        this.Height = Height;
    }

    public double getLength(){
        return this.Length;
    }

    public double getWide(){
        return this.Wide;
    }

    public double getHeight(){
        return this.Height;
    }
}

