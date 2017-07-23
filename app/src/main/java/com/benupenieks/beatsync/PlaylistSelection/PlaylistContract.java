package com.benupenieks.beatsync.PlaylistSelection;

import com.benupenieks.beatsync.Playlist;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public interface PlaylistContract {
    interface View {
        void attachPresenter();

        void displayPlaylists(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists);
    }

    interface Presenter {
        void attachView(PlaylistContract.View view);

        void detatchView();

        void onInit();

        void setSelectedPlaylists(List<Playlist> selectedPlaylists);

    }

    interface Interactor {

        interface playlistConfigListener {
            void onInitSuccess(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists);

            void onInitError();
        }

        void initPlaylists(playlistConfigListener listener);

        void updateSelectedPlaylists(List<Playlist> selectedPlaylists);
    }
}
