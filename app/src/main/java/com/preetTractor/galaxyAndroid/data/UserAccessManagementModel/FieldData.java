package com.preetTractor.galaxyAndroid.data.UserAccessManagementModel;

public class FieldData {
    private String key;
    private String label;
    private boolean is_mandatory;
    private boolean is_workflow;
    private boolean is_key_mandatory;
    private boolean is_extra_field;
    private boolean is_dynamic;
    private int id;

    public String getKey() { return key; }
    public String getLabel() { return label; }
    public boolean isMandatory() { return is_mandatory; }
    public boolean isWorkflow() { return is_workflow; }
    public boolean isKeyMandatory() { return is_key_mandatory; }
    public boolean isExtraField() { return is_extra_field; }
    public boolean isDynamic() { return is_dynamic; }
    public int getId() { return id; }
}
