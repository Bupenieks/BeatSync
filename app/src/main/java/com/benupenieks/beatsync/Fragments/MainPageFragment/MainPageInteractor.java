package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;


import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.MainActivity.AccelerometerInteractor;
import com.benupenieks.beatsync.SpotifyController;
import com.benupenieks.beatsync.Track;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.security.AccessController.getContext;

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
    public void playRandomTrack(MainPageContract.Presenter listener) {
        Random rand = new Random();
        int size = mValidTracks.size();
        if (size > 0) {
            int index = rand.nextInt(size);
            mSpotify.playTrack(mValidTracks.get(index));
        } else {
            listener.onDisplayErrorToast("No songs sync with that BPM.");
        }
    }

    @Override
    public void updateValidTracks(int bpm) {
        mValidTracks.clear();
        Map<Integer, ArrayList<Track>> BpmTrackMap = mSpotify.getBpmTrackMap();
        for (int i = bpm; i < MAX_BPM; i+=i) {
            for (int j = i - mBufferRange; j < i + mBufferRange || j == i; j++) {
                if (j <= 0) { continue; }
                ArrayList<Track> tracks = BpmTrackMap.get(j);
                if (tracks != null) {
                    mValidTracks.addAll(tracks);
                }
            }
        }
    }

    @Subscribe
    public void onAccelerometerDataEvent(AccelerometerInteractor.AccelerometerDataEvent event) {
        mListener.updateAccelerometerGraph(((float) event.mTimeStamp) / 1000000.f, event.mMovingAverage);
    }
}
