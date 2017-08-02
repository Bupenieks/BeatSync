package com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment;

import com.benupenieks.beatsync.Playlist;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public interface PlaylistSelectionContract {
    interface View {
        void displayPlaylists(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists);
    }

    interface Presenter {
        void attachView(PlaylistSelectionContract.View view);

        void detatchView();

        void onInit();

        void setSelectedPlaylists(List<Playlist> selectedPlaylists);

        void onPlaylistSelected(Playlist playlist);

        void onPlaylistDeselected(Playlist playlist);

    }

    interface Interactor {

        interface playlistConfigListener {
            void onInitSuccess(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists);

            void onInitError();
        }

        void initPlaylists(playlistConfigListener listener);

        void updateSelectedPlaylists(List<Playlist> selectedPlaylists);

        void addNewSelectedPlaylist(Playlist playlist);

        void removeSelectedPlaylist(Playlist playlist);
    }
}
