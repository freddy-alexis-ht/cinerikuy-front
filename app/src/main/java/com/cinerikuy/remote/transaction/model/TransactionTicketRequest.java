package com.cinerikuy.remote.transaction.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionTicketRequest {
    private String username;
    private String cinemaCode;
    private String movieCode;
    private String movieSchedule;
    private int movieNumberOfTickets;
}
