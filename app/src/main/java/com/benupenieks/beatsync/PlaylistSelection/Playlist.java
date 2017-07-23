package com.benupenieks.beatsync.PlaylistSelection;

/**
 * Created by Ben on 2017-07-22.
 */

public class Playlist {
    private String mName;
    private String mPlaylitsId;
    private String mTrackEndPoint;
    private String mUri;
    private int mNumTracks;

    public Playlist (String name, String id, String href, String uri, int numTracks) {
        mName = name;
        mPlaylitsId = id;
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

    public String getPlaylitsId() {

        return mPlaylitsId;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "mName='" + mName + '\'' +
                ", mPlaylitsId='" + mPlaylitsId + '\'' +
                ", mTrackEndPoint='" + mTrackEndPoint + '\'' +
                ", mUri='" + mUri + '\'' +
                ", mNumTracks=" + mNumTracks +
                '}';
    }
}
