package com.android.moviestreamer.ui.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.moviestreamer.R;
import com.android.moviestreamer.media_player.TorrentStreamerTestActivity;
import com.android.moviestreamer.media_player.TorrentStreammer;
import com.android.moviestreamer.media_player.VideoPlayerActivity;
import com.android.moviestreamer.media_player.WebViewPlayerActivity;
import com.android.moviestreamer.ui.people.PeoplePosterAdapter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    private String TAG = "MovieDetailsActivity";

    TextView tv_movie_name, tv_icon_restricted, tv_geners, tv_runtime, tv_user_rating, tv_overview;
    RecyclerView rv_cast_and_crew, rv_similar_movies;
    Button btn_watch_now;
    ImageView iv_backdrop;
    Movie movie;
    ShimmerFrameLayout sfl_cast, sfl_similar_movies;
    List<Movie> mData_similar_movies = new ArrayList<>();

    List<JSONObject> mData_cast_crew = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = getIntent();

        movie = intent.getParcelableExtra("movie");
        Log.d(TAG, "onCreate: movie_id" + movie.getId());


        tv_movie_name = findViewById(R.id.tv_movie_name);
        tv_icon_restricted = findViewById(R.id.tv_icon_restricted);
        tv_geners = findViewById(R.id.tv_geners);
        tv_runtime = findViewById(R.id.tv_runtime);
        tv_user_rating = findViewById(R.id.tv_user_rating);
        tv_overview = findViewById(R.id.tv_overview);
        iv_backdrop = findViewById(R.id.iv_movie_backdrop);
        btn_watch_now = findViewById(R.id.btn_watch_now);
        rv_cast_and_crew = findViewById(R.id.rv_cast_and_crew);
        sfl_cast = findViewById(R.id.sfl_cast);
        rv_similar_movies = findViewById(R.id.rv_similar_movies);

        sfl_similar_movies = findViewById(R.id.sfl_similar_movies);
        if (mData_cast_crew.size() == 0) {
            sfl_cast.setVisibility(View.VISIBLE);
        } else {
            sfl_cast.setVisibility(View.GONE);
        }
        if (mData_similar_movies.size() == 0) {
            sfl_similar_movies.setVisibility(View.VISIBLE);
        } else {
            sfl_similar_movies.setVisibility(View.GONE);
        }

        btn_watch_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playMovie();

            }
        });


        tv_movie_name.setText(movie.getTitle());

        if (movie.isAdult()) {
            tv_icon_restricted.setVisibility(View.VISIBLE);
        } else {
            tv_icon_restricted.setVisibility(View.GONE);
        }
        tv_user_rating.setText(((int) movie.getVote_average() * 10) + "% user likes this after watching");
        tv_runtime.setText(getRuntimeSTR());
        tv_overview.setText(movie.getOverview());
        tv_geners.setText(getGenreSTR());

        setMovieBackdrop();
        setCastAndCrew();
        setSimilarMovies();

    }

    void playMovie() {
        String sources = movie.getSources();
        try {
            JSONObject sources_jsonObject = new JSONObject(sources);
            JSONArray torrents = sources_jsonObject.getJSONArray("torrents");
            JSONArray drive = sources_jsonObject.getJSONArray("Drive");
            JSONArray firebaseJson = sources_jsonObject.getJSONArray("firebase");

            if (firebaseJson.length() >0){
                playFirebase(firebaseJson);
            }
//            else if (drive.length() > 0) {
//                Log.d(TAG, "playMovie: " + drive);
//                playDrive(drive);
//            }
            else {
                 playTorrent(torrents);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void playFirebase(JSONArray firebaseJson){
        try {
            Intent intent = new Intent(MovieDetailsActivity.this, VideoPlayerActivity.class);
            intent.putExtra("Movie", movie);
//        intent.putExtra("Url", "https://firebasestorage.googleapis.com/v0/b/dj-movies-and-shows.appspot.com/o/Ad.Astra.2019.720p.BluRay.x264-%5BYTS.LT%5D.mp4?alt=media&token=a4b2ed2c-8017-42fc-9e8b-c47c017935f8");
            intent.putExtra("Url", firebaseJson.getJSONObject(0).getString("public_url"));

            intent.putExtra("isTorrent", true);

            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void playDrive(JSONArray drive) {
        try {
            Intent intent = new Intent(MovieDetailsActivity.this, WebViewPlayerActivity.class);
            intent.putExtra("Movie", movie);
            String preview_url ="";
            for (int index = 0; index < drive.length(); index++) {
                 preview_url = drive.getJSONObject(index).getString("preview_url");

                break;

            }
            Log.d(TAG, "playDrive: " + preview_url);

//            String url = downloadUrl("https://drive.google.com/file/d/1jx4rASBA1A0B7Xi-EK3gGDuJ_vCn_9FT/preview");
//            Log.d(TAG, "onClick: " + url);
            intent.putExtra("Url", preview_url);
            intent.putExtra("isTorrent", true);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void playTorrent(JSONArray torrents) {
        try {
            int Healthy_torrent_index = 0;
            int min_Seeds = 0;

            for (int index = 0; index < torrents.length(); index++) {
                int seeds = ((JSONObject) torrents.get(index)).getInt("seeds");
                String quality = ((JSONObject) torrents.get(index)).getString("quality");
                if (min_Seeds < seeds && quality.contains("720p")) {
                    min_Seeds = seeds;
                    Healthy_torrent_index = index;
                    Log.d(TAG, "onClick: " + Healthy_torrent_index);
                }
            }

            String url = ((JSONObject) torrents.get(Healthy_torrent_index)).getString("url");

            url = getString(R.string.API_BASE_URL) + getString(R.string.API_DOWNLOAD_TORRENT_FILE) + torrents.getJSONObject(Healthy_torrent_index).getInt("id");
            ;

            Log.d(TAG, "onClick: " + url);
            Log.d(TAG, "onClick: " + torrents);

//                    Intent intent = new Intent(MovieDetailsActivity.this, TorrentStreammer.class);
//                    intent.putExtra("Movie", movie);
//                    intent.putExtra("url",url);
//                    startActivity(intent);
//                    TorrentStreammer.start(MovieDetailsActivity.this, movie, url);
//            Intent intent = new Intent(MovieDetailsActivity.this, VideoPlayerActivity.class);
            Intent intent = new Intent(MovieDetailsActivity.this, TorrentStreamerTestActivity.class);
            intent.putExtra("Movie", movie);
            intent.putExtra("Url", "https://firebasestorage.googleapis.com/v0/b/dj-movies-and-shows.appspot.com/o/Ad.Astra.2019.720p.BluRay.x264-%5BYTS.LT%5D.mp4?alt=media&token=a4b2ed2c-8017-42fc-9e8b-c47c017935f8");
//                    intent.putExtra("Url", "https://doc-14-34-docs.googleusercontent.com/docs/securesc/n25kolo7sl8b313qtog9d8cvkgp0ue4k/h0ikecc9ufgen9406a2g48gpgev4v5th/1603975275000/08218467108766436231/12882288675603248858Z/1oIeQs_YhhnFHvD-HFUOIXUy-kjpouwn4?e=download");
            try {
                url = downloadUrl("https://drive.google.com/file/d/1oIeQs_YhhnFHvD-HFUOIXUy-kjpouwn4/view");
                url = downloadUrl("https://drive.google.com/file/d/1jx4rASBA1A0B7Xi-EK3gGDuJ_vCn_9FT/view");

                Log.d(TAG, "onClick: " + url);

                intent.putExtra("Url", url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtra("isTorrent", true);

            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setSimilarMovies() {
        rv_similar_movies.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_similar_movies.setItemViewCacheSize(10);
        rv_similar_movies.setDrawingCacheEnabled(true);
        rv_similar_movies.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mData_similar_movies.clear();
        sfl_cast.setVisibility(View.VISIBLE);

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_MOVIE_SIMILAR_MOVIES) + "{id}";

        Log.d(TAG, "setSimilarMovies: " + url);
        AndroidNetworking.get(url)
                .addPathParameter("id", String.valueOf(movie.getId()))
                .addQueryParameter("fetch_length", "50")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray movies = response.getJSONArray("results");
                            for (int index = 0; index < movies.length(); index++) {
                                Movie movie1 = new Movie(movies.getJSONObject(index));
                                mData_similar_movies.add(movie1);
                                Log.d(TAG, "onResponse: similarMovie" + movie1.getOriginal_title());
                            }
                            RecyclerView.Adapter adapter = new MoviePosterAdapter(MovieDetailsActivity.this, mData_similar_movies);
                            rv_similar_movies.setAdapter(adapter);
                            sfl_similar_movies.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    void setCastAndCrew() {
        rv_cast_and_crew.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_cast_and_crew.setItemViewCacheSize(10);
        rv_cast_and_crew.setDrawingCacheEnabled(true);
        rv_cast_and_crew.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mData_cast_crew.clear();
        sfl_cast.setVisibility(View.VISIBLE);

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_MOVIE_CREDITS_MOVIES) + "{id}";

        AndroidNetworking.get(url).addPathParameter("id", String.valueOf(movie.getId())).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray cast = response.getJSONArray("cast");

                    JSONArray crew = response.getJSONArray("crew");


                    for (int index = 0; index < cast.length(); index++) {
                        JSONObject castObject = cast.getJSONObject(index);
                        castObject.put("type", "cast");
                        mData_cast_crew.add(castObject);
                        Log.d(TAG, "onResponse: " + castObject);
                    }
                    for (int index = 0; index < crew.length(); index++) {
                        JSONObject castObject = crew.getJSONObject(index);
                        castObject.put("type", "crew");
                        mData_cast_crew.add(castObject);
                        Log.d(TAG, "onResponse: " + castObject);

                    }

                    RecyclerView.Adapter adapter = new PeoplePosterAdapter(MovieDetailsActivity.this, mData_cast_crew);
                    rv_cast_and_crew.setAdapter(adapter);
                    sfl_cast.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    String getRuntimeSTR() {
        int runtime = movie.getRuntime();
        int hours = runtime / 60;
        int minutes = runtime % 50;
        String runtimeSTR = tv_runtime.getText().toString();

        if (hours > 0) {
            runtimeSTR = hours + "h";
        }

        if (minutes > 0) {
            runtimeSTR += " " + minutes + "m";
        }

        return runtimeSTR;
    }

    String getGenreSTR() {
        String genreSTR = tv_geners.getText().toString();

        for (String genre : movie.getGenres()) {
            genreSTR += genre + ", ";
        }
        ;
        return genreSTR;
    }

    void setMovieBackdrop() {
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();

        Log.d(TAG, "setMovieBackdrop: ");

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Log.d(TAG, "setMovieBackdrop: " + movie.getBackdrop_urls().get(0));
//        if (movie.getBackdrop_urls().size() > 0) {
        Glide.with(MovieDetailsActivity.this)
                .load(movie.getBackdrop_urls().get(0))
                .centerCrop()
                .placeholder(shimmerDrawable)
                .error(R.drawable.backdrop_placeholder_dark)
                .fitCenter()
                .into(iv_backdrop);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mData_cast_crew.size() == 0) {
            sfl_cast.setVisibility(View.VISIBLE);
        } else {
            sfl_cast.setVisibility(View.GONE);
        }
        if (mData_similar_movies.size() == 0) {
            sfl_similar_movies.setVisibility(View.VISIBLE);
        } else {
            sfl_similar_movies.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mData_cast_crew.size() == 0) {
            sfl_cast.setVisibility(View.VISIBLE);
        } else {
            sfl_cast.setVisibility(View.GONE);
        }
        if (mData_similar_movies.size() == 0) {
            sfl_similar_movies.setVisibility(View.VISIBLE);
        } else {
            sfl_similar_movies.setVisibility(View.GONE);
        }

    }

        public String downloadUrl(String myurl) throws IOException {
            Log.d(TAG, "downloadUrl: "+myurl);
            InputStream is = null;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                String contentAsString = readIt(is);
                return contentAsString;
            }  finally {
                if (is != null) {
                    is.close();
                }
            }
        }

    public String readIt(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("fmt_stream_map")) {
                sb.append(line + "\n");
                break;
            }
        }
        reader.close();
        String result = decode(sb.toString());
        String[] url = result.split("\\|");
        return url[1];
    }

    public String decode(String in) {
        String working = in;
        int index;
        index = working.indexOf("\\u");
        while (index > -1) {
            int length = working.length();
            if (index > (length - 6)) break;
            int numStart = index + 2;
            int numFinish = numStart + 4;
            String substring = working.substring(numStart, numFinish);
            int number = Integer.parseInt(substring, 16);
            String stringStart = working.substring(0, index);
            String stringEnd = working.substring(numFinish);
            working = stringStart + ((char) number) + stringEnd;
            index = working.indexOf("\\u");
        }
        return working;
    }

}