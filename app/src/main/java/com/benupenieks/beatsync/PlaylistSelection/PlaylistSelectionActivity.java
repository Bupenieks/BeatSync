package com.benupenieks.beatsync.PlaylistSelection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

import java.util.List;

public class PlaylistSelectionActivity extends AppCompatActivity implements PlaylistContract.View {

    private PlaylistPresenter mPresenter;

    private static final SpotifyController mSpotify = SpotifyController.getInstance();
    private List<Playlist> mPlaylists;

    private RadioGroup mPlaylistsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, SpotifyController.getInstance().getUserId(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPlaylistsContainer = (RadioGroup) findViewById(R.id.playlist_selector);

        attachPresenter();
        mPresenter.onInit();
    }

    @Override
    public void attachPresenter() {
        mPresenter = (PlaylistPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            mPresenter = new PlaylistPresenter();
        }
        mPresenter.attachView(this);
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    public void displayPlaylists(List<Playlist> allPlaylists) {
        for (Playlist p : allPlaylists) {
            RadioButton rb = new RadioButton(this);
            rb.setText(p.getName());
            mPlaylistsContainer.addView(rb);
        }
    }
}
