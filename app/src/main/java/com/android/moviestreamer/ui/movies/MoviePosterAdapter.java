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
import com.bumptech.glide.request.target.CustomTarget;
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

    public MoviePosterAdapter(Context mContext,List<Movie> mData) {
            this.mData = mData;
            SIZE = mData.size();
            this.mContext = mContext;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.tvMovieName.setText(mData.get(position).getTitle());
            String url = "http://www.test.diljotsingh.com/get_movie_poster/" + mData.get(position).getId() +"?width=200&language=en";

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();
//        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).build();



        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
            Glide.with(mContext)
                    .load(url)
                    .placeholder(shimmerDrawable)

                    .centerCrop()
                    .into(holder.ivMoviePoster);



        AndroidNetworking.get("http://www.test.diljotsingh.com/get_complete_movie_details/"+mData.get(position).getId())

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Movie movie = mData.get(position);
                        movie.loadMovieData(response);
                        Log.d("TAG", "onResponse: "+movie);
                        Log.d(TAG, "onResponse: "+ movie.getOriginal_title());
                        mData.set(position,movie);
                        holder.cl_container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new  Intent(mContext, MovieDetailsActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("movie",mData.get(position));
                                mContext.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });



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