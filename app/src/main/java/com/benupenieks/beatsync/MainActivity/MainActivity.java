package com.benupenieks.beatsync.MainActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageContract;
import com.benupenieks.beatsync.Fragments.MainPageFragment.MainPageFragment;
import com.benupenieks.beatsync.Fragments.PlaylistSelectionFragment.PlaylistSelectionFragment;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.RowingActivity.RowingActivity;
import com.benupenieks.beatsync.SpotifyController;

import org.greenrobot.eventbus.EventBus;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AestheticActivity implements
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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        Aesthetic.get()
                .colorPrimaryRes(R.color.colorPrimary)
                .colorWindowBackgroundRes(R.color.colorBackground)
                .colorAccentRes(R.color.colorAccent)
                .apply();

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabsLayout = (TabLayout) findViewById(R.id.tabs);
        tabsLayout.setupWithViewPager(mViewPager);

        mPresenter = new MainPresenter();
        mPresenter.attachView(this);

        SpotifyController.getInstance().logIn(this);

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
        Log.d("Main Activity", "Activity Result Received");
        Log.d("MAin Activity", " " + requestCode);
        switch (requestCode) {
            case SpotifyController.SPOTIFY_LOGIN_REQUEST_CODE:
                SpotifyController.getInstance().verifyLogIn(
                        (PlaylistSelectionFragment) mPlaylistSelection, MainActivity.this, resultCode, intent);
                break;
            case MainPageFragment.ROWING_ACTIVITY_REQUEST_CODE:
                Log.d("Main Activity", "Posting stroke rate");
                EventBus.getDefault().post(intent.getIntExtra("stroke_rate", 1));
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

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart(this);
    }
}