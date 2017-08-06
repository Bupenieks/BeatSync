package com.benupenieks.beatsync.MainActivity;

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

    }

    interface Interactor {
    }
}
