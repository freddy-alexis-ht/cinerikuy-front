package com.cinerikuy.remote.customer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerSignInRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String dni;
    private String cellphone;
}
