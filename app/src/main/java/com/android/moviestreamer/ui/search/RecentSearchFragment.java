package com.android.moviestreamer.ui.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentSearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private List<Movie> movies;
    private List<Movie> series;
    private List<Movie> liveTV;


    public RecentSearchFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RecentSearchFragment newInstance(ArrayList<Movie> param1, ArrayList<Movie> param2,ArrayList<Movie> param3) {
        RecentSearchFragment fragment = new RecentSearchFragment();
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
        return inflater.inflate(R.layout.fragment_recent_search, container, false);
    }
}