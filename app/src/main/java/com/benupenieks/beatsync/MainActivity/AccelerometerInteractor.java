package com.benupenieks.beatsync.MainActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Ben on 2017-08-05.
 */

public class AccelerometerInteractor implements MainContract.Interactor, SensorEventListener {

    private class AccelerometerData {
        private float mMovingAverage;
        private long mTimeStamp;

        AccelerometerData(float movingAverage, long timestamp) {
            mMovingAverage = movingAverage;
            mTimeStamp = timestamp;
        }

    }

    private final static int MOVING_AVERAGE_RANGE = 3;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ArrayList<AccelerometerData> mSensorData;
    private ArrayList<Float> mMovingAverageDataList;

    public void init(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        int dataSize = mMovingAverageDataList.size();
        if (dataSize >= MOVING_AVERAGE_RANGE) {
            mMovingAverageDataList.remove(0);
        }
        mMovingAverageDataList.add(calcNorm(event.values));

        if (dataSize >= MOVING_AVERAGE_RANGE) {
            mSensorData.add(
                    new AccelerometerData(average(mMovingAverageDataList), event.timestamp)
            );
        }
    }

    private float average(ArrayList<Float> data) {
        float avg = 0;
        float length = (float) data.size();
        for (float val : data) {
            avg += val / length;
        }
        return avg;
    }

    private float calcNorm(float[] data) {
        // Protects against overflow
        float max = 0;
        for (float val : data) {
            float absVal = Math.abs(val);
            if (absVal > max) {
                max = absVal;
            }
        }
        float sum = 0;
        for (float val : data) {
            sum += (val / max) * (val / max);
        }

        return max * (float) Math.sqrt(sum);
    }

    public void resume() {}


}
