package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainPresenter implements MainContract.Presenter{
    private MainContract.View mView;
    private MainContract.Interactor mInteractor;

    public MainPresenter() {}

    public void attachView(MainContract.View view) {
        mView = view;
    }

    public void detatchView() {
        mView = null;
    }

    public void onSpotifyLogIn() {
        mInteractor.spotifyLogIn(mView);
    }

    public void onSpotifyLoginReceived(int resultCode, Intent intent) {
        mInteractor.verifySpotifyLogin(mView, resultCode, intent);
    }
}
