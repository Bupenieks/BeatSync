package com.benupenieks.beatsync.RowingActivity;

import android.app.Activity;
import android.util.Log;

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.MainActivity.MainContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Ben on 2017-09-23.
 */

public class RowingPresenter implements RowingContract.Presenter {

    RowingContract.View mView;
    AccelerometerInteractor mAccelerometer = new AccelerometerInteractor();

    private final static String TAG = "RowingPresenter";
    @Override
    public void attachView(RowingContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }


    public void onResume() {
        mAccelerometer.resume();
    }

    public void onPause() {
        mAccelerometer.pause();
    }

    public void onStart(Activity activity) {
        mAccelerometer.init(activity, this);
    }

    public void onNewAccelerometerData(AccelerometerInteractor.AccelerometerDataEvent data) {
        mView.updateGraph((float) data.mTimeStamp, data.mMovingAverage);
    }

    public void onRowingComplete(int strokeRate) {
        mView.finish(strokeRate);
    }

    /*
    @Subscribe
    public void initAccelerometer(RowingActivity.RowingToggleEvent event) {
        switch (event.action) {
            case BEGIN:
                mAccelerometer.beginRowing();
                break;
            case END:

                break;
            default:
                Log.e(TAG, "Invalid Rowing Toggle Event");
        }
    } */
}
