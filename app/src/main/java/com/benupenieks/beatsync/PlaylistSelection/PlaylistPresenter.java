package com.benupenieks.beatsync.PlaylistSelection;

import com.benupenieks.beatsync.Playlist;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public class PlaylistPresenter implements
        PlaylistContract.Presenter, PlaylistContract.Interactor.playlistConfigListener {
    private PlaylistContract.View mView;
    private PlaylistContract.Interactor mInteractor = new PlaylistInteractor();

    public PlaylistPresenter() {}

    @Override
    public void attachView(PlaylistContract.View view) {
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
}
