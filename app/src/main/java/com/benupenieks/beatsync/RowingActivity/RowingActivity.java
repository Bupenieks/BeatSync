package com.benupenieks.beatsync.RowingActivity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.MainActivity.MainActivity;
import com.benupenieks.beatsync.MainActivity.MainContract;
import com.benupenieks.beatsync.MainActivity.MainPresenter;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;


public class RowingActivity
        extends AestheticActivity implements
        RowingContract.View,
        MainPageFragment.OnFragmentInteractionListener,
        PlaylistSelectionFragment.OnFragmentInteractionListener {

    private RowingContract.Presenter mPresenter;
    private SpotifyController mSpotify;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rowing);

        Aesthetic.get()
                .colorPrimaryRes(R.color.colorPrimary)
                .colorWindowBackgroundRes(R.color.colorBackground)
                .colorAccentRes(R.color.colorAccent)
                .apply();

        mPresenter = new RowingPresenter();
        mPresenter.attachView(this);


        findViewById(R.id.finish_rowing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RowingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mSpotify = SpotifyController.getInstance();
    }

    // @Override
    public void attachPresenter() {
        mPresenter = (RowingPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            mPresenter = new RowingPresenter();
        }
        mPresenter.attachView(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart(this);
    }
}