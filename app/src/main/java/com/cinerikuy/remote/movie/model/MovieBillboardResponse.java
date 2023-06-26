package com.cinerikuy.remote.movie.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieBillboardResponse {
    private String movieCode;
    private String name;
    private String duration;
    private String imageUrl;
    private String genre;
}
