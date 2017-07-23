package com.benupenieks.beatsync.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import com.benupenieks.beatsync.SpotifyController;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainInteractor implements MainContract.Interactor {
    private SpotifyController mSpotify = SpotifyController.getInstance();

    public MainInteractor() {}

    @Override
    public void spotifyLogIn(MainContract.View view) {
        mSpotify.logIn((Activity) view);
    }

    @Override
    public void verifySpotifyLogin(MainContract.View view, int resultCode, Intent intent) {
        mSpotify.verifyLogIn((Activity) view, resultCode, intent);
    }
}
