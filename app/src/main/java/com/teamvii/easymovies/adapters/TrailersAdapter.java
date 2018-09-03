package com.teamvii.easymovies.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamii.easymovies.R;
import com.teamvii.easymovies.models.Trailer;
import com.teamvii.easymovies.utilities.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mk_25 on 28/03/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private List<Trailer> trailers;
    private TrailerClickHandler trailerClickHandler;

    public TrailersAdapter(TrailerClickHandler trailerClickHandler) {
        this.trailerClickHandler = trailerClickHandler;
    }



    public interface TrailerClickHandler{
        void onClickHandler(Trailer trailer);
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.trailerNameTv.setText(trailer.getName());
        Uri uri = NetworkUtils.youTubeImageUrlBuilder(trailer);
        Picasso.get()
                .load(uri)
                .into(holder.trailerIv);
    }

    @Override
    public int getItemCount() {
        if (trailers != null){
            return trailers.size();
        }else {
            return 0;
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_name_tv)
        TextView trailerNameTv;
        @BindView(R.id.trailer_image)
        ImageView trailerIv;
        TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Trailer trailer = trailers.get(position);
            trailerClickHandler.onClickHandler(trailer);
        }
    }

    public void setTrailers(List<Trailer> trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
    }
}
