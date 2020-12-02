package com.android.moviestreamer.media_player;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.android.moviestreamer.R;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

public class TorrentStreamerTestActivity extends AppCompatActivity {

    private static final String TAG = "TorrentStreamerTestActivity";

    TorrentStream torrentStream;
    TorrentOptions torrentOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_streamer_test);

//        String TorrentURL = "https://yts.mx/torrent/download/DCCA4828559E2FD52AB33631D51030C0CB93EB0D";
//        String TorrentURL = getString(R.string.API_BASE_URL) + getString(R.string.API_DOWNLOAD_TORRENT_FILE) + "359";
        String TorrentURL = "magnet:?xt=urn:btih:DCCA4828559E2FD52AB33631D51030C0CB93EB0D&dn=Bill%20Ted%20Face%20The%20Music%20%282020%29%20%5b1080p%5d%20%5bWEBRip%5d%20%5b5.1%5d%20%5bYTS.MX%5d&tr=udp%3a%2f%2ftracker.coppersurfer.tk%3a6969%2fannounce&tr=udp%3a%2f%2f9.rarbg.com%3a2800%2fannounce&tr=udp%3a%2f%2fp4p.arenabg.com%3a1337&tr=udp%3a%2f%2ftracker.internetwarriors.net%3a1337&tr=udp%3a%2f%2ftracker.opentrackr.org%3a1337%2fannounce";
//        String TorrentURL = "https://yts.mx/torrent/download/0EFAB02F3A049AAA0716954BB74CCE040E253F59";

        Log.d(TAG, "onCreate: URL: "+TorrentURL);

        torrentOptions = new TorrentOptions.Builder().saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).maxConnections(10000000).build();
        torrentStream = TorrentStream.init(torrentOptions);
        torrentStream.startStream(TorrentURL);
        Log.d(TAG, "onCreate: "+torrentStream.isStreaming());
        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                Log.d(TAG, "onStreamPrepared: " + torrent);
                Log.d(TAG, "onStreamPrepared: " + torrent.getState());
            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                Log.d(TAG, "onStreamStarted: " + torrent.getState());
                try {
                    Log.d(TAG, "onStreamStarted: " + torrent.getVideoStream());
                } catch (Exception e) {
                    Log.w(TAG, "onStreamStarted: ",e );
                }
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                Log.d(TAG, "onStreamError: " + torrent);
                Log.w(TAG, "onStreamError: ", e);
            }

            @Override
            public void onStreamReady(Torrent torrent) {
                Log.d(TAG, "onStreamReady: " + torrent);
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus status) {
                Log.d(TAG, "onStreamProgress: " + torrent);
                Log.d(TAG, "onStreamProgress: " + status);
            }

            @Override
            public void onStreamStopped() {
                Log.d(TAG, "onStreamStopped: ");

            }


        });

    }
}