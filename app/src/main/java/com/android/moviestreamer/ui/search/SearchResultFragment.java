package com.android.moviestreamer.ui.search;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.android.moviestreamer.ui.movies.MoviePosterAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String TAG = "SearchResultFragment";

    private List<Movie> movies = new ArrayList<>();
    private List<Movie> series = new ArrayList<>();
    private List<Movie> liveTV = new ArrayList<>();
    RecyclerView rv_movies ;
    private RecyclerView.Adapter movieAdapter;


    public SearchResultFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(ArrayList<Movie> param1, ArrayList<Movie> param2, ArrayList<Movie> param3) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, param1);
        args.putParcelableArrayList(ARG_PARAM2, param2);
        args.putParcelableArrayList(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movies = getArguments().getParcelableArrayList(ARG_PARAM1);
            series = getArguments().getParcelableArrayList(ARG_PARAM2);
            liveTV = getArguments().getParcelableArrayList(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_search_result, container, false);

        int spanCount=0;
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 4;
        } else {
            spanCount = 6;
            // code for landscape mode
        }

        rv_movies = root.findViewById(R.id.rv_movie_search_result_fragment);
        rv_movies.setHasFixedSize(true);
        rv_movies.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),4));
        rv_movies.setItemViewCacheSize(5);

        movieAdapter = new SearchResultAdapter( getActivity().getApplicationContext(),movies,0,2,spanCount);
        rv_movies.setAdapter(movieAdapter);

        for(Movie movie:movies){
            Log.d(TAG, "onCreateView: " + movie.getOriginal_title());
        }

        return root;
    }
}