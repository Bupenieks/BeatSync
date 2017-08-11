package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.content.Intent;
import android.text.TextUtils;

import com.benupenieks.beatsync.SpotifyController;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainPagePresenter implements MainPageContract.Presenter{
    private MainPageContract.View mView;
    private MainPageContract.Interactor mInteractor = new MainPageInteractor(this);

    private static final int MAX_BPM = 300;

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
    public void onPlayButtonPress(String bpmContents, int currentBpm, boolean state) {
        if (state) {
            if (!TextUtils.isEmpty(bpmContents)) {
                int bpm = Integer.parseInt(bpmContents);
                // Todo detect playlist change
                if (bpm != currentBpm && bpm > 0) {
                    mView.setCurrentBpm(bpm);
                    mInteractor.updateValidTracks(bpm);
                }
            } else {
                mView.displayErrorToast("Enter a BPM");
                mView.setPlayButtonState(!state);
                return;
            }

            if (currentBpm > 0 && currentBpm < MAX_BPM) {
                mInteractor.trackInteraction(SpotifyController.Interaction.PLAY);
            } else {
                mView.displayErrorToast("Enter a valid BPM");
                mView.setPlayButtonState(!state);
            }
        } else {
            mInteractor.trackInteraction(SpotifyController.Interaction.PAUSE);
        }
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

    @Override
    public void onError(SpotifyController.Interaction interaction) {
        switch (interaction) {
            case PLAY:
                mView.displayErrorToast("Could not play track");
                break;
            case PAUSE:
                mView.displayErrorToast("Could not pause track");
                break;
            case NEXT_TRACK:
                mView.displayErrorToast("Could not play next track");
                break;
            case PREVIOUS_TRACK:
                mView.displayErrorToast("No previous track");
                break;
            case INVALID:
                mView.displayErrorToast("Invalid interaction request");
                break;
            default:
                mView.displayErrorToast("Unknown error");
        }
    }
}

