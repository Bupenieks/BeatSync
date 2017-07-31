package com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment;

import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionContract;
import com.benupenieks.beatsync.Playlist;
import com.benupenieks.beatsync.SpotifyController;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public class PlaylistSelectionInteractor implements PlaylistSelectionContract.Interactor {

    private static final SpotifyController mSpotify = SpotifyController.getInstance();

    public PlaylistSelectionInteractor() {}

    @Override
    public void initPlaylists(playlistConfigListener listener) {
        listener.onInitSuccess(mSpotify.getAllPlaylists(), mSpotify.getSelectedPlaylists());
    }

    @Override
    public void updateSelectedPlaylists(List<Playlist> selectedPlaylists) {
        mSpotify.setSelectedPlaylists(selectedPlaylists);
    }
}
