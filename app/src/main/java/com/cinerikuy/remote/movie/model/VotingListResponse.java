package com.cinerikuy.remote.movie.model;

import lombok.Data;

@Data
public class VotingListResponse {
    private String movieCode;
    private String name;
    private String duration;
    private String imageUrl;
    private String synopsis;
    private String director;
    private String genre;
    private Boolean voted;
}
