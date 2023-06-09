package com.cinerikuy.remote.movie.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;


@Getter
public class MovieDetailsResponse {
    private String movieCode;
    private String name;
    private String duration;
    private int imageUrl;
    private String trailerUrl;
    private String synopsis;
    private boolean peruvian;
    private String director;
    private String actors;
    private List<String> schedules;
    private String genre;
    private String language;
    private String situation;
    private String vote;

    public MovieDetailsResponse(String movieCode, int imageUrl) {
        this.movieCode = movieCode;
        this.imageUrl = imageUrl;
    }
}
