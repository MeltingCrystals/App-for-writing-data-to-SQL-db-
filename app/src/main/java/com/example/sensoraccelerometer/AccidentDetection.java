package com.example.sensoraccelerometer;

public class AccidentDetection {
    private static final float ACCELEROMETER_THRESHOLD = 2.0f; // Adjust as needed
    private static final float GYROSCOPE_THRESHOLD = 4.5f; // Adjust as needed
    private static final float RATE_OF_CHANGE_THRESHOLD = 5.0f;
    private static final int LACK_OF_MOVEMENT_TIME = 5000;
    private long lastMovementTime = System.currentTimeMillis();
    private float lastAccMagnitude = 0;
    private float lastGyroMagnitude = 0;
    private boolean isFirstReading = true;

    // For orientation check
    private boolean wasVertical = true; // need to connect to gyroscope values
    public boolean detectCrash(float avgAx, float avgAy, float avgAz, float avgMx, float avgMy, float avgMz) {
        float magAcc = magnitude(avgAx, avgAy, avgAz);
        float magGyro = magnitude(avgMx, avgMy, avgMz);

        // Check Rate of Change of Acceleration - monitor the derivative of accelerometer data
        //at first reading the initial value of lastAccMagnitude is zero, which might cause a false positive, so check it
        if (!isFirstReading && Math.abs(magAcc - lastAccMagnitude) > RATE_OF_CHANGE_THRESHOLD) {
            System.out.println("ALARM: Rate of Change of Acceleration");
            return true;
        }
        lastAccMagnitude = magAcc;
        isFirstReading = false;

        // Prolonged Lack of Movement can indicate that the bicycle has fallen
        if (magAcc < 1) {  // Threshold for movement, adjust to experiment
            if (System.currentTimeMillis() - lastMovementTime > LACK_OF_MOVEMENT_TIME) {
                System.out.println("ALARM: Prolonged Lack of Movement");
                return true;
            }
        } else {
//            System.out.println("nothing serious");
            lastMovementTime = System.currentTimeMillis();
        }

        // Orientation Change (Assuming upright vertical is the common position for the device when cycling)
        if (magGyro > 1) {
            System.out.println("ALARM Orientation Change");
            return true;
        }

        // Primary accelerometer and gyroscope detection
        if (magAcc > ACCELEROMETER_THRESHOLD && magGyro > GYROSCOPE_THRESHOLD) {
            System.out.println("ALARM Primary detection");
            return true;
        }
        return false;
    }
    private float magnitude(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

}
