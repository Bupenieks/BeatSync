package com.benupenieks.beatsync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.benupenieks.beatsync.PlaylistSelection.Playlist;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ben on 2017-07-21.
 */

public class SpotifyController implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final SpotifyController spcInstance = new SpotifyController();
    public static SpotifyController getInstance() { return spcInstance; }
    private SpotifyController() {}

    private static final String CLIENT_ID    = "0ca1042db69d4e759c7e8995f6ef0f50";
    private static final String REDIRECT_URI = "beatsync-login://callback";
    public  static final int SPOTIFY_LOGIN_REQUEST_CODE = 12345;

    private String mUserAccessToken;
    private String mUserId;

    private List<Playlist> mPlaylists = new ArrayList<>();

    private RequestQueue mRequestQueue;
    private Player mPlayer;

    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(mPlaylists);
    }

    public String getUserId() {
        return mUserId;
    }

    public void updateUserInfo() {
        updateUserId();
        updatePlaylists();
    }

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

    public void verifyLogIn(final Activity parentActivity, int resultCode, Intent intent) {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            mUserAccessToken = response.getAccessToken();
            Config playerConfig = new Config(parentActivity, mUserAccessToken, CLIENT_ID);
            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(SpotifyController.this);
                    mPlayer.addNotificationCallback(SpotifyController.this);
                    mRequestQueue = VolleyRequestQueue.getInstance(parentActivity).getRequestQueue();
                    spcInstance.updateUserId();
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
        mUserId = null;
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

    private void updateUserId() {
        String requestUrl = "https://api.spotify.com/v1/me";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Display the first 500 characters of the response string.
                    Log.d("mResponseQueue", "Response Received");
                    try {
                        mUserId = response.getString("id");
                    } catch (JSONException e) {
                        Log.e("SpotifyController", "updateUserID: User ID does not exist in response. Exception: ", e);
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("SpotifyController", "updateUserId failed");
        }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","Authorization: Bearer " + mUserAccessToken);
                return params;

            }
        };

        mRequestQueue.add(jsonRequest);
    }

    private void updatePlaylists() {
        final String requestUrl = "https://api.spotify.com/v1/me/playlists";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("mResponseQueue", "Response Received");
                        String nextPage;
                        try {
                            JSONArray items = response.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject jsonObj = items.getJSONObject(i);
                                Playlist playlist = new Playlist(
                                        jsonObj.getString("name"),
                                        jsonObj.getString("id"),
                                        jsonObj.getJSONObject("tracks").getString("href"),
                                        jsonObj.getString("uri"),
                                        jsonObj.getJSONObject("tracks").getInt("total")
                                );
                                mPlaylists.add(playlist);
                            }
                            nextPage = response.getString("next");
                        } catch (JSONException e) {
                            Log.e("SpotifyController", "updatePlaylists: Failed. Exception: ", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SpotifyController", "updatePlaylists failed");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Authorization","Authorization: Bearer " + mUserAccessToken);
                return params;

            }
        };

        mRequestQueue.add(jsonRequest);
    }

}
