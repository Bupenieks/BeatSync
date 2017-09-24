package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.benupenieks.beatsync.SpotifyController;
import com.benupenieks.beatsync.Track;

/**
 * Created by Ben on 2017-07-22.
 */

public interface MainPageContract {
    interface View {
        void displayErrorToast(String errorMsg);

        void displayLowPriorityErrorToast(String errorMsg);

        void updateGraph(float x, float y);

        void setCurrentBpm(int bpm);

        void setPlayButtonState(boolean state);

        int getCurrentBpm();


    }

    interface Presenter {
        void onStart();

        void onStop();

        void attachView(View view);

        void detatchView();

        void onSpotifyLogIn();

        void onSpotifyLoginReceived(int resultCode, Intent intent);

        void onPlayButtonPress(String bpmContents, int currentBpm, boolean state);

        void onForwardButtonPress(int bpm, int currentBpm);

        void onBackButtonPress();

        void onDisplayErrorToast(String errorMsg);

        void updateAccelerometerGraph(float timestamp, float movingAverage);

        void onError(SpotifyController.Interaction interaction);

        void onSuccess(SpotifyController.Interaction interaction);

    }

    interface Interactor{
        void start();

        void stop();

        void spotifyLogIn(View view);

        void verifySpotifyLogin(View view, int resultCode, Intent intent);

        void playRandomTrack();

        void playDifferentTrack(Track trackNotToPlay);

        void updateValidTracks(int bpm);

        void trackInteraction(SpotifyController.Interaction interaction);

        void onError(SpotifyController.Interaction interaction);

        void onError(String message);

        void onSuccessfulInteraction(SpotifyController.Interaction interaction);
    }
}
