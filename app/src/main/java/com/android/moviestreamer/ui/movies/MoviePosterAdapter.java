package com.android.moviestreamer.ui.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.moviestreamer.DashboardActivity;
import com.android.moviestreamer.R;
import com.android.moviestreamer.SplashScreenActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import org.json.JSONObject;

import java.util.List;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {

    private static final String TAG = "MoviePosterAdapter";
    private int SIZE = 0;
    private List<Movie> mData;
    private Context mContext;

    public MoviePosterAdapter(Context mContext, List<Movie> mData) {
        this.mData = mData;
        SIZE = mData.size();
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvMovieName.setText(mData.get(position).getTitle());
        String url = mContext.getString(R.string.API_BASE_URL) + mContext.getString(R.string.API_ENDPOINT_MOVIE_POSTER) + mData.get(position).getId() + "?width=200";

        loadPoster(holder,position,url);

        String get_complete_movie_details_url = mContext.getString(R.string.API_BASE_URL) + mContext.getString(R.string.API_ENDPOINT_MOVIE_DETAILS) + "{id}";

        holder.cl_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.get(position).isComplete()) {
                    Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("movie", mData.get(position));
                    mContext.startActivity(intent);
                } else {
                    AndroidNetworking.get(get_complete_movie_details_url)
                            .addPathParameter("id", String.valueOf(mData.get(position).getId()))
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Movie movie = mData.get(position);
                                    movie.loadMovieData(response);
                                    Log.d(TAG, "onResponse: "+response);
                                    Log.d("TAG", "onResponse: " + movie);
                                    Log.d(TAG, "onResponse: " + movie.getOriginal_title());
                                    Log.d(TAG, "onResponse: " + movie.getGenres());

                                    mData.set(position, movie);

                                    Intent intent = new Intent(mContext, MovieDetailsActivity.class);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("movie", mData.get(position));
                                    mContext.startActivity(intent);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    anError.printStackTrace();
                                }
                            });
                }
            }
        });


    }
    public void loadPoster(final ViewHolder holder, final int position,String url){

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();
//        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).build();


        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Glide.with(mContext)
                .load(url)
                .placeholder(shimmerDrawable)
                .error(R.drawable.poster_placeholder_dark)
                .centerCrop()
                .fitCenter()
                .into(holder.ivMoviePoster);
    }




    @Override
    public int getItemCount() {
        return SIZE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMovieName;
        public ImageView ivMoviePoster;
        public ConstraintLayout cl_container;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tv_movie_name);
            ivMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            cl_container = itemView.findViewById(R.id.cl_movie_container);

        }

    }
}
