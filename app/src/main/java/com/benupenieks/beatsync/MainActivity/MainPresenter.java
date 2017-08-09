package com.benupenieks.beatsync.MainActivity;

import android.support.v4.app.Fragment;

/**
 * Created by Ben on 2017-08-05.
 */

public class MainPresenter implements MainContract.Presenter {

    AccelerometerInteractor mAccelerometer = new AccelerometerInteractor();
    MainContract.View mView;

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

    public void onPause() {}
}
