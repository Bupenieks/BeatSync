package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Ben on 2017-08-05.
 */

public class MainPresenter implements MainContract.Presenter {

    MainContract.View mView;
    AccelerometerInteractor mAccelerometer = new AccelerometerInteractor();

    private final static String TAG = "MainPresenter";

    MainPresenter() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void attachView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }


    public void onResume() {
        mAccelerometer.resume();
    }

    public void onPause() { mAccelerometer.pause(); }

    public void onStart(Activity activity) {
        mAccelerometer.init(activity);
    }

    @Subscribe
    public void initAccelerometer(MainPageFragment.RowingToggleEvent event) {
        switch (event.action) {
            case BEGIN:
                mAccelerometer.beginRowing();
                break;
            case END:

                break;
            default:
                Log.e(TAG, "Invalid Rowing Toggle Event");
        }
    }
}
