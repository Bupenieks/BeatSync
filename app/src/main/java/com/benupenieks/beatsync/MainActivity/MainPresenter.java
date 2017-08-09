package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Ben on 2017-08-05.
 */

public class MainPresenter implements MainContract.Presenter {

    MainContract.View mView;
    AccelerometerInteractor mAccelerometer = new AccelerometerInteractor();

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
        mAccelerometer.init((Context) activity);
    }
}
