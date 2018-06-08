package com.kuaishoupackaging.been;

public class ScanDatas {

    /**
     * operType : 1
     * operBody : {         "barcode": "38373413-1-1-",         "machineCode": "JD-MJQ-001",         "weight": 28.1,         "length": 14.33,         "width": 25.6,         "high": 28.45,         "volume": 0,         "scannerTime": 1502094011255     }
     */

    private int operType;
    private String operBody;

    public ScanDatas(int operType, String operBody) {
        this.operType = operType;
        this.operBody = operBody;
    }

    public int getOperType() {
        return operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
    }

    public String getOperBody() {
        return operBody;
    }

    public void setOperBody(String operBody) {
        this.operBody = operBody;
    }
}
