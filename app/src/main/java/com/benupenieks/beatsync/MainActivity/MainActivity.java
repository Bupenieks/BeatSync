package com.benupenieks.beatsync.MainActivity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.benupenieks.beatsync.Fragments.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment;
import com.benupenieks.beatsync.PlaylistSelection.PlaylistSelectionActivity;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;
import com.benupenieks.beatsync.Track;

public class MainActivity extends FragmentActivity implements MainContract.View,
        MainPageFragment.OnFragmentInteractionListener, PlaylistSelectionFragment.OnFragmentInteractionListener{

    private MainPresenter mPresenter;

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attachPresenter();

        mPresenter.onSpotifyLogIn();

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

    }
    // TODO: Abstract base presenter class?
    @Override
    public void attachPresenter() {
        mPresenter = (MainPresenter) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            mPresenter = new MainPresenter();
        }
        mPresenter.attachView(this);
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SpotifyController.SPOTIFY_LOGIN_REQUEST_CODE:
                mPresenter.onSpotifyLoginReceived(resultCode, intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detatchView();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new MainPageFragment();
                case 1:
                default:
                    return new PlaylistSelectionFragment();
            }
        }

        @Override
        public int getCount() { return 2; }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }
}