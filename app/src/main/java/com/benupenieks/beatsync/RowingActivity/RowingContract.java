package com.benupenieks.beatsync.RowingActivity;

import android.app.Activity;

import com.benupenieks.beatsync.MainActivity.MainContract;

/**
 * Created by Ben on 2017-09-23.
 */

public class RowingContract {
    interface View {
        void attachPresenter();
    }

    interface Presenter {
        void attachView(RowingContract.View view);

        void detachView();

        void onResume();

        void onPause();

        void onStart(Activity activity);
    }

    interface Interactor {
    }
}