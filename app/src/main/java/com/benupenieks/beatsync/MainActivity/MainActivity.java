package com.benupenieks.beatsync.MainActivity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageContract;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPagePresenter;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

public class MainActivity extends AppCompatActivity implements MainPageContract.View,
        MainPageFragment.OnFragmentInteractionListener,
        PlaylistSelectionFragment.OnFragmentInteractionListener{

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    Fragment mMainPage = new MainPageFragment();
    Fragment mPlaylistSelection = new PlaylistSelectionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

    }
    // TODO: Abstract base presenter class?

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
    public void displayErrorToast(String errorMsg) {}

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
            return "Section " + (position + 1);
        }
    }
}