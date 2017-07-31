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

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionPresenter;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionContract;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.Playlist;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistSelectionActivity extends AppCompatActivity implements PlaylistSelectionContract.View, PlaylistSelectionFragment.OnFragmentInteractionListener,
        MainPageFragment.OnFragmentInteractionListener {

    private PlaylistSelectionPresenter mPresenter;

    private LinearLayout mPlaylistsContainer;

    Map<CheckBox, Playlist> mCheckBoxPlaylistMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_selection);

    }

    public void attachPresenter() {
        mPresenter = (PlaylistSelectionPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            mPresenter = new PlaylistSelectionPresenter();
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
