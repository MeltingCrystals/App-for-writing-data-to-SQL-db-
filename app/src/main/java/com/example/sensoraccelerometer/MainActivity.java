package com.example.sensoraccelerometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public SensorData sensorDataBuf = new SensorData();
    DataBaseHandler dataBaseHandler = new DataBaseHandler(this);

    public int sensorDataId = 0; // to update the sensorDataBuf for database
    private TextView textViewA;
    private TextView textViewG;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    Button writeCsv;

    private static final int WINDOW_SIZE = 3;
    private float[] recentAx = new float[WINDOW_SIZE];
    private float[] recentAy = new float[WINDOW_SIZE];
    private float[] recentAz = new float[WINDOW_SIZE];
    private int index = 0;
    private float[] recentGx = new float[WINDOW_SIZE];
    private float[] recentGy = new float[WINDOW_SIZE];
    private float[] recentGz = new float[WINDOW_SIZE];

    private AccidentDetection accidentDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewA = findViewById(R.id.text_accelerometer);
        textViewG = findViewById(R.id.text_gyroscope);

        writeCsv = findViewById(R.id.button_write);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(MainActivity.this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);

        // test read from db
        List<SensorData> sensorDataList = dataBaseHandler.getAllSensorValues();
        for(SensorData sensorDtemp : sensorDataList) {
            Log.d("TempInfo ", "ID: " + sensorDtemp.getId() + ", " + sensorDtemp.getAx() +", " +
                    sensorDtemp.getAy() + ", " + sensorDtemp.getAz()+ ", " +
                    sensorDtemp.getGx()+ ", " + sensorDtemp.getGy()+ ", " + sensorDtemp.getGz());
        }
        dataBaseHandler.addSensorData(sensorDataBuf);

        accidentDetection = new AccidentDetection();

        requestPermissions();

        Button exportButton = findViewById(R.id.button_write);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeDataToCsv();
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //add new values to the recent arrays and increment index
            recentAx[index] = event.values[0];
            recentAy[index] = event.values[1];
            recentAz[index] = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            recentGx[index] = event.values[0];
            recentGy[index] = event.values[1];
            recentGz[index] = event.values[2];
        }

        //implement a circular buffer by keeping the index within the range [0, WINDOW_SIZE-1]
        index = (index + 1) % WINDOW_SIZE;

        //Calculate the moving averages
        float avgAx = average(recentAx);
        float avgAy = average(recentAy);
        float avgAz = average(recentAz);

        float avgGx = average(recentGx);
        float avgGy = average(recentGy);
        float avgGz = average(recentGz);

        // set global buffer with last data
        sensorDataBuf.setId(sensorDataId);
        sensorDataBuf.setAx(avgAx);
        sensorDataBuf.setAy(avgAy);
        sensorDataBuf.setAz(avgAz);
        sensorDataBuf.setGx(avgGx);
        sensorDataBuf.setGy(avgGy);
        sensorDataBuf.setGz(avgGz);

        dataBaseHandler.addSensorData(sensorDataBuf);
        checkAndCleanDatabase();
        sensorDataId++;

        //TODO
        boolean isAccidentDetected = accidentDetection.detectCrash(avgAx, avgAy, avgAz, avgGx, avgGy, avgGz);


        //display the moving averages
        textViewA.setText("Accelerometer data" + "\n" + avgAx + "\n" + avgAy + "\n" + avgAz + "\n" + "\n");
        textViewG.setText("Gyroscope data" + "\n" + avgGx + "\n" + avgGy + "\n" + avgGz);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private float average(float[] values) {
        float sum = 0;
        for(float v : values) {
            sum += v;
        }
        return sum/values.length;
    }

    public void requestPermissions(){
        // Create a list of permissions to request
        List<String> permissionsToRequest = new ArrayList<>();

        // Loop through all the permissions and check if they are granted
        for (String permission : Constants.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // If the permission is not granted, add it to the list of permissions to request
                permissionsToRequest.add(permission);
            }
        }

        // If the list of permissions to request is not empty, request the permissions
        if (!permissionsToRequest.isEmpty()) {

            // Request the permissions
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    100
            );
        }
    }
    public void checkAndCleanDatabase() {
        int count = dataBaseHandler.getDataCount();
        if (count > 6000) {
            dataBaseHandler.cleanOldData();
        }
    }

    public void writeDataToCsv () {
        SQLiteDatabase db = dataBaseHandler.getReadableDatabase();

        //Select all the data from the dataBase
        //String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        String selectQuery = "SELECT * FROM sensorValues";

        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
                File appSpecificExternalDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File myTextFile = new File(appSpecificExternalDir, "sensorValues.csv");
                FileWriter fileWriter = new FileWriter(myTextFile);
                if (!appSpecificExternalDir.exists()) {
                    appSpecificExternalDir.mkdirs();
                }

                // Write header if needed
                fileWriter.append("ID,AX,AY,AZ,GX,GY,GZ\n");

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);  // assuming ID is at column index 0
                    float ax = cursor.getFloat(1);  // assuming Ax is at column index 1
                    float ay = cursor.getFloat(2);  // assuming Ay is at column index 2
                    float az = cursor.getFloat(3);  // assuming Az is at column index 3
                    float gx = cursor.getFloat(4);  // assuming Gx is at column index 4
                    float gy = cursor.getFloat(5);  // assuming Gy is at column index 5
                    float gz = cursor.getFloat(6);  // assuming Gz is at column index 6

                    fileWriter.append(String.valueOf(id));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(ax));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(ay));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(az));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(gx));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(gy));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(gz));
                    fileWriter.append("\n");
                }
                fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    }

