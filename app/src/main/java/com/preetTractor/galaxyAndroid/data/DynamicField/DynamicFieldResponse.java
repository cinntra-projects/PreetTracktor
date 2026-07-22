package com.preetTractor.galaxyAndroid.data.DynamicField;

import java.util.ArrayList;

public class DynamicFieldResponse{
    public String message;
    public int status;
    public ArrayList<DynamicFieldResponseData> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<DynamicFieldResponseData> getData() {
        return data;
    }

    public void setData(ArrayList<DynamicFieldResponseData> data) {
        this.data = data;
    }
}

