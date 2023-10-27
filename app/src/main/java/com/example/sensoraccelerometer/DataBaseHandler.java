package com.example.sensoraccelerometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {


    public DataBaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    /**
     * create a database
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_SENSOR_DATA_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + " ("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY, "
                + Constants.KEY_AXIS_AX + " REAL, "
                + Constants.KEY_AXIS_AY + " REAL, "
                + Constants.KEY_AXIS_AZ + " REAL, "
                + Constants.KEY_AXIS_GX + " REAL, "
                + Constants.KEY_AXIS_GY + " REAL, "
                + Constants.KEY_AXIS_GZ + " REAL " + " )";

        String create_table = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s REAL , %s REAL, %s REAL, %s REAL, %s REAL, %s REAL)",
                Constants.TABLE_NAME,
                Constants.KEY_ID,
                Constants.KEY_AXIS_AX,
                Constants.KEY_AXIS_AY,
                Constants.KEY_AXIS_AY,
                Constants.KEY_AXIS_GX,
                Constants.KEY_AXIS_GY,
                Constants.KEY_AXIS_GZ);
        sqLiteDatabase.execSQL(CREATE_SENSOR_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * write accelerometer data into the database table
     *
     * @param sensorData
     */
    public void addSensorData(SensorData sensorData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY_AXIS_AX, sensorData.getAx());
        contentValues.put(Constants.KEY_AXIS_AY, sensorData.getAy());
        contentValues.put(Constants.KEY_AXIS_AZ, sensorData.getAz());
        contentValues.put(Constants.KEY_AXIS_GX, sensorData.getGx());
        contentValues.put(Constants.KEY_AXIS_GY, sensorData.getGy());
        contentValues.put(Constants.KEY_AXIS_GZ, sensorData.getGz());


        db.insert(Constants.TABLE_NAME, null, contentValues);
        db.close();
    }

    /**
     * gets sensor data from the DB by Id
     *
     * @param id
     * @return
     */
    public SensorData getSensorData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID, Constants.KEY_AXIS_AX, Constants.KEY_AXIS_AY,
                        Constants.KEY_AXIS_AZ, Constants.KEY_AXIS_GX, Constants.KEY_AXIS_GY, Constants.KEY_AXIS_GZ},
                Constants.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        SensorData accData = new SensorData(Integer.parseInt(cursor.getString(0)),
                Float.parseFloat(cursor.getString(1)),
                Float.parseFloat(cursor.getString(2)),
                Float.parseFloat(cursor.getString(3)),
                Float.parseFloat(cursor.getString(4)),
                Float.parseFloat(cursor.getString(5)),
                Float.parseFloat(cursor.getString(6)));
        return accData;
    }

    /**
     * Return the whole list of Accelerometer and Gyroscope values from the table
     *
     * @return
     */
    public List<SensorData> getAllSensorValues() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<SensorData> sensorValuesList = new ArrayList<>();
        String selectAllValues = "Select * from " + Constants.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAllValues, null);
        if (cursor.moveToFirst()) {
            do {
                SensorData sensorData = new SensorData();
                sensorData.setId(Integer.parseInt(cursor.getString(0)));
                sensorData.setAx(Float.parseFloat(cursor.getString(1)));
                sensorData.setAy(Float.parseFloat(cursor.getString(2)));
                sensorData.setAz(Float.parseFloat(cursor.getString(3)));
                sensorData.setGx(Float.parseFloat(cursor.getString(4)));
                sensorData.setGy(Float.parseFloat(cursor.getString(5)));
                sensorData.setGz(Float.parseFloat(cursor.getString(6)));

                sensorValuesList.add(sensorData);
            } while (cursor.moveToNext());
        }
        return sensorValuesList;
    }

    public void cleanOldData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + Constants.TABLE_NAME;
        db.execSQL(deleteQuery);
    }

    public int getDataCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
