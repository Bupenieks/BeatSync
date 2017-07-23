package com.benupenieks.beatsync.PlaylistSelection;

import com.benupenieks.beatsync.Playlist;
import com.benupenieks.beatsync.SpotifyController;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public class PlaylistInteractor implements PlaylistContract.Interactor {

    private static final SpotifyController mSpotify = SpotifyController.getInstance();

    public PlaylistInteractor() {}

    @Override
    public void initPlaylists(playlistConfigListener listener) {
        listener.onInitSuccess(mSpotify.getAllPlaylists(), mSpotify.getSelectedPlaylists());
    }

    @Override
    public void updateSelectedPlaylists(List<Playlist> selectedPlaylists) {
        mSpotify.setSelectedPlaylists(selectedPlaylists);
    }
}
