package com.benupenieks.beatsync;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public void addTrack(JSONObject track) {
        mTrackList.add(new Track(track));
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
}