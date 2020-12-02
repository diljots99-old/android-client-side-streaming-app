package com.android.moviestreamer.ui.myLibrary.history;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.Movie;
import com.android.moviestreamer.ui.movies.MoviePosterAdapter;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    String TAG = "HistoryAdapter";
    int SIZE = 0;
    Context mContext;
    List<Object> mData = new ArrayList();
    List<Object> mLoadedData = new ArrayList();

    public HistoryAdapter(Context mContext, List<Object> mData) {
        this.SIZE = mData.size();
        Log.d(TAG, "HistoryAdapter: " + mData.size());
        this.mContext = mContext;
        this.mData = mData;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_history, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        JSONObject historyJson = (JSONObject) mData.get(position);

        try {
            String historyID = historyJson.getString("type");
            int backref_id = historyJson.getInt("backref_id");
            JSONObject data = historyJson.getJSONObject("data");
            Log.d(TAG, "onBindViewHolder: " + data);
            if (data.length() > 0) {


                Movie movie = new Movie(data);

                holder.tv_name_history_item.setText(movie.getTitle());

                String posterUrl = mContext.getString(R.string.API_BASE_URL) + mContext.getString(R.string.API_ENDPOINT_MOVIE_POSTER) + movie.getId() + "?width=200";
                String backdropUrl = mContext.getString(R.string.API_BASE_URL) + mContext.getString(R.string.API_ENDPOINT_MOVIE_BACKDROP) + movie.getId() + "?width=200";

                loadPoster(holder, posterUrl);
                loadbackdrop(holder, backdropUrl);



            }else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    public void loadPoster(final HistoryAdapter.ViewHolder holder, String url) {


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
                .into(holder.iv_poster);
    }
    public void loadbackdrop(final HistoryAdapter.ViewHolder holder, String url) {


        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();
//        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).build();


        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Glide.with(mContext)
                .load(url)
                .placeholder(shimmerDrawable)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(200, 100)))
               .centerCrop()
                .into(holder.iv_backdrop);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name_history_item;
        public ImageView iv_poster,iv_backdrop;
        public ImageButton ib_more_history_item;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_history_item = itemView.findViewById(R.id.tv_name_history_item);
            iv_poster = itemView.findViewById(R.id.iv_poster);
            iv_backdrop = itemView.findViewById(R.id.iv_backdrop);
            ib_more_history_item = itemView.findViewById(R.id.ib_more_history_item);

        }

    }
}
