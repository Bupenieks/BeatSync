package com.benupenieks.beatsync.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.benupenieks.beatsync.PlaylistSelection.PlaylistSelectionActivity;
import com.benupenieks.beatsync.R;
import com.benupenieks.beatsync.SpotifyController;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attachPresenter();

        mPresenter.onSpotifyLogIn();

        final Button toPlaylists = (Button) findViewById(R.id.toPlaylists);
        toPlaylists.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent toPlaylistSelector = new Intent(MainActivity.this, PlaylistSelectionActivity.class);
                MainActivity.this.startActivity(toPlaylistSelector);
            }
        });

        final Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotifyController.getInstance().playTrack();
            }
        });
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
}