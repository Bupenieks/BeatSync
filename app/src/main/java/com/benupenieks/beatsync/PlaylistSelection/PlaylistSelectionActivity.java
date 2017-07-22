package com.benupenieks.beatsync.PlaylistSelection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.Request;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

import java.util.HashMap;
import java.util.Map;

public class PlaylistSelectionActivity extends AppCompatActivity implements PlaylistView {

    private RequestQueue mRequestQueue;
    private TextView mPlaylistsContainer;
    private final String mRequestUrl = "https://api.spotify.com/v1/me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_selection);

        mRequestQueue = Volley.newRequestQueue(this);
        mPlaylistsContainer = (TextView) findViewById(R.id.playlist_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, SpotifyController.mUserId, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
