package com.example.sensorDataDB;

public class Constants {

    // Define all required app permissions
    public static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,

    };

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sensorData";
    public static final String TABLE_NAME = "sensorValues";
    public static final String KEY_ID = "id";
    public static final String KEY_AXIS_AX = "AxValue";
    public static final String KEY_AXIS_AY = "AyValue";
    public static final String KEY_AXIS_AZ = "AzValue";
    public static final String KEY_AXIS_GX = "GxValue";
    public static final String KEY_AXIS_GY = "GyValue";
    public static final String KEY_AXIS_GZ = "GzValue";
    public static final String KEY_TIMESTAMP = "timestamp";


}
