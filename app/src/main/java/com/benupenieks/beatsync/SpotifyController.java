package com.benupenieks.beatsync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

/**
 * Created by Ben on 2017-07-21.
 */

public class SpotifyController implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final SpotifyController spcInstance = new SpotifyController();

    public static SpotifyController getInstance() { return spcInstance; }

    private static final String CLIENT_ID    = "0ca1042db69d4e759c7e8995f6ef0f50";
    private static final String REDIRECT_URI = "beatsync-login://callback";
    public static final int SPOTIFY_LOGIN_REQUEST_CODE = 12345;

    private Player mPlayer;

    private SpotifyController() {}

    public void playTrack(String uri) {
        Log.d("SpotifyController", "Playing track: " + uri);
        mPlayer.playUri(null, uri, 0, 0);
    }

    public void logIn(Activity parentActivity) {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(parentActivity, SPOTIFY_LOGIN_REQUEST_CODE, request);
    }

    public void verifyLogIn(Activity parentActivity, int resultCode, Intent intent) {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            Config playerConfig = new Config(parentActivity, response.getAccessToken(), CLIENT_ID);
            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(SpotifyController.this);
                    mPlayer.addNotificationCallback(SpotifyController.this);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("SpotifyController", "Could not initialize player: "
                            + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("SpotifyController", "User logged in");

        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("SpotifyController", "Logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("SpotifyController", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("SpotifyController", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SpotifyController", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("SpotifyController", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("SpotifyController", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    public void onDestroy() {
        Spotify.destroyPlayer(this);
    }
}
