package com.android.moviestreamer.ui.movies;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.moviestreamer.R;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private static final String TAG = "MoviesFragment";
    private RecyclerView rv_now_playing,rv_popular,rv_top_rated,rv_new_releases;
    private ShimmerFrameLayout sfl_now_playing,sfl_popular,sfl_top_rated,sfl_new_releases;
    List<Movie> mData_top_Rated = new ArrayList<>();
    List<Movie> mData_popular = new ArrayList<>();

    List<Movie> mData_now_playing = new ArrayList<>();
    List<Movie> mData_new_releases = new ArrayList<>();

    Context mContext;


    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity().getApplicationContext();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_movies, container, false);

        rv_now_playing = root.findViewById(R.id.rv_now_playing);
        rv_popular = root.findViewById(R.id.rv_popular);
        rv_top_rated = root.findViewById(R.id.rv_top_rated);
        rv_new_releases = root.findViewById(R.id.rv_new_releases);

        sfl_top_rated = root.findViewById(R.id.sfl_top_rated);
        sfl_now_playing = root.findViewById(R.id.sfl_now_playing);
        sfl_popular = root.findViewById(R.id.sfl_popular);
        sfl_new_releases = root.findViewById(R.id.sfl_new_releases);

        mContext = getActivity().getApplicationContext();

        Initialize_New_Releases();
        Initialize_Now_Playing();
        Initialize_Popular();
        Initialize_Top_Rated();


        return root;
    }

    public  void Initialize_New_Releases(){
        rv_new_releases.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        rv_new_releases.setItemViewCacheSize(10);
        rv_new_releases.setDrawingCacheEnabled(true);
        rv_new_releases.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_MOVIE_NEW_RELEASES_MOVIES);

        AndroidNetworking.get(url)
                .addQueryParameter("no_of_pages","4")
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                Log.d(TAG,"onResponse: Length "+response.length());
                try {
                    JSONArray listOfMovies = (JSONArray) response.get("results");
                    int length = (int) response.get("length");
                    Log.d(TAG, "onResponse: "+listOfMovies);
                    Log.d(TAG, "onResponse: "+listOfMovies.length());

                    Log.d(TAG, "onResponse: "+length);
                    for (int index=0;index<listOfMovies.length();index++  ){
                        JSONObject movieJSON = (JSONObject) listOfMovies.get(index);
                        Movie movie = new Movie(movieJSON);
                        mData_new_releases.add(movie);
                        Log.d(TAG, "onResponse: "+movie);
                    }

                    RecyclerView.Adapter adapter = new MoviePosterAdapter(mContext,mData_new_releases);
                    rv_new_releases.setAdapter(adapter);
                    sfl_new_releases.stopShimmer();
                    sfl_new_releases.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: "+anError);
            }
        });
    }
    public  void Initialize_Now_Playing(){
        rv_now_playing.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        rv_now_playing.setItemViewCacheSize(10);
        rv_now_playing.setDrawingCacheEnabled(true);
        rv_now_playing.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_MOVIE_UPCOMING_MOVIES);

        AndroidNetworking.get(url)
                .addQueryParameter("no_of_pages","4")
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                Log.d(TAG,"onResponse: Length "+response.length());
                try {
                    JSONArray listOfMovies = (JSONArray) response.get("results");
                    int length = (int) response.get("length");
                    Log.d(TAG, "onResponse: "+listOfMovies);
                    Log.d(TAG, "onResponse: "+listOfMovies.length());

                    Log.d(TAG, "onResponse: "+length);
                    for (int index=0;index<listOfMovies.length();index++  ){
                        JSONObject movieJSON = (JSONObject) listOfMovies.get(index);
                        Movie movie = new Movie(movieJSON);
                        mData_now_playing.add(movie);
                        Log.d(TAG, "onResponse: "+movie);
                    }

                    RecyclerView.Adapter adapter = new MoviePosterAdapter(mContext,mData_now_playing);
                    rv_now_playing.setAdapter(adapter);
                    sfl_now_playing.stopShimmer();
                    sfl_now_playing.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: "+anError);
            }
        });
    }

    public  void Initialize_Popular(){
        rv_popular.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        rv_popular.setItemViewCacheSize(10);
        rv_popular.setDrawingCacheEnabled(true);
        rv_popular.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_MOVIE_POPULAR_MOVIES);


        AndroidNetworking.get(url)
                .addQueryParameter("no_of_pages","4")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                Log.d(TAG,"onResponse: Length "+response.length());
                try {
                    JSONArray listOfMovies = (JSONArray) response.get("results");
                    int length = (int) response.get("length");
                    Log.d(TAG, "onResponse: "+listOfMovies);
                    Log.d(TAG, "onResponse: "+listOfMovies.length());

                    Log.d(TAG, "onResponse: "+length);

                    for (int index=0;index<listOfMovies.length();index++  ){
                        JSONObject movieJSON = (JSONObject) listOfMovies.get(index);
                        Movie movie = new Movie(movieJSON);
                        mData_popular.add(movie);
                        Log.d(TAG, "onResponse: "+movie);
                    }

                    RecyclerView.Adapter adapter = new MoviePosterAdapter(mContext,mData_popular);
                    rv_popular.setAdapter(adapter);
                    sfl_popular.stopShimmer();
                    sfl_popular.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: "+anError);
            }
        });
    }
    public  void Initialize_Top_Rated(){
        rv_top_rated.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        rv_top_rated.setItemViewCacheSize(10);
        rv_top_rated.setDrawingCacheEnabled(true);
        rv_top_rated.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        String url = getString(R.string.API_BASE_URL) + getString(R.string.API_MOVIE_TOP_RATED_MOVIES);


        AndroidNetworking.get(url)
                .addQueryParameter("no_of_pages","4")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                Log.d(TAG,"onResponse: Length "+response.length());
                try {
                    JSONArray listOfMovies = (JSONArray) response.get("results");
                    int length = (int) response.get("length");
                    Log.d(TAG, "onResponse: "+listOfMovies);
                    Log.d(TAG, "onResponse: "+listOfMovies.length());

                    Log.d(TAG, "onResponse: "+length);
                    for (int index=0;index<listOfMovies.length();index++  ){
                        JSONObject movieJSON = (JSONObject) listOfMovies.get(index);
                        Movie movie = new Movie(movieJSON);
                        mData_top_Rated.add(movie);
                        Log.d(TAG, "onResponse: "+movie);
                    }

                    RecyclerView.Adapter adapter = new MoviePosterAdapter(mContext,mData_top_Rated);
                    rv_top_rated.setAdapter(adapter);

                    sfl_top_rated.stopShimmer();
                    sfl_top_rated.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ANError anError) {
                Log.d("Oncreate", "onError: "+anError);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mData_now_playing.isEmpty())
            sfl_now_playing.setVisibility(View.VISIBLE);
        else
            sfl_now_playing.setVisibility(View.GONE);

        if (mData_popular.isEmpty())
            sfl_popular.setVisibility(View.VISIBLE);
        else
            sfl_popular.setVisibility(View.GONE);

        if (mData_top_Rated.isEmpty())
            sfl_top_rated.setVisibility(View.VISIBLE);
        else
            sfl_top_rated.setVisibility(View.GONE);

        if (mData_new_releases.isEmpty())
            sfl_new_releases.setVisibility(View.VISIBLE);
        else
            sfl_new_releases.setVisibility(View.GONE);


        mContext = getActivity().getApplicationContext();

    }

    @Override
    public void onPause() {
        super.onPause();
        sfl_popular.stopShimmer();
        sfl_top_rated.stopShimmer();
        sfl_now_playing.stopShimmer();
        sfl_new_releases.stopShimmer();
    }
}