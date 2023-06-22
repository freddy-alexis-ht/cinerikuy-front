package com.cinerikuy.utilty.listener;

import android.widget.ImageView;

import com.cinerikuy.remote.movie.model.MovieBillboardResponse;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;

public interface MovieItemClickListener {
    void onMovieClick(MovieBillboardResponse movieDetailsResponse, ImageView moviImageView);
}
