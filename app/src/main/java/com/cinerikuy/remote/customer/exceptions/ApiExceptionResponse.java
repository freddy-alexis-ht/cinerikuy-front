package com.cinerikuy.remote.customer.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApiExceptionResponse{
    private String type;
    private String code;
    private String detail;
    private String httpStatus;
}
