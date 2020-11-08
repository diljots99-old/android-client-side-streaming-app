package com.android.moviestreamer.media_player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;

import com.frostwire.jlibtorrent.TorrentHandle;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;



import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.STORAGE;

public class TorrentStreammer extends AppCompatActivity {
    private static final String TAG = "TorrentStreammer";

    Movie movieItem;

    TorrentStream torrentStream;
    TorrentOptions torrentOptions;

    static String StreamUrl;
    CardView cvBuffering;
    TextView tvActivePeer, tvDownloadSpeed, tvStatus;
    ProgressBar pbBuffredprogress;
    String[] perms = {"android.permission.FINE_LOCATION", "android.permission.CAMERA"};

    private boolean isStreamReady;

    private boolean doubleBackToExitPressedOnce = false;

    private static final int PERMISSION_REQUEST_CODE = 200;

    public TorrentStreammer(){
        torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))

                .maxConnections(10000000)
                .build();

        torrentStream = TorrentStream.init(torrentOptions);

        Log.d(TAG, "TorrentStreammer: streamURL constructor "+StreamUrl);

        torrentStream.startStream(StreamUrl);
    }

    public static void start(Context context, Movie movieItem, String url) {
        Intent intent = new Intent(context, TorrentStreammer.class);

        intent.putExtra("Movie", movieItem);
        intent.putExtra("url", url);

        context.startActivity(intent);
        StreamUrl = url;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_streamer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, STORAGE}, PERMISSION_REQUEST_CODE);
        movieItem = getIntent().getParcelableExtra("Movie");






        tvActivePeer = findViewById(R.id.tvActivePeerIndicator);
        tvDownloadSpeed = findViewById(R.id.tvDownloadSpeedIndicator);
        pbBuffredprogress = findViewById(R.id.pbBufferedProgressPlayMovie);
        pbBuffredprogress.setIndeterminate(true);
        tvStatus = findViewById(R.id.tvStatusPlayMovie);

        pbBuffredprogress.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);



        cvBuffering = findViewById(R.id.cvBuffering);
        cvBuffering.setVisibility(View.VISIBLE);


        Log.d(TAG, "onCreate: torrent Url:  "+  torrentStream.getCurrentTorrentUrl());
        Log.d(TAG, "onCreate: isStream"+torrentStream.isStreaming());
        torrentStream.getCurrentTorrentUrl();
        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                tvStatus.setText("Waiting For Connection...");

                Log.d(TAG, "onStreamPrepared: + "+ torrent.getFileNames());
                Log.d(TAG, "onStreamPrepared: + "+ torrent.getSaveLocation());
                Log.d(TAG, "onStreamPrepared: + "+ torrent.getVideoFile());
                Log.d(TAG, "onStreamPrepared: + "+ torrent.getState());
                Log.d(TAG, "onStreamPrepared: + "+ torrent.getPiecesToPrepare());

            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                tvStatus.setText("Starting");
                Log.d(TAG, "onStreamStarted: ");

                Log.d(TAG, "onStreamStarted: + "+ torrent.getFileNames());
                Log.d(TAG, "onStreamStarted: + "+ torrent.getSaveLocation());
                Log.d(TAG, "onStreamStarted: + "+ torrent.getVideoFile());
                Log.d(TAG, "onStreamStarted: + "+ torrent.getState());

                Log.d(TAG, "onStreamStarted: + "+ torrent.getPiecesToPrepare());
                Log.d(TAG, "onStreamStarted: + "+ torrent.getInterestedPieceIndex());
                Log.d(TAG, "onStreamStarted: + "+ torrent.getTorrentHandle().getDownloadQueue());
                Log.d(TAG, "onStreamStarted: + "+ torrent.getTorrentHandle().fileProgress());

                Log.d(TAG, "onStreamStarted: + "+ torrent.getTorrentHandle().getDownloadLimit());
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {

                e.printStackTrace();

            }

            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d(TAG, "onStreamReady: " + torrent.getState());


                try {
                    //TODO PLay Movie here
                    Intent intent = new Intent(TorrentStreammer.this, VideoPlayerActivity.class);
                    intent.putExtra("Movie",movieItem);
                    intent.putExtra("Url", Uri.fromFile(torrent.getVideoFile()).toString());
                    intent.putExtra("isTorrent",true);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus status) {
                Log.d(TAG, "onStreamProgress: Status Progress" + status.progress);
                pbBuffredprogress.setIndeterminate(false);
                pbBuffredprogress.setProgress(status.bufferProgress);
                tvActivePeer.setText(status.seeds + "");
                String download = status.downloadSpeed / 1000 + " KB/s";
                tvDownloadSpeed.setText(download);
                tvStatus.setText("Buffering");

            }

            @Override
            public void onStreamStopped() {

            }
        });

    }

    protected void onStop () {
        super.onStop();
        torrentStream.pauseSession();
    }


    protected void onResume () {
        super.onResume();
        torrentStream.resumeSession();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            torrentStream.stopStream();
            this.finish();

            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(TorrentStreammer.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        CountDownTimer timer = new CountDownTimer(2000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                doubleBackToExitPressedOnce = false;
            }
        }.start();

    }
}
