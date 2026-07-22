package com.preetTractor.galaxyAndroid.data;

import java.util.ArrayList;

public class DynamicFieldsListModelClass {
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

    public ArrayList<DynamicFieldsKeysResponse> getData() {
        return data;
    }

    public void setData(ArrayList<DynamicFieldsKeysResponse> data) {
        this.data = data;
    }

    public String message;
    public int status;
    public ArrayList<DynamicFieldsKeysResponse> data;
}


