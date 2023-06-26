package com.cinerikuy.remote.movie;

import com.cinerikuy.remote.movie.model.MovieBillboardResponse;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IMovie {
    @GET("billboard")
    Call<List<MovieBillboardResponse>> getMainBillboard();

    @GET("{movieCode}/details")
    Call<MovieDetailsResponse> getMovieDetails(@Path("movieCode") String movieCode);

    @GET("/billboard/{cinemaCode}")
    Call<List<MovieBillboardResponse>> getSpecificBillboard(@Path("cinemaCode") String cinemaCode);
}
