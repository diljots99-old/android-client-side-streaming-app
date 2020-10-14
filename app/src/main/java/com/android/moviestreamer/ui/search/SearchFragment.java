package com.android.moviestreamer.ui.search;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    String TAG = "SearchFragment";

    ArrayList<Movie> moviesList = new ArrayList<>();
    ArrayList<Movie> seriesList = new ArrayList<>();
    ArrayList<Movie> liveTvList = new ArrayList<>();




    private MaterialSearchBar searchView = null;
    RecentSearchFragment recentSearchFragment;
    SearchResultFragment searchResultFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = root.findViewById(R.id.sv_search_view);

        swapRecentFragment();


        searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                Log.d(TAG, "onSearchStateChanged: "+enabled);
                if (enabled == false){
                    swapRecentFragment();
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
//                String url = "www.test.diljotsingh.com/search/movie?query="+text+"&fetch_length=10";
                    searchMovies(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                Log.d(TAG, "onButtonClicked: +" +buttonCode);
                switch (buttonCode){
                    case MaterialSearchBar.BUTTON_NAVIGATION:swapRecentFragment();
                                                            break;


                }

            }
        });


        return root;
    }

    void swapRecentFragment(){
        recentSearchFragment = new RecentSearchFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_search_fragment,recentSearchFragment).addToBackStack(null).commit();
    }

    public void searchMovies(String query){
        String url = " http://www.test.diljotsingh.com/search/movie";
        moviesList = new ArrayList<>();


        Log.d(TAG, "onSearchConfirmed: url "+url);
        Log.d(TAG, "onSearchConfirmed: url "+query);

        AndroidNetworking.get(url).addQueryParameter("query",  query).addQueryParameter("fetch_length","10").build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    Log.d(TAG, "onResponse: "+response.get("total_results"));
                    JSONArray jsonArray= response.getJSONArray("results");
                    for(int index=0 ; index<jsonArray.length();index++){
                        Movie movie = new Movie(jsonArray.getJSONObject(index));
                        movie.loadMovieData(jsonArray.getJSONObject(index));
                        moviesList.add(movie);
                    }
                    SearchResultFragment searchResultFragment =  SearchResultFragment.newInstance(moviesList,seriesList,liveTvList);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_search_fragment,searchResultFragment).addToBackStack(null).commit();

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void onError(ANError anError) {
                Log.d(TAG, "onError: "+anError);
            }
        });
    }


}