package com.preetTractor.galaxyAndroid.data.UserAccessManagementModel;

import java.util.List;

public class UserAccessData {
    private int id;
    private int client_id;
    private int application_id;
    private int module_id;
    private String module_name;
    private List<FieldData> data;
    private List<DynamicData> dynamic_data;

    public int getId() { return id; }
    public int getClient_id() { return client_id; }
    public int getApplication_id() { return application_id; }
    public int getModule_id() { return module_id; }
    public String getModule_name() { return module_name; }
    public List<FieldData> getData() { return data; }
    public List<DynamicData> getDynamic_data() { return dynamic_data; }
}

