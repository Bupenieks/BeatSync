package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.content.Intent;
import android.text.TextUtils;

import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionPresenter;
import com.benupenieks.beatsync.SpotifyController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static android.os.SystemClock.sleep;
import static com.benupenieks.beatsync.SpotifyController.Interaction.PAUSE;
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
                    interaction = PLAY_NEW;
                }
                mView.setCurrentBpm(bpm);
                mInteractor.updateValidTracks(bpm);

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
            mInteractor.trackInteraction(PAUSE);
        }
    }

    @Override
    public void onForwardButtonPress(int bpm, int currentBpm) {
        mInteractor.updateValidTracks(bpm);
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
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
                mView.displayLowPriorityErrorToast("Could not play track");
                mView.setPlayButtonState(false);
                mInteractor.trackInteraction(PAUSE);
                break;
            case PAUSE:
                mView.displayLowPriorityErrorToast("Could not pause track");
                mView.setPlayButtonState(true);
                break;
            case NEXT_TRACK:
                mView.displayLowPriorityErrorToast("Could not play next track");
                break;
            case PREVIOUS_TRACK:
                mView.displayLowPriorityErrorToast("No previous track");
                break;
            case INVALID:
                mView.displayLowPriorityErrorToast("Invalid interaction request");
                break;
            default:
                mView.displayLowPriorityErrorToast("Unknown error");
        }
    }

    @Override
    public void onSuccess(SpotifyController.Interaction interaction) {
        switch (interaction) {
            case NEXT_TRACK:
            case PLAY_NEW:
            case RESUME:
            case PREVIOUS_TRACK:
            case FORCE_PLAY:
                mView.setPlayButtonState(true);
                break;
            case PAUSE:
                mView.setPlayButtonState(false);
                break;
            default:
                mView.displayErrorToast("Invalid success received");
        }
    }

    @Subscribe
    public void onPlaylistSelectionEvent(PlaylistSelectionPresenter.PlaylistSelectionEvent event) {
        mInteractor.updateValidTracks(mView.getCurrentBpm());
    }
}

