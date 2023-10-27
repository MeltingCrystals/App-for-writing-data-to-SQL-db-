package com.example.sensoraccelerometer;

// Define a class to store all the constants
public class Constants {

    // Define all required app permissions
    public static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_CONTACTS,
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


    // Define a constant string for the shared preferences name
    public static final String MyPREFERENCES = "MyPrefs";

    // Define eCall delays (30s, 60s or 90 seconds )
    public static final long[] delayValues={90000,60000,30000};

    // Define eCall delays options
    public static final String[] delayTextValues = {
            "90sec",
            "60sec",
            "30sec"
    };

    // Define vehicle types
    public static final String[] vehicleTypes={"Bicycle","eBike"};

    // Define the TTS "fall detected" message
    public static final String TTS_FALL_DETECTED_MESSAGE = "A fall has been detected. Please cancel the emergency alert if you are OK";

    // Define the TTS "eCall will be initiated" message
    public static final String TTS_ECALL_INITIATED_MESSAGE = "Emergency alert will be initiated in";

    // Define maximum number of call retries
    public static final int MAX_CALL_RETRIES = 3;


    // ------------------ Constants for the fall detection algorithm ------------------
    // Minimal value for detection of the "Free fall" phase
    public static final double MIN_OUTLIER_VALUE = 5.0;

    // Maximal value for detection of the "Impact" phase
    public static final double MAX_OUTLIER_VALUE = 26.5;

}
