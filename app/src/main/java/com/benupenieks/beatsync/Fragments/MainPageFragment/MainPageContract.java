package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ben on 2017-07-22.
 */

public interface MainPageContract {
    interface View {
        void displayErrorToast(String errorMsg);

        void updateGraph(float x, float y);
    }

    interface Presenter {
        void onStart();

        void onStop();

        void attachView(View view);

        void detatchView();

        void onSpotifyLogIn();

        void onSpotifyLoginReceived(int resultCode, Intent intent);

        void onPlayTrack();

        void onUpdateBpm(int bpm);

        void onDisplayErrorToast(String errorMsg);

        void updateAccelerometerGraph(float timestamp, float movingAverage);

    }

    interface Interactor{
        void start();

        void stop();

        void spotifyLogIn(View view);

        void verifySpotifyLogin(View view, int resultCode, Intent intent);

        void playRandomTrack(MainPageContract.Presenter listener);

        void updateValidTracks(int bpm);
    }
}
