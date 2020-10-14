package com.android.moviestreamer.ui.people;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.moviestreamer.R;
import com.android.moviestreamer.ui.movies.MoviePosterAdapter;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PeoplePosterAdapter extends RecyclerView.Adapter<PeoplePosterAdapter.ViewHolder> {

    private static final String TAG = "PeoplePosterAdapter";

    Context mContext;
    List<JSONObject> mData;

    public PeoplePosterAdapter(Context mContext, List<JSONObject> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_poster_item,parent,false);
        return new PeoplePosterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.tv_person_name.setText(mData.get(position).getString("name"));

            if (mData.get(position).getString("type").contains("cast")){
                holder.tv_character_name.setText(mData.get(position).getString("character"));
            }
            else{
                holder.tv_character_name.setText(mData.get(position).getString("job"));

            }
            Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).setBaseAlpha(0.9f).setHighlightAlpha(0.8f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT).build();
//        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setAutoStart(true).build();
            String url = "http://www.test.diljotsingh.com/people/profile_image/" + mData.get(position).getInt("id") +"?width=200";



            ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
            shimmerDrawable.setShimmer(shimmer);
            Glide.with(mContext)
                    .load(url)
                    .placeholder(shimmerDrawable)

                    .centerCrop()
                    .into(holder.iv_people_poster);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_character_name,tv_person_name;
        public ImageView iv_people_poster;
        public ConstraintLayout cl_container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_character_name = itemView.findViewById(R.id.tv_character_name);
            tv_person_name = itemView.findViewById(R.id.tv_person_name);
            iv_people_poster = itemView.findViewById(R.id.iv_people_poster);

        }
    }
}
