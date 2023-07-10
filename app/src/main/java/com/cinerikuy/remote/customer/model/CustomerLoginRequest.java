package com.cinerikuy.remote.customer.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerLoginRequest {
    private String username;
    private String password;
}
