package com.cinerikuy.remote.cinema;

import com.cinerikuy.remote.cinema.model.CinemaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ICinema {

    @GET("cinemas")
    Call<List<CinemaResponse>> findAll();
}
