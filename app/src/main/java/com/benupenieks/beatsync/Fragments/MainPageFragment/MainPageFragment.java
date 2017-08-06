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

    private EditText mBpmBox;

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

        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bpmContents = mBpmBox.getText().toString();
                if (!TextUtils.isEmpty(bpmContents)) {
                    int bpm = Integer.parseInt(bpmContents);
                    // Todo detect playlist change
                    if (bpm != mCurrentBpm && bpm > 0) {
                        mCurrentBpm = bpm;
                        mPresenter.onUpdateBpm(bpm);
                    }
                }

                if (mCurrentBpm > 0 && mCurrentBpm < MAX_BPM) {
                    mPresenter.onPlayTrack();
                } else {
                    displayErrorToast("Enter a valid BPM");
                }
            }
        });


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
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
