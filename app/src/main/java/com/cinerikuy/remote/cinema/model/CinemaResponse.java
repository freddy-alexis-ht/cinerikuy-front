package com.cinerikuy.remote.cinema.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaResponse {
    private String cinemaCode;
    private String name;
    private String address;
    private String district;
    private String city;
    private String ticketPrice;
}
