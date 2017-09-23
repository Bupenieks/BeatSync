package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.util.Log;

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.RowingActivity.AccelerometerInteractor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Ben on 2017-08-05.
 */

public class MainPresenter implements MainContract.Presenter {

    MainContract.View mView;
    AccelerometerInteractor mAccelerometer = new AccelerometerInteractor();

    private final static String TAG = "MainPresenter";

    @Override
    public void attachView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }


    public void onResume() {
    }

    public void onPause() {  }

    public void onStart(Activity activity) {
    }

}
