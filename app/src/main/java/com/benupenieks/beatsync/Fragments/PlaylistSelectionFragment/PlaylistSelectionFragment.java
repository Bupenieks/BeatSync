package com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benupenieks.beatsync.Playlist;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistSelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistSelectionFragment extends Fragment
        implements PlaylistSelectionContract.View {

    private PlaylistSelectionPresenter mPresenter = new PlaylistSelectionPresenter();
    private LinearLayout mPlaylistsContainer;
    private Map<CheckBox, Playlist> mCheckBoxPlaylistMap = new HashMap<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PlaylistSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistSelectionFragment newInstance(String param1, String param2) {
        PlaylistSelectionFragment fragment = new PlaylistSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mPresenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist_selection, container, false);
        mPlaylistsContainer = (LinearLayout) view.findViewById(R.id.playlist_container);

        return view;
    }
    

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void displayPlaylists(List<Playlist> allPlaylists, List<Playlist> selectedPlaylists) {
        // TODO: put size values in resources.
        for (final Playlist playlist : allPlaylists) {
            final CheckBox cb = new CheckBox(getContext());
            cb.setText(playlist.getName());
            cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            cb.setLines(2);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (cb.isChecked()) {
                        mPresenter.onPlaylistSelected(mCheckBoxPlaylistMap.get(cb));
                    } else {
                        mPresenter.onPlaylistDeselected(mCheckBoxPlaylistMap.get(cb));
                    }
                }
            });
            mCheckBoxPlaylistMap.put(cb, playlist);
            mPlaylistsContainer.addView(cb);
            if (selectedPlaylists.contains(playlist)) {
                cb.setChecked(true);
            }
        }
    }
}
