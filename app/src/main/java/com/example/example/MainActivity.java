package com.example.example;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private Activity activity = this;

    private Button mPlayPauseToggleButton;

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(MainActivity.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
//                setSupportMediaController(mMediaControllerCompat);
                MediaControllerCompat.setMediaController(activity, mMediaControllerCompat);
//                Uri uri = Uri.parse("https://www.ssaurel.com/tmp/mymusic.mp3");
//                mMediaControllerCompat.getTransportControls().playFromUri(uri, null);
                mMediaControllerCompat.getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);
//                getSupportMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);

            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    mCurrentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayPauseToggleButton = (Button) findViewById(R.id.button);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        mPlayPauseToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mCurrentState == STATE_PAUSED ) {
                    mMediaControllerCompat.getTransportControls().play();
//                    getSupportMediaController().getTransportControls().play();
                    mCurrentState = STATE_PLAYING;
                } else {
//                    if( getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                    if( mMediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
//                        getSupportMediaController().getTransportControls().pause();
                        mMediaControllerCompat.getTransportControls().pause();
                    }

                    mCurrentState = STATE_PAUSED;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if( getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
//            getSupportMediaController().getTransportControls().pause();
//        }
        if( mMediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            mMediaControllerCompat.getTransportControls().pause();
        }

        mMediaBrowserCompat.disconnect();
    }

}
