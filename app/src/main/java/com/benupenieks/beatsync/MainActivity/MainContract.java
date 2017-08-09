package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Ben on 2017-08-05.
 */

public interface MainContract {

    interface View {
        void attachPresenter();
    }

    interface Presenter {
        void attachView(MainContract.View view);

        void detachView();

        void onResume();

        void onPause();

        void onStart(Activity activity);
    }

    interface Interactor {
    }
}
