package com.benupenieks.beatsync.PlaylistSelection;

import android.media.SoundPool;

import com.benupenieks.beatsync.SpotifyController;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public class PlaylistInteractor implements PlaylistContract.Interactor {

    private static final SpotifyController mSpotify = SpotifyController.getInstance();

    private List<Playlist> mAllPlaylists;
    private List<Playlist> mSelectedPlaylists;

    public PlaylistInteractor() {
    }

    @Override
    public void initPlaylists(playlistConfigListener listener) {
        mAllPlaylists = mSpotify.getPlaylists();
        listener.onInitSuccess(mAllPlaylists);
    }
}
