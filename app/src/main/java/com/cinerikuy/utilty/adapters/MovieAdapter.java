package com.cinerikuy.utilty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinerikuy.R;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    Context context;
    List<MovieDetailsResponse> movies;

    public MovieAdapter(Context context, List<MovieDetailsResponse> movies) {
        this.context = context;
        this.movies = movies;
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
        holder.imgMovie.setImageResource(movies.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView imgMovie;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_movie_title);
            imgMovie = itemView.findViewById(R.id.item_movie_img);
        }
    }
}
