package com.cinerikuy.remote.movie.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VotingUpRequest {
    private String movieCode;
    private String username;
}
