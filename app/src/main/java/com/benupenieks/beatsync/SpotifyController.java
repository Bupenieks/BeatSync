package com.benupenieks.beatsync;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageContract;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import static com.benupenieks.beatsync.SpotifyController.Interaction.INVALID;
import static com.benupenieks.beatsync.SpotifyController.Interaction.NEXT_TRACK;
import static com.benupenieks.beatsync.SpotifyController.Interaction.PAUSE;
import static com.benupenieks.beatsync.SpotifyController.Interaction.PLAY_NEW;
import static com.benupenieks.beatsync.SpotifyController.Interaction.PREVIOUS_TRACK;
import static com.benupenieks.beatsync.SpotifyController.Interaction.RESUME;


/**
 * Created by Ben on 2017-07-21.
 */

public class SpotifyController implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    private static final SpotifyController spcInstance = new SpotifyController();
    public static SpotifyController getInstance() { return spcInstance; }
    private SpotifyController() {}

    private static final String TAG = "SpotifyController";
    private static final String CLIENT_ID    = "0ca1042db69d4e759c7e8995f6ef0f50";
    private static final String REDIRECT_URI = "beatsync-login://callback";
    public  static final int SPOTIFY_LOGIN_REQUEST_CODE = 12345;

    private String mUserAccessToken = null;
    private String mUserId = null;

    private Stack<Track> trackStack = new Stack();
    private Track mCurrentTrack = null;

    private List<Playlist> mPlaylists = new ArrayList<>();
    private List<Playlist> mSelectedPlaylists = new ArrayList<>();
    private List<Track> mValidTracks = new ArrayList<>();
    private Map<Integer, ArrayList<Track> > mBPMToTrackMap = new HashMap<>();

    private RequestQueue mRequestQueue;
    private Player mPlayer;
    private EventBus mEventBus = EventBus.getDefault();

    public enum Interaction {
        PLAY_NEW, PAUSE, RESUME, NEXT_TRACK, PREVIOUS_TRACK, INVALID
    }


    public List<Playlist> getAllPlaylists() {
        return Collections.unmodifiableList(mPlaylists);
    }

    public List<Playlist> getSelectedPlaylists() {
        return mSelectedPlaylists;
    }

    public Map<Integer, ArrayList<Track>> getBpmTrackMap() {
        return Collections.unmodifiableMap(mBPMToTrackMap);
    }

    public String getUserId() {
        return mUserId;
    }

    public Boolean isLoggedIn() { return mUserAccessToken != null; }

    public void addNewSelectedPlaylist(Playlist playlist) {
        mSelectedPlaylists.add(playlist);
    }

    public void removeSelectedPlaylist(Playlist playlist) {
        mSelectedPlaylists.remove(playlist);
    }

    public void playTrack(Track track, final MainPageContract.Interactor errorListener) {
        playTrack(track, errorListener, PLAY_NEW);
    }

    public void playTrack(Track track, final MainPageContract.Interactor errorListener, final Interaction interaction) {
        if (!trackStack.empty() && track == trackStack.peek()) {
            errorListener.playDifferentTrack(track);
            return;
        } else if (mSelectedPlaylists.isEmpty()) {
            errorListener.onError("No playlists selected");
            return;
        }
        Log.d("SpotifyController", "Playing track: " + track.getName()
                + " BPM : " + track.getBPM());

        mEventBus.postSticky(track);
        mPlayer.playUri(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                errorListener.onSuccessfulInteraction(interaction);
            }

            @Override
            public void onError(Error error) {
                errorListener.onError(interaction);
            }
        }, track.getUri(), 0, 0);
        mCurrentTrack = track;
    }

    public void playTrack(Track track) {
        Log.d("SpotifyController", "Playing track: " + track.getName()
                + " BPM : " + track.getBPM());
        mPlayer.playUri(null, track.getUri(), 0, 0);
        mCurrentTrack = track;
        mEventBus.postSticky(track);
    }

    public void pause() {
        mPlayer.pause(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Pause successful");
            }

            @Override
            public void onError(Error error) {
                Log.d(TAG, error.toString());
            }
        });}

    public void trackInteraction(Interaction interaction, final MainPageContract.Interactor listener) {
        switch (interaction) {
            case PAUSE:
                mPlayer.pause(new Player.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Pause successful");
                        listener.onSuccessfulInteraction(PAUSE);
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(TAG, error.toString());
                        listener.onError(PAUSE);
                    }
                });
                break;
            case RESUME:
                if (mPlayer.getMetadata().currentTrack != null) {
                    mPlayer.resume(new Player.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Resume successful");
                            listener.onSuccessfulInteraction(RESUME);
                        }

                        @Override
                        public void onError(Error error) {
                            Log.d(TAG, error.toString());
                            listener.onError(PLAY_NEW);
                        }
                    });
                    break;
                }
            case PLAY_NEW:
                listener.playRandomTrack();
                break;
            case NEXT_TRACK:
                if (mCurrentTrack != null) {
                    trackStack.add(mCurrentTrack);
                }
                listener.playRandomTrack();
                break;
            case PREVIOUS_TRACK:
                if (!trackStack.empty()) {
                    playTrack(trackStack.pop(), listener, PREVIOUS_TRACK);
                } else {
                    listener.onError(PREVIOUS_TRACK);
                }
                break;
            default:
                listener.onError(INVALID);

        }


    }

    public void logIn(Activity parentActivity) {
        if (isLoggedIn()) return;
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(parentActivity, SPOTIFY_LOGIN_REQUEST_CODE, request);
    }

    public void verifyLogIn(final PlaylistSelectionFragment playlistSelection, final Activity parentActivity, int resultCode, Intent intent) {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            mUserAccessToken = response.getAccessToken();
            final Config playerConfig = new Config(parentActivity, mUserAccessToken, CLIENT_ID);
            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(SpotifyController.this);
                    mPlayer.addNotificationCallback(SpotifyController.this);
                    mRequestQueue = VolleyRequestQueue.getInstance(parentActivity).getRequestQueue();
                    spcInstance.updateUserInfo(playlistSelection);
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
    }

    public void updateUserInfo(PlaylistSelectionFragment listener) {
        updateUserId();
        updatePlaylists(listener);
    }

    @Override
    public void onLoggedOut() {
        Log.d("SpotifyController", "Logged out");
        mUserId = null;
        mUserAccessToken = null;
        mPlaylists.clear();
        mSelectedPlaylists.clear();
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
    // TODO: Take care of empty playlists
    // TODO: Multiple playlist sle
    public Track getRandomTrack() {
        Random rand = new Random();
        int numPlaylists = mSelectedPlaylists.size();
        int index = numPlaylists == 0 ? 0 : rand.nextInt(numPlaylists);
        return mSelectedPlaylists.get(index).getRandomTrack();
    }

    private void updateUserId() {
        String requestUrl = "https://api.spotify.com/v1/me";

        Response.Listener responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                Log.d("mResponseQueue", "Response Received");
                try {
                    mUserId = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SpotifyController", "updateUserId failed");
            }
        };

        JSONApiGetRequest(requestUrl, responseListener, errorListener);
    }

    private void updatePlaylists(final PlaylistSelectionFragment listener) {
        mPlaylists.clear();
        mSelectedPlaylists.clear();
        updatePlaylists("https://api.spotify.com/v1/me/playlists", listener);
    }

    private void updatePlaylists(final String requestUrl, final PlaylistSelectionFragment listener) {

        Response.Listener responseListener = new Response.Listener<JSONObject>() {
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
                    if (!nextPage.equals("null")) {
                        Log.d("mResponseQueue", nextPage);
                        updatePlaylists(nextPage, listener);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.displayPlaylists(mPlaylists, mSelectedPlaylists);
                updateTrackList();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SpotifyController", "updatePlaylists failed");
            }
        };

        JSONApiGetRequest(requestUrl, responseListener, errorListener);
    }

    public void updateTrackList() {
        for (final Playlist playlist : mPlaylists) {
            String requestUrl = "https://api.spotify.com/v1/users/" + mUserId + "/playlists/"
                    + playlist.getId() + "/tracks";
            updateTrackList(requestUrl, playlist);
        }
    }

    public void updateTrackList(String requestUrl, final Playlist playlist) {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("mResponseQueue", "Response Received");
                String nextPage;
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        playlist.addTrack(items.getJSONObject(i).getJSONObject("track"));
                    }

                    nextPage = response.getString("next");
                    if (!nextPage.equals("null")) {
                        updateTrackList(nextPage, playlist);
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mBPMToTrackMap.clear();
                playlist.populateTrackFeatures(mBPMToTrackMap);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SpotifyController", "updateTrackList failed");
            }
        };

        JSONApiGetRequest(requestUrl, responseListener, errorListener);

    }

    public void JSONApiGetRequest(String requestUrl, Response.Listener listener,
                                    Response.ErrorListener errorListener) {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Authorization: Bearer " + mUserAccessToken);
                return params;
            }
        };
        mRequestQueue.add(jsonRequest);
    }

}
