package com.benupenieks.beatsync.Fragments.MainPageFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment implements MainPageContract.View {

    private class AccelerometerGraphData {
        public LineDataSet dataSet;
        public LineData data;
        public List<Entry> entries = new ArrayList<>();

        private final int MAX_DATA_POINTS = 300;

        public void updateData(Entry entry) {
            entries.add(entry);
            int numEntries = entries.size();
            if (numEntries == 2) {
                // init graph
                dataSet = new LineDataSet(entries, "Accelerometer");
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                List<ILineDataSet> tempHolder = new ArrayList<>();
                tempHolder.add(dataSet);
                data = new LineData(tempHolder);
                dataSet.setDrawValues(false);
                dataSet.setLineWidth(3.f);
                dataSet.setDrawCircles(false);
                mAccelerometerGraph.setData(data);
            } else if (numEntries > 2) {
                // update graph

                if (numEntries >= MAX_DATA_POINTS) {
                    dataSet.removeEntry(0);
                    entries.remove(0);
                }
                dataSet.addEntry(entry);
                data.notifyDataChanged();
                mAccelerometerGraph.notifyDataSetChanged();
            }
            mAccelerometerGraph.invalidate();
        }
    }

    private MainPagePresenter mPresenter = new MainPagePresenter();

    private Toast mCurrentToast = null;
    private EditText mBpmBox;
    private LineChart mAccelerometerGraph;
    private PlayPauseButton mPlayButton;
    private AccelerometerGraphData mGraphData = new AccelerometerGraphData();
    private boolean mPlayButtonState = false;

    // FIXME
    private static final int MAX_BPM = 300;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mCurrentBpm = 0;

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
        view.findViewById(R.id.spotify_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotifyController.getInstance().logIn(getActivity());
            }
        });

        mBpmBox = (EditText) view.findViewById(R.id.bpm_box);

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
                mPresenter.onForwardButtonPress();
            }
        });

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onBackButtonPress();
            }
        });



        // Graph formatting
        mAccelerometerGraph = (LineChart) view.findViewById(R.id.accelerometer_graph);
        mAccelerometerGraph.setDrawGridBackground(false);
        mAccelerometerGraph.setDrawBorders(false);
        mAccelerometerGraph.getAxisLeft().setDrawLabels(false);
        mAccelerometerGraph.getAxisRight().setDrawLabels(false);
        mAccelerometerGraph.getXAxis().setDrawLabels(false);
        mAccelerometerGraph.getLegend().setEnabled(false);
        mAccelerometerGraph.getAxisLeft().setDrawGridLines(false);
        mAccelerometerGraph.getAxisLeft().setEnabled(false);
        mAccelerometerGraph.getXAxis().setEnabled(false);
        mAccelerometerGraph.getAxisRight().setEnabled(false);

        Description des = mAccelerometerGraph.getDescription();
        des.setEnabled(false);

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

    public void displayErrorToast(String errorMsg) {
        Toast toast = Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT);
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }
        mCurrentToast = toast;
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void updateGraph(float x, float y) {
        mGraphData.updateData(new Entry(x, y));
    }

    public void setCurrentBpm(int bpm) { mCurrentBpm = bpm; }

    public void setPlayButtonState(boolean state) {
        mPlayButton.setPlayed(state);
        mPlayButton.startAnimation();
    }
}
