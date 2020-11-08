package com.android.moviestreamer.ui.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.android.moviestreamer.ui.movies.MovieDetailsActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    Context mContext;
    List mData = new ArrayList<>();
    int flags; // 0 == movie, 1==series, 2==livetv
    int SIZE = 0;

    public SearchResultAdapter(Context mContext, List mData, int flags ,int rows, int snapCount ) {
        this.mContext = mContext;
        this.mData = mData;
        this.flags = flags;
        if (mData.size() < snapCount * rows) {
            this.SIZE = mData.size();

        }else{
            this.SIZE = snapCount * rows;

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.serach_result_item, parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (flags == 0) {
            holder.tvMovieName.setText( ((Movie) mData.get(position)).getTitle() );

            String url =  "http://www.test.diljotsingh.com/get_movie_poster/" +((Movie) mData.get(position)).getId() +"?width=200&language=en" ;

            Glide.with(mContext)
                    .load(url)
                    .fitCenter()
                    .into(holder.ivMoviePoster);

            holder.cl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new  Intent(mContext, MovieDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("movie",((Movie) mData.get(position))  );
                    mContext.startActivity(intent);
                }
            });
        }

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
            tvMovieName = itemView.findViewById(R.id.tv_movie_name_search_item);
            ivMoviePoster = itemView.findViewById(R.id.iv_movie_poster_search_item);
            cl_container = itemView.findViewById(R.id.cl_movie_container_search_item);

        }

    }
}
