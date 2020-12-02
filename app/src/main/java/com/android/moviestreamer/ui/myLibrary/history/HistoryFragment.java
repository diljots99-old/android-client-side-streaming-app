package com.android.moviestreamer.ui.myLibrary.history;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.android.moviestreamer.ui.movies.MoviePosterAdapter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    Context mContext;
    FirebaseAuth mAuth;
    FirebaseUser user;

    List<Object> mHistory = new ArrayList<>();

    RecyclerView rv_history;

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mContext = getActivity().getApplicationContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mContext = getActivity().getApplicationContext();

        rv_history = root.findViewById(R.id.rv_history);


        rv_history.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        rv_history.setItemViewCacheSize(10);
        rv_history.setDrawingCacheEnabled(true);
        rv_history.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);




        getHistory();
        return root;
    }


    void getHistory() {

        String url = mContext.getString(R.string.API_BASE_URL) + mContext.getString(R.string.API_USER_HISTORY) + "{uid}";
        Log.d(TAG, "getHistory: intiated");

        user.getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                Log.d(TAG, "onSuccess: " + getTokenResult.getToken());
                Log.d(TAG, "onSuccess: url " + url);
                AndroidNetworking.get(url).addPathParameter("uid", user.getUid()).addQueryParameter("token", getTokenResult.getToken()).build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(TAG, "onResponse: " + response);

                                for (int index = 0; index < response.length(); index++) {
                                    try {
                                        String type = response.getJSONObject(index).getString("type");
                                        int backref_id = response.getJSONObject(index).getInt("backref_id");
                                        JSONObject data = response.getJSONObject(index).getJSONObject("data");
                                        if(data.length()>0) {
                                            mHistory.add(response.getJSONObject(index));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                RecyclerView.Adapter adapter = new HistoryAdapter(mContext,mHistory);
                                rv_history.setAdapter(adapter);

                                Log.d(TAG, "onResponse: mHistory" + mHistory);
                            }

                            @Override
                            public void onError(ANError anError) {
                                anError.printStackTrace();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mContext = getActivity().getApplicationContext();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mContext = getActivity().getApplicationContext();

    }
}