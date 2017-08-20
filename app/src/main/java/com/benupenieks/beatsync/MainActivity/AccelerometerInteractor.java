package com.benupenieks.beatsync.MainActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 2017-08-05.
 */

public class AccelerometerInteractor implements MainContract.Interactor, SensorEventListener {
    private static final String TAG = "AccelerometerInteractor";

    EventBus mEventBus = EventBus.getDefault();

    public class AccelerometerDataEvent {
        public float mMovingAverage;
        public long mTimeStamp;

        AccelerometerDataEvent(float movingAverage, long timestamp) {
            mMovingAverage = movingAverage;
            mTimeStamp = timestamp;
        }
    }

    private final static int MOVING_AVERAGE_RANGE = 5;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ArrayList<AccelerometerDataEvent> mSensorData = new ArrayList<>();
    private ArrayList<Float> mMovingAverageDataList = new ArrayList<>();

    public void init(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG, "init: ");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: ");
        int dataSize = mMovingAverageDataList.size();
        if (dataSize >= MOVING_AVERAGE_RANGE) {
            mMovingAverageDataList.remove(0);
        }
        mMovingAverageDataList.add(calcNorm(event.values));

        if (dataSize >= MOVING_AVERAGE_RANGE) {
            AccelerometerDataEvent dataEvent
                    = new AccelerometerDataEvent(average(mMovingAverageDataList), event.timestamp);
            mSensorData.add(dataEvent);
            mEventBus.post(dataEvent);
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

    public void resume() {
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void pause() {
        if (mSensorManager != null){
            mSensorManager.unregisterListener(this);
        }
    }


}
