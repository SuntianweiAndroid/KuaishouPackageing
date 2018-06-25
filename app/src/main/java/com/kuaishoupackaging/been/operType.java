package com.kuaishoupackaging.been;

public class operType {
    private int operType;

    public operType(int operType) {
        this.operType = operType;
    }

    public int getOperType() {
        return operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
    }

    @Override
    public String toString() {
        return "operType{" +
                "operType=" + operType +
                '}';
    }
}
