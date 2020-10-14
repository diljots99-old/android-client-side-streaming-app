package com.android.moviestreamer.ui.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.moviestreamer.R;
import com.android.moviestreamer.media_player.TorrentStreammer;
import com.android.moviestreamer.ui.people.PeoplePosterAdapter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private String TAG = "MovieDetailsActivity";

    TextView tv_movie_name, tv_icon_restricted, tv_geners, tv_runtime, tv_user_rating, tv_overview;
    RecyclerView rv_cast_and_crew;
    Button btn_watch_now;
    ImageView iv_backdrop;
    Movie movie;
    ShimmerFrameLayout sfl_cast;
    List<JSONObject> mData = new ArrayList<>();


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
        tv_user_rating = findViewById(R.id.tv_user_rating) ;
        tv_overview = findViewById(R.id.tv_overview);
        iv_backdrop = findViewById(R.id.iv_movie_backdrop);
        btn_watch_now = findViewById(R.id.btn_watch_now);
        rv_cast_and_crew = findViewById(R.id.rv_cast_and_crew);
        sfl_cast = findViewById(R.id.sfl_cast);

        if (mData.size()==0){
            sfl_cast.setVisibility(View.VISIBLE);
        }else{
            sfl_cast.setVisibility(View.GONE);
        }


        btn_watch_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sources = movie.getSources();
                try {
                    JSONObject sources_jsonObject = new JSONObject(sources);
                    JSONArray torrents = sources_jsonObject.getJSONArray("torrents");
                    int Healthy_torrent_index = 0;
                    int min_Seeds = 0;

                    for (int index = 0 ; index <torrents.length();index++){
                        int seeds = ((JSONObject) torrents.get(index)).getInt("seeds");
                        String quality  = ((JSONObject) torrents.get(index)).getString("quality");
                        if(min_Seeds < seeds && quality.contains("720p")){
                            min_Seeds = seeds;
                            Healthy_torrent_index =index;
                            Log.d(TAG, "onClick: "+Healthy_torrent_index);
                        }
                    }

                    String url = ( (JSONObject) torrents.get(Healthy_torrent_index)).getString("url");
                    Log.d(TAG, "onClick: " + url);
                    Log.d(TAG, "onClick: "+ torrents);

                    Intent intent = new Intent(MovieDetailsActivity.this, TorrentStreammer.class);
                    intent.putExtra("Movie", movie);
                    intent.putExtra("url",url);
                    startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        tv_movie_name.setText(movie.getTitle());

        if (movie.isAdult()){
            tv_icon_restricted.setVisibility(View.VISIBLE);
        }else{
            tv_icon_restricted.setVisibility(View.GONE);
        }
        tv_user_rating.setText( ((int) movie.getVote_average()*10) + "% user likes this after watching");
        tv_runtime.setText(getRuntimeSTR());
        tv_overview.setText(movie.getOverview());
        tv_geners.setText(getGenreSTR());

        setMovieBackdrop();
        setCastAndCrew();

    }

    void setCastAndCrew(){
        rv_cast_and_crew.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false));
        rv_cast_and_crew.setItemViewCacheSize(10);
        rv_cast_and_crew.setDrawingCacheEnabled(true);
        rv_cast_and_crew.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mData.clear();
        sfl_cast.setVisibility(View.VISIBLE);


        AndroidNetworking.get("http://www.test.diljotsingh.com//movie/credits/"+movie.getId()).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray cast = response.getJSONArray("cast");

                    JSONArray crew = response.getJSONArray("crew");


                    for(int index = 0 ; index<cast.length();index++){
                                JSONObject castObject  = cast .getJSONObject(index);
                                castObject.put("type","cast");
                                mData.add(castObject);
                        Log.d(TAG, "onResponse: "+castObject);
                    }
                    for(int index = 0 ; index<crew.length();index++){
                        JSONObject castObject  = crew .getJSONObject(index);
                        castObject.put("type","crew");
                        mData.add(castObject);
                        Log.d(TAG, "onResponse: "+castObject);

                    }

                    RecyclerView.Adapter adapter = new PeoplePosterAdapter(MovieDetailsActivity.this,mData);
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

    String getRuntimeSTR(){
        int runtime = movie.getRuntime();
        int hours = runtime / 60;
        int minutes = runtime % 50;
        String runtimeSTR = tv_runtime.getText().toString();

        if (hours>0){ runtimeSTR = hours+"h"; }

        if (minutes > 0){ runtimeSTR += " "+minutes+"m"; }

        return  runtimeSTR;
    }

    String getGenreSTR(){
        String genreSTR = tv_geners.getText().toString();

        for (String genre : movie.getGenres()){
            genreSTR += genre + ", ";
        };
        return  genreSTR;
    }

    void  setMovieBackdrop(){
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();



        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        Glide.with(MovieDetailsActivity.this)
                .load(movie.getBackdrop_urls().get(0))
                .centerCrop()
                .placeholder(shimmerDrawable)
                .fitCenter()
                .into(iv_backdrop);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mData.size()==0){
            sfl_cast.setVisibility(View.VISIBLE);
        }else{
            sfl_cast.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mData.size()==0){
            sfl_cast.setVisibility(View.VISIBLE);
        }else{
            sfl_cast.setVisibility(View.GONE);
        }
    }
}