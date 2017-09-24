package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.benupenieks.beatsync.MainActivity.MainActivity;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.RowingActivity.AccelerometerInteractor;
import com.benupenieks.beatsync.RowingActivity.RowingActivity;
import com.benupenieks.beatsync.SpotifyController;
import com.benupenieks.beatsync.Track;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;

import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment implements MainPageContract.View {

    private MainPagePresenter mPresenter = new MainPagePresenter();

    private Toast mCurrentToast = null;
    private EditText mBpmBox;
    private LineChart mAccelerometerGraph;
    private PlayPauseButton mPlayButton;
    private boolean mPlayButtonState = false;
    private EventBus mEventBus = EventBus.getDefault();
    private TextView mSongInfo;
    private Visualizer mVisualizer;

    // FIXME
    private static final int MAX_BPM = 300;
    public static final int ROWING_ACTIVITY_REQUEST_CODE = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mCurrentBpm = 1;

    private OnFragmentInteractionListener mListener;

    public MainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPageFragment newInstance(String param1, String param2) {
        MainPageFragment fragment = new MainPageFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        mBpmBox = (EditText) view.findViewById(R.id.bpm_box);
        mSongInfo = (TextView) view.findViewById(R.id.song_info);
        mPlayButton = (PlayPauseButton) view.findViewById(R.id.play_button);

        mPlayButton.setPlayed(true);
        mPlayButton.setPlayed(false);

        mPlayButton.setOnControlStatusChangeListener(new PlayPauseButton.OnControlStatusChangeListener() {
            @Override
            public void onStatusChange(View view, boolean state) {
                mPresenter.onPlayButtonPress(mBpmBox.getText().toString(), mCurrentBpm, state);
            }
        });

        view.findViewById(R.id.forward_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onForwardButtonPress(Integer.parseInt(mBpmBox.getText().toString()), mCurrentBpm);
            }
        });

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onBackButtonPress();
            }
        });


        view.findViewById(R.id.rowing_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotifyController.getInstance().pause();
                Intent intent = new Intent(getContext(), RowingActivity.class);
                startActivityForResult(intent, ROWING_ACTIVITY_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        switch (requestCode) {
            case ROWING_ACTIVITY_REQUEST_CODE:
                mBpmBox.setText(data.getIntExtra("stroke_rate", 1));
        }
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
        mPresenter.detatchView();
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
    public void displayErrorToast(String errorMsg) {
        Toast toast = Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT);
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
        if (view != null) {
            view.setGravity(Gravity.CENTER);
        }
        mCurrentToast = toast;
        toast.show();

    }

    @Override
    public void displayLowPriorityErrorToast(String errorMsg) {
        if (mCurrentToast != null) return;
        Toast toast = Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT);
        TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
        if (view != null) {
            view.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
        mEventBus.unregister(this);
    }

    @Override
    public void updateGraph(float x, float y) {


    }

    public void setCurrentBpm(int bpm) { mCurrentBpm = bpm; }

    public void setPlayButtonState(boolean state) {
        mPlayButton.setPlayed(state);
        mPlayButton.startAnimation();
    }

    public int getCurrentBpm() {
        return mCurrentBpm;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateSongInfo(Track track) {
        Log.d("TEST","HERE");
        mSongInfo.setText(String.format("%s\n%s", track.getName(), track.getArtist()));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRowingResult(Integer strokeRate) {
        Log.d("ROWINGRESULT", "HERE");
        mPresenter.onForwardButtonPress(strokeRate, mCurrentBpm);
        mCurrentBpm = strokeRate;
        mBpmBox.setText(Integer.toString(strokeRate));
    }
}
