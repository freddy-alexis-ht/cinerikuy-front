package com.cinerikuy.remote.movie.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MovieDetailsResponse {
    private String movieCode;
    private String name;
    private String duration;
    private String imageUrl;
    private String trailerUrl;
    private String imageCover;
    private String synopsis;
    private boolean peruvian;
    private String director;
    private String actors;
    private List<String> schedules;
    private String genre;
    private String language;
    private String situation;
    private String vote;
    //Valor que referencia a la imagen del trailer
    //private int coverPhoto;

    public MovieDetailsResponse(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

}
