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
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;
import com.cinerikuy.utilty.listener.MovieItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    Context context;
    List<MovieBillboardResponse> movies;
    MovieItemClickListener movieItemClickListener;

    public MovieAdapter(Context context, List<MovieBillboardResponse> movies, MovieItemClickListener listener) {
        this.context = context;
        this.movies = movies;
        movieItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(movies.get(position).getName());
        holder.code.setText(movies.get(position).getMovieCode());
        String imgUrl = movies.get(position).getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(imgUrl)
                .into(holder.imgMovie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, code;
        private ImageView imgMovie;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_movie_title);
            imgMovie = itemView.findViewById(R.id.item_movie_img);
            code = itemView.findViewById(R.id.item_movie_code);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieItemClickListener.onMovieClick(movies.get(getAdapterPosition()),imgMovie);
                }
            });
        }
    }
}
