package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.app.Activity;
import android.content.Intent;


import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.SpotifyController;

/**
 * Created by Ben on 2017-07-22.
 */

public class MainPageInteractor implements MainPageContract.Interactor {
    private SpotifyController mSpotify = SpotifyController.getInstance();

    public MainPageInteractor() {}

    @Override
    public void spotifyLogIn(MainPageContract.View view) {
        if (mSpotify.getUserId() == null) {
            mSpotify.logIn((Activity) view);
        }
    }

    @Override
    public void verifySpotifyLogin(MainPageContract.View view, int resultCode, Intent intent) {
        mSpotify.verifyLogIn((PlaylistSelectionFragment) view, (Activity) view, resultCode, intent);
    }
}
