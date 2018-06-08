package com.kuaishou.utils;

/**
 * Created by cch on 2018/3/27.
 */

public class CameraEntity {

    private byte[] yuvData;
    private int height;
    private int width;

    public CameraEntity(byte[] yuvData, int height, int width) {
        this.yuvData = yuvData;
        this.height = height;
        this.width = width;

    }

    public byte[] getYuvData() {
        return yuvData;
    }

    public void setYuvData(byte[] yuvData) {
        this.yuvData = yuvData;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
