package com.benupenieks.beatsync.PlaylistSelection;

import android.content.Intent;

import com.benupenieks.beatsync.MainActivity.MainContract;

import java.util.List;

/**
 * Created by Ben on 2017-07-22.
 */

public interface PlaylistContract {
    interface View {
        void attachPresenter();

        void displayPlaylists(List<Playlist> allPlaylists);
    }

    interface Presenter {
        void attachView(PlaylistContract.View view);

        void detatchView();

        void onInit();

    }

    interface Interactor{
        void initPlaylists(playlistConfigListener listener);

        interface playlistConfigListener {
            void onInitSuccess(List<Playlist> allPlaylists);

            void onInitError();
        }
    }
}
