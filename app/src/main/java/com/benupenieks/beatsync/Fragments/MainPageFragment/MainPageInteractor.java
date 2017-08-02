package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Intent;


import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.SpotifyController;
import com.benupenieks.beatsync.Track;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainPageInteractor implements MainPageContract.Interactor {
    private SpotifyController mSpotify = SpotifyController.getInstance();

    public MainPageInteractor() {}

    private static final int MAX_BPM = 300;

    private List<Track> mValidTracks = new ArrayList<>();

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
        int index = rand.nextInt(mValidTracks.size());
        mSpotify.playTrack(mValidTracks.get(index));
    }

    @Override
    public void updateValidTracks(int bpm) {
        Map<Integer, ArrayList<Track>> BpmTrackMap = mSpotify.getBpmTrackMap();
        for (int i = bpm; i < MAX_BPM; i*=2) {
            ArrayList<Track> tracks = BpmTrackMap.get(i);
            if (tracks != null) {
                mValidTracks.addAll(tracks);
            }
        }
    }
}
