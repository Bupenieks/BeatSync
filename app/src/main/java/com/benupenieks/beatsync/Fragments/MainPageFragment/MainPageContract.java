package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ben on 2017-07-22.
 */

public interface MainPageContract {
    interface View {
        void displayErrorToast(String errorMsg);
    }

    interface Presenter {
        void attachView(View view);

        void detatchView();

        void onSpotifyLogIn();

        void onSpotifyLoginReceived(int resultCode, Intent intent);

        void onPlayTrack();

        void onUpdateBpm(int bpm);

        void onDisplayErrorToast(String errorMsg);
    }

    interface Interactor{
        void spotifyLogIn(View view);

        void verifySpotifyLogin(View view, int resultCode, Intent intent);

        void playRandomTrack(MainPageContract.Presenter listener);

        void updateValidTracks(int bpm);
    }
}
