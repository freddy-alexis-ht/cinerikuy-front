package com.cinerikuy.utilty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cinerikuy.R;
import com.cinerikuy.remote.movie.model.MovieBillboardResponse;
import com.cinerikuy.utilty.listener.MovieItemClickListener;

import java.util.List;

public class MovieCartProxAdapter extends RecyclerView.Adapter<MovieCartProxAdapter.ViewHolder> {

    private List<MovieBillboardResponse> movies;
    private Context context;
    MovieItemClickListener movieItemClickListener;

    public MovieCartProxAdapter(List<MovieBillboardResponse> movies, Context context, MovieItemClickListener listener) {
        this.movies = movies;
        this.context = context;
        this.movieItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.movieCode.setText(movies.get(position).getMovieCode());
        Glide.with(context).load(movies.get(position).getImageUrl())
                .into(holder.iv_portada);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_portada;
        private TextView movieCode;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_portada = itemView.findViewById(R.id.iv_portada);
            movieCode = itemView.findViewById(R.id.movieCode);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieItemClickListener.onMovieClick(movies.get(getAdapterPosition()),iv_portada);
                }
            });
        }
    }
}
