package com.benupenieks.beatsync.MainActivity;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageContract;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

public class MainActivity extends AppCompatActivity implements
        MainContract.View,
        MainPageFragment.OnFragmentInteractionListener,
        PlaylistSelectionFragment.OnFragmentInteractionListener {

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    Fragment mMainPage = new MainPageFragment();
    Fragment mPlaylistSelection = new PlaylistSelectionFragment();

    MainContract.Presenter mPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabsLayout = (TabLayout) findViewById(R.id.tabs);
        tabsLayout.setupWithViewPager(mViewPager);

        mPresenter = new MainPresenter();
        mPresenter.attachView(this);

    }

    // @Override
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
                SpotifyController.getInstance().verifyLogIn(
                        (PlaylistSelectionFragment) mPlaylistSelection, MainActivity.this, resultCode, intent);
                break;
        }
    }


    @Override
    protected void onDestroy() {
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
                    return mMainPage;
                case 1:
                default:
                    return mPlaylistSelection;
            }
        }

        @Override
        public int getCount() { return 2; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                default:
                    return "Playlist Selection";
            }

        }
    }

    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }
}