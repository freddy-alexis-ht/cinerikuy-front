package com.cinerikuy.remote.cinema.model;

import lombok.Data;

@Data
public class Cinema {

    private long id;
    private String cinemaCode;
    private String name;
    private String address;
    private String district;
    private double ticketPrice;
    private boolean enable;

}
