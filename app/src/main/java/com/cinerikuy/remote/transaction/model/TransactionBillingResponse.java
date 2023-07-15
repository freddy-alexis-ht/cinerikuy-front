package com.cinerikuy.remote.transaction.model;

import lombok.Data;

@Data
public class TransactionBillingResponse {
    private String transactionCode;
    private String cinemaName;
    private String movieName;
    private String movieSchedule;
    private String date;
    private String totalCost;
}
