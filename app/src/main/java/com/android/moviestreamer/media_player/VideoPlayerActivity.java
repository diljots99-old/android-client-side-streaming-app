package com.android.moviestreamer.media_player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG ="VideoPlayerActivity" ;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    public static String VIDEO_URL ;
    public static Uri VIDEO_URI;

    public ProgressBar progressBar;

    ImageButton ib_more;
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

        VIDEO_URI = Uri.parse(VIDEO_URL);


        playerView = findViewById(R.id.pvExoPlayer);
        progressBar = findViewById(R.id.id_buffering_progress_bar);
        ib_more = playerView.findViewById(R.id.ib_control_view_more);
//        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);


        LoadControl loadControl = new DefaultLoadControl();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        TrackSelector trackSelector = new DefaultTrackSelector( new AdaptiveTrackSelection.Factory(bandwidthMeter));

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(VideoPlayerActivity.this,trackSelector,loadControl);

        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(
          "exoplayer_video"
        );

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(VIDEO_URI, factory,extractorsFactory,null,null );

        playerView.setPlayer(simpleExoPlayer);

        playerView.setKeepScreenOn(true);

        simpleExoPlayer.prepare(mediaSource);

        simpleExoPlayer.setPlayWhenReady(true);

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == Player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                }else if (playbackState == Player.STATE_READY){
                    progressBar.setVisibility(View.GONE);

                }
            }


        });


        ib_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

//        MediaSource videoSource = getMediaSourceFormURL(VIDEO_URL);
//        prePareAndPlay(videoSource);

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


    @Override
    protected void onPause() {
        super.onPause();

        simpleExoPlayer.setPlayWhenReady(false);

        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        simpleExoPlayer.setPlayWhenReady(true);

        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }
}