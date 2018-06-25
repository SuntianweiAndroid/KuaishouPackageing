package com.kuaishoupackaging.been;

public class OperBody {
    private String barCode;
    private String machineCode;
    private double weight;
    private double length;
    private double width;
    private double high;
    private double volume;
    private long scannerTime;

    public OperBody(String barCode, String machineCode, double weight, double length, double width, double high, double volume, long scannerTime) {
        this.barCode = barCode;
        this.machineCode = machineCode;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.high = high;
        this.volume = volume;
        this.scannerTime = scannerTime;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double gethigh() {
        return high;
    }

    public void sethigh(double high) {
        this.high = high;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public long getScannerTime() {
        return scannerTime;
    }

    public void setScannerTime(long scannerTime) {
        this.scannerTime = scannerTime;
    }

    @Override
    public String toString() {
        return "OperBody{" +
                "barCode='" + barCode + '\'' +
                ", machineCode='" + machineCode + '\'' +
                ", weight=" + weight +
                ", length=" + length +
                ", width=" + width +
                ", high=" + high +
                ", volume=" + volume +
                ", scannerTime=" + scannerTime +
                '}';
    }
}
