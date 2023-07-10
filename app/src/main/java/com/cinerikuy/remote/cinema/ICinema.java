package com.cinerikuy.remote.cinema;

import com.cinerikuy.remote.cinema.model.Cinema;
import com.cinerikuy.remote.cinema.model.CinemaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ICinema {

    @GET("/cinemas")
    Call<List<CinemaResponse>> findAll();

    @GET("/cinemaCode/{cinemaCode}")
    Call<Cinema> findByCinemaCode(@Path("cinemaCode") String cinemaCode);

}
