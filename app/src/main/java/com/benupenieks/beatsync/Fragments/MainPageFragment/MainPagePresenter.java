package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.content.Intent;
import android.text.TextUtils;

import com.benupenieks.beatsync.SpotifyController;

import static android.os.SystemClock.sleep;
import static com.benupenieks.beatsync.SpotifyController.Interaction.PLAY_NEW;
import static com.benupenieks.beatsync.SpotifyController.Interaction.RESUME;

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
            SpotifyController.Interaction interaction = RESUME;
            if (!TextUtils.isEmpty(bpmContents)) {
                int bpm = Integer.parseInt(bpmContents);
                // Todo detect playlist change
                if (bpm != currentBpm && bpm > 0) {
                    mView.setCurrentBpm(bpm);
                    mInteractor.updateValidTracks(bpm);
                    interaction = PLAY_NEW;
                }

                if (bpm > 0 && bpm < MAX_BPM) {
                    mInteractor.trackInteraction(interaction);
                } else {
                    mView.displayErrorToast("Enter a valid BPM");
                    mView.setPlayButtonState(!state);
                }
            } else {
                mView.displayErrorToast("Enter a BPM");
                mView.setPlayButtonState(!state);
            }
        } else {
            mInteractor.trackInteraction(SpotifyController.Interaction.PAUSE);
        }
    }

    @Override
    public void onForwardButtonPress() {
        mInteractor.trackInteraction(SpotifyController.Interaction.NEXT_TRACK);
    }

    @Override
    public void onBackButtonPress() {
        mInteractor.trackInteraction(SpotifyController.Interaction.PREVIOUS_TRACK);
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
            case PLAY_NEW:
                mView.displayErrorToast("Could not play track");
                mView.setPlayButtonState(false);
                break;
            case PAUSE:
                mView.displayErrorToast("Could not pause track");
                mView.setPlayButtonState(true);
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

    @Override
    public void onSuccess(SpotifyController.Interaction interaction) {
        switch (interaction) {
            case PLAY_NEW:
            case NEXT_TRACK:
            case PREVIOUS_TRACK:
                mView.setPlayButtonState(true);
                break;
            case PAUSE:
                mView.setPlayButtonState(false);
                break;
            default:
                mView.displayErrorToast("Invalid success received");
        }
    }
}

