package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;


import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.RowingActivity.AccelerometerInteractor;
import com.benupenieks.beatsync.Playlist;
import com.benupenieks.beatsync.SpotifyController;
import com.benupenieks.beatsync.Track;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainPageInteractor implements MainPageContract.Interactor {
    private MainPageContract.Presenter mListener;

    private SpotifyController mSpotify = SpotifyController.getInstance();

    public MainPageInteractor(MainPageContract.Presenter listener) {
        mListener = listener;
    }

    private static final int MAX_BPM = 300;
    private static final String TAG = "MainPageInteractor";

    private List<Track> mValidTracks = new ArrayList<>();
    private int mBufferRange = 0;

    @Override
    public void start() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void stop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void spotifyLogIn(MainPageContract.View view) {
        if (mSpotify.getUserId() == null) {
            mSpotify.logIn((Activity) view);
        }
    }

    @Override
    public void verifySpotifyLogin(MainPageContract.View view, int resultCode, Intent intent) {
        mSpotify.verifyLogIn((PlaylistSelectionFragment) view, (Activity) view, resultCode, intent);
    }

    @Override
    public void playRandomTrack() {
        Random rand = new Random();
        int size = mValidTracks.size();
        if (size > 0) {
            int index = rand.nextInt(size);
            mSpotify.playTrack(mValidTracks.get(index), this);
        } else {
            mListener.onDisplayErrorToast("No songs sync with that BPM.");
            mListener.onError(SpotifyController.Interaction.PLAY_NEW);
        }
    }

    @Override
    public void playDifferentTrack(Track trackNotToPlay) {
        for (Track track : mValidTracks) {
            if (track != trackNotToPlay) {
                mSpotify.playTrack(track, this);
                return;
            }
        }
        mSpotify.playTrack(trackNotToPlay);
    }

    @Override
    public void updateValidTracks(int bpm) {
        if (bpm == 0) return;
        mValidTracks.clear();
        Map<Integer, ArrayList<Track>> BpmTrackMap = mSpotify.getBpmTrackMap();
        List<Playlist> selectedPlaylists = mSpotify.getSelectedPlaylists();
        Log.d(TAG, selectedPlaylists.toString());
        for (int i = bpm; i < MAX_BPM; i+=bpm) {
            for (int j = i - mBufferRange; j < i + mBufferRange || j == i; j++) {
                if (j <= 0) { continue; }
                ArrayList<Track> tracks = BpmTrackMap.get(j);
                if (tracks == null) { continue; }
                for (Track track : tracks) {
                    if (selectedPlaylists.contains(track.getParentPlaylist())) {
                        mValidTracks.add(track);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onAccelerometerDataEvent(AccelerometerInteractor.AccelerometerDataEvent event) {
        mListener.updateAccelerometerGraph(((float) event.mTimeStamp) / 1000000.f, event.mMovingAverage);
    }

    @Override
    public void trackInteraction(SpotifyController.Interaction interaction) {
        mSpotify.trackInteraction(interaction, this);
    }

    @Override
    public void onError(SpotifyController.Interaction interaction) {
        mListener.onError(interaction);
    }

    @Override
    public void onSuccessfulInteraction(SpotifyController.Interaction interaction) {
        mListener.onSuccess(interaction);
    }
}
