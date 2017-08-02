package com.benupenieks.beatsync;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ben on 2017-07-23.
 */

public class Track {
    private JSONObject mTrackData;
    private JSONObject mTrackFeatures;

    private String mTrackName;
    private String mTrackUri;
    private String mTrackId;

    private int mTrackBPM;


    public Track(JSONObject track) {
        mTrackData = track;
        try {
            mTrackName = mTrackData.getString("name");
            mTrackUri = mTrackData.getString("uri");
            mTrackId = mTrackData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mTrackName;
    }

    public String getUri() {
        return mTrackUri;
    }

    public int getBPM() {
        return mTrackBPM;
    }

    public String getId() { return mTrackId; }

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
