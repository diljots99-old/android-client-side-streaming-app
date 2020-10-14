package com.android.moviestreamer.media_player;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG ="VideoPlayerActivity" ;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    public static String VIDEO_URL ;

    Movie movieItem;
    boolean isTorrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_video_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        movieItem = getIntent().getParcelableExtra("Movie");
        isTorrent = getIntent().getBooleanExtra("isTorrent",false);

        VIDEO_URL = getIntent().getStringExtra("Url");
        Log.d(TAG, "onCreate: VIDEO_URL: "+VIDEO_URL);

        iniExoplayer();
        MediaSource videoSource = getMediaSourceFormURL(VIDEO_URL);
        prePareAndPlay(videoSource);

    }

    private void prePareAndPlay(MediaSource videoSource) {
        simpleExoPlayer.prepare(videoSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    private MediaSource getMediaSourceFormURL(String URL) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,getResources().getString(R.string.app_name)));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(URL));
        return videoSource;
    }
    private MediaSource getMediaSourceFormURL(Uri URL) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,getResources().getString(R.string.app_name)));

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(URL);
        return videoSource;
    }

    private void hideActionbar() {
        getSupportActionBar().hide();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void iniExoplayer() {
        playerView = findViewById(R.id.pvExoPlayer);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }
}