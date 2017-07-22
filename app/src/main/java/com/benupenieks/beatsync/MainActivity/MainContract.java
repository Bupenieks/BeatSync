package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ben on 2017-07-22.
 */

public interface MainContract {
    interface View {

    }

    interface Presenter {
        void attachView(View view);

        void detatchView();

        void onSpotifyLogIn();

        void onSpotifyLoginReceived(int resultCode, Intent intent);
    }

    interface Interactor{
        void spotifyLogIn(View view);

        void verifySpotifyLogin(View view, int resultCode, Intent intent);
    }
}
