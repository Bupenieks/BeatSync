package com.benupenieks.beatsync.MainActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by Ben on 2017-08-05.
 */

public class AccelerometerInteractor implements MainContract.Interactor, SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ArrayList<Integer[]> mSensorData;

    public void init(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
    }

    public void resume() {}


}
