package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.content.Intent;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainPagePresenter implements MainPageContract.Presenter{
    private MainPageContract.View mView;
    private MainPageContract.Interactor mInteractor = new MainPageInteractor(this);

    public MainPagePresenter() {}

    @Override
    public void attachView(MainPageContract.View view) {
        mView = view;
    }

    @Override
    public void detatchView() {
        mView = null;
    }

    @Override
    public void onSpotifyLogIn() {
        mInteractor.spotifyLogIn(mView);
    }

    @Override
    public void onSpotifyLoginReceived(int resultCode, Intent intent) {
        mInteractor.verifySpotifyLogin(mView, resultCode, intent);
    }

    @Override
    public void onPlayTrack() {
        mInteractor.playRandomTrack(this);
    }

    public void onUpdateBpm(int bpm) {
        mInteractor.updateValidTracks(bpm);
    }

    @Override
    public void onDisplayErrorToast(String errorMsg) {
        mView.displayErrorToast(errorMsg);
    }

    @Override
    public void onStart() {
        mInteractor.start();
    }

    @Override
    public void onStop() {
        mInteractor.stop();
    }

    @Override
    public void updateAccelerometerGraph(float timestamp, float movingAverage) {
        mView.updateGraph(timestamp, movingAverage);
    }
}

