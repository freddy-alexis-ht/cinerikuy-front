package com.cinerikuy.remote.customer.model;

import lombok.Builder;

@Builder
public class CustomerLoginRequest {
    private String username;
    private String password;
}
