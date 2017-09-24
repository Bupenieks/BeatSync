package com.benupenieks.beatsync.RowingActivity;

import android.app.Activity;

import com.benupenieks.beatsync.MainActivity.MainContract;

/**
 * Created by Ben on 2017-09-23.
 */

public class RowingContract {
    interface View {
        void attachPresenter();

        void updateGraph(float x, float y);

        void finish(int strokeRate);
    }

    interface Presenter {
        void attachView(RowingContract.View view);

        void detachView();

        void onResume();

        void onPause();

        void onStart(Activity activity);

        void onStop();

        void onNewAccelerometerData(AccelerometerInteractor.AccelerometerDataEvent data);

        void onRowingComplete(int strokeRate);
    }

    interface Interactor {
    }
}