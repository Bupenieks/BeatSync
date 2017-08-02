package com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment;

import com.benupenieks.beatsync.Playlist;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public class PlaylistSelectionPresenter implements
        PlaylistSelectionContract.Presenter, PlaylistSelectionContract.Interactor.playlistConfigListener {
    private PlaylistSelectionContract.View mView;
    private PlaylistSelectionContract.Interactor mInteractor = new PlaylistSelectionInteractor();

    public PlaylistSelectionPresenter() {}

    @Override
    public void attachView(PlaylistSelectionContract.View view) {
        mView = view;
    }

    @Override
    public void detatchView() {
        mView = null;
    }

    @Override
    public void onInit() {
        mInteractor.initPlaylists(this);
    }

    @Override
    public void onInitSuccess(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists) {
        mView.displayPlaylists(allPlaylists, selectedPlaylists);
    }

    @Override
    public void onInitError() {
    }

    @Override
    public void setSelectedPlaylists(List<Playlist> selectedPlaylists) {
        mInteractor.updateSelectedPlaylists(selectedPlaylists);
    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {
        mInteractor.addNewSelectedPlaylist(playlist);
    }

    @Override
    public void onPlaylistDeselected(Playlist playlist) {
        mInteractor.removeSelectedPlaylist(playlist);
    }

}
