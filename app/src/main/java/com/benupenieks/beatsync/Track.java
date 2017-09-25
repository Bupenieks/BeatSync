package com.benupenieks.beatsync;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ben on 2017-07-23.
 */

public class Track {
    private JSONObject mTrackData;
    private JSONObject mTrackFeatures;

    private String mTrackName;
    private String mTrackUri;
    private String mTrackId;
    private String mTrackArtist;

    private int mTrackBPM;
    private int mTrackDuration;

    private Playlist mParentPlaylist;

    public Track() {}

    public Track(JSONObject track, Playlist playlist) {
        mParentPlaylist = playlist;
        mTrackData = track;
        try {
            mTrackUri = mTrackData.getString("uri");
            mTrackId = mTrackData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mTrackName = mTrackData.getString("name");
            mTrackArtist = mTrackData.getJSONArray("artists").getJSONObject(0).getString("name");
            mTrackDuration = mTrackData.getInt("duration_ms");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mTrackName;
    }

    public String getArtist() {return mTrackArtist; }

    public String getUri() {
        return mTrackUri;
    }

    public int getBPM() {
        return mTrackBPM;
    }

    public String getId() { return mTrackId; }

    public Playlist getParentPlaylist() {
        return mParentPlaylist;
    }

    public Object getTrackFeature(String key) {
        try {
            return mTrackFeatures.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return R.string.json_not_found;
    }

    public Object getTrackDataElement(String key) {
        try {
            return mTrackFeatures.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return R.string.json_not_found;
    }

    private void getTrackFeatures() {
        String requestUrl = "https://api.spotify.com/v1/audio-features/" + mTrackId;
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("mResponseQueue", "Response Received");
                try {
                    mTrackFeatures = response;
                    mTrackBPM = mTrackFeatures.getInt("tempo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Track", "getTrackFeatures failed");
            }
        };

        SpotifyController.getInstance().JSONApiGetRequest(requestUrl, responseListener,
                errorListener);
    }

    public void setTrackFeatures(JSONObject features) {
        mTrackFeatures = features;
        try {
            mTrackBPM = mTrackFeatures.getInt("tempo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
