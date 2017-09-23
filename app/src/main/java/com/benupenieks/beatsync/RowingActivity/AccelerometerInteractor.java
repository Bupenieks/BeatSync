package com.benupenieks.beatsync.RowingActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.MainActivity.MainContract;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static java.security.AccessController.getContext;

/**
 * Created by Ben on 2017-08-05.
 */

public class AccelerometerInteractor implements RowingContract.Interactor, SensorEventListener {
    private static final String TAG = "AccelerometerInteractor";

    Timer mTimer = new Timer();
    RowingContract.Presenter mListener = null;

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
    private ArrayList<Long> mStrokeTimes = new ArrayList<>();
    private boolean mBeginSynchronizing = false;
    private boolean mBeginReading = false;
    private float mAccelerationAverage = 0;


    public void init(Context context, RowingContract.Presenter listener) {
        if (mListener == null) {
            mListener = listener;
        }
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        beginRowing();
       // mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "init: ");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        //Log.d(TAG, "onSensorChanged: ");
        int dataSize = mMovingAverageDataList.size();
        if (dataSize >= MOVING_AVERAGE_RANGE) {
            mMovingAverageDataList.remove(0);
        }
        mMovingAverageDataList.add(calcNorm(event.values));

        if (dataSize >= MOVING_AVERAGE_RANGE) {
            AccelerometerDataEvent dataEvent
                    = new AccelerometerDataEvent(average(mMovingAverageDataList), event.timestamp);
            mListener.onNewAccelerometerData(dataEvent);
            if (mBeginReading) {
                if (mBeginSynchronizing) {
                    if (mStrokeTimes.isEmpty()) {
                        mStrokeTimes.add(dataEvent.mTimeStamp / 1000000);
                        Log.d(TAG, String.format("Added %d ms to mStrokeTimes", dataEvent.mTimeStamp / 1000000));
                    } else {
                        if (dataEvent.mMovingAverage > mAccelerationAverage
                                && mSensorData.get(mSensorData.size() - 1).mMovingAverage < mAccelerationAverage) {
                            long timeDifferenceMs = (dataEvent.mTimeStamp / 1000000 - mStrokeTimes.get(mStrokeTimes.size() - 1)); // Nano -> Milli
                            Log.d(TAG, " " + timeDifferenceMs + " : " + dataEvent.mTimeStamp / 1000000 + " : " + mStrokeTimes.get(mStrokeTimes.size() - 1));
                            mStrokeTimes.add(dataEvent.mTimeStamp / 1000000);
                            Log.d(TAG, String.format("Added %d ms to mStrokeTimes", (dataEvent.mTimeStamp / 1000000)));
                        }
                    }
                }
                mSensorData.add(dataEvent);
                float allDataSize = (float) mSensorData.size();
                mAccelerationAverage = ((mAccelerationAverage * ((allDataSize - 1) / allDataSize)) + (dataEvent.mMovingAverage / allDataSize));
            }
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
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    private int calculateStrokeRate() {
        int average = 0;
        for (int i = 1; i < mStrokeTimes.size(); i++) {
            int strokeTime = (int)(mStrokeTimes.get(i) - mStrokeTimes.get(i - 1));
            average = average * (i - 1) / i + strokeTime / i;
        }
        return 60000 / average;
    }

    public void beginRowing() {
        resume();
        Log.d(TAG, "START TIMER");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    mBeginReading = true;
                Log.d(TAG, "Beginning to read sensor data");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Beginning to process sensor data");

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int strokeRate = calculateStrokeRate();
                                Log.d(TAG, String.format("Finished analysis. Average stroke rate: %d", strokeRate));
                                mSensorManager.unregisterListener(AccelerometerInteractor.this);
                                mListener.onRowingComplete(strokeRate);
                            }
                        }, 6000);
                        mBeginSynchronizing = true;
                    }
                }, 2000);
            }
        }, 3000);
    }

    public void stopRowing() {}


}
