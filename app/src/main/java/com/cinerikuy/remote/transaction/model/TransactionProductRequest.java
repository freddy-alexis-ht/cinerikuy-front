package com.cinerikuy.remote.transaction.model;

import java.util.HashMap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionProductRequest {

    private String username;
    private HashMap<String, Integer> mapCodeAmount;
    private String cinemaCode;

}
