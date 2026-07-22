package com.preetTractor.galaxyAndroid.data.UserAccessManagementModel;

import java.util.List;

public class UserAccessManagementResponse {
    private int status;
    private String message;
    private List<UserAccessData> data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public List<UserAccessData> getData() { return data; }
}
