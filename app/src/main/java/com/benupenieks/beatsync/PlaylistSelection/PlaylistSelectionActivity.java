package com.benupenieks.beatsync.PlaylistSelection;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.benupenieks.beatsync.Fragments.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment;
import com.benupenieks.beatsync.MainActivity.MainActivity;
import com.benupenieks.beatsync.Playlist;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistSelectionActivity extends AppCompatActivity implements PlaylistContract.View, PlaylistSelectionFragment.OnFragmentInteractionListener,
        MainPageFragment.OnFragmentInteractionListener {

    private PlaylistPresenter mPresenter;

    private LinearLayout mPlaylistsContainer;

    Map<CheckBox, Playlist> mCheckBoxPlaylistMap = new HashMap<>();

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

        mPlaylistsContainer = (LinearLayout) findViewById(R.id.playlist_selector);

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
    public void displayPlaylists(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists) {
        // TODO: put size values in resources.
        for (final Playlist playlist : allPlaylists) {
            CheckBox cb = new CheckBox(this);
            cb.setText(playlist.getName());
            cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            cb.setLines(2);
            mCheckBoxPlaylistMap.put(cb, playlist);
            mPlaylistsContainer.addView(cb);
            if (selectedPlaylists.contains(playlist)) {
                cb.setChecked(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO: Add spinner to wait for playlist tracks to load.
        List<Playlist> selectedPlaylists = new ArrayList<>();
        for (int i = 0; i < mPlaylistsContainer.getChildCount(); i++) {
            CheckBox cb = (CheckBox) mPlaylistsContainer.getChildAt(i);
            if (cb.isChecked()) {
                selectedPlaylists.add(mCheckBoxPlaylistMap.get(cb));
            }
        }
        mPresenter.setSelectedPlaylists(selectedPlaylists);
        mPresenter.detatchView();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
