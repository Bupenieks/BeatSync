package com.benupenieks.beatsync;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Ben on 2017-07-22.
 */

public class Playlist {
    private String mName;
    private String mPlaylistId;
    private String mTrackEndPoint;
    private String mUri;
    private int mNumTracks;

    private List<Track> mTrackList = new ArrayList<>();

    public Playlist (String name, String id, String href, String uri, int numTracks) {
        mName = name;
        mPlaylistId = id;
        mTrackEndPoint = href;
        mUri = uri;
        mNumTracks = numTracks;
    }

    public String getName() {
        return mName;
    }

    public String getTrackEndPoint() {
        return mTrackEndPoint;
    }

    public String getUri() {
        return mUri;
    }

    public int getNumTracks() {
        return mNumTracks;
    }

    public String getId() { return mPlaylistId;}

    public List<Track> getTrackList() {
        return mTrackList;
    }

    public boolean isEmpty() {
        return mTrackList.isEmpty();
    }

    public void addTrack(JSONObject track) {
        mTrackList.add(new Track(track, this));
    }

    public Track getRandomTrack() {
        Random rand = new Random();
        int index = rand.nextInt(mTrackList.size());
        return mTrackList.get(index);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "mName='" + mName + '\'' +
                ", mPlaylistId='" + mPlaylistId + '\'' +
                ", mTrackEndPoint='" + mTrackEndPoint + '\'' +
                ", mUri='" + mUri + '\'' +
                ", mNumTracks=" + mNumTracks +
                '}';
    }

    public void populateTrackFeatures(Map<Integer, ArrayList<Track> > BPMToTrackMap) {
        String trackIds = "";
        int trackCount = 0;
        final Map<String, Track> idToTrackMap = new HashMap<>();
        for (Track track : mTrackList) {
            final String id = track.getId();
            trackIds += trackIds.equals("") ? id : ',' + id;
            idToTrackMap.put(id, track);
            trackCount++;

            if (trackCount == 100) { // Max number of tracks for one get request
                trackCount = 0;
                trackFeatureGetRequest(trackIds, idToTrackMap, BPMToTrackMap);
                trackIds = "";
            }
        }
        if (!trackIds.equals("")) {
            trackFeatureGetRequest(trackIds, idToTrackMap, BPMToTrackMap);
        }
    }


    private void trackFeatureGetRequest(String trackIds, final Map<String, Track> trackMap, final Map<Integer, ArrayList<Track> > BPMToTrackMap) {
        String requestUrl = "https://api.spotify.com/v1/audio-features/?ids=" + trackIds;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("mResponseQueue", "Response Received");
                try {
                    JSONArray featureList = response.getJSONArray("audio_features");
                    for (int i = 0; i < featureList.length(); i++) {
                        JSONObject features = featureList.getJSONObject(i);
                        Track track = trackMap.get(features.getString("id"));
                        track.setTrackFeatures(features);

                        ArrayList<Track> trackList = BPMToTrackMap.get(track.getBPM());
                        if (trackList == null) {
                            trackList = new ArrayList<>();
                            BPMToTrackMap.put(track.getBPM(), trackList);
                        }
                        trackList.add(track);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("SpotifyController", "updateTrackList failed");
            }
        };

        SpotifyController.getInstance().JSONApiGetRequest
                (requestUrl, responseListener, errorListener);
    }
}
