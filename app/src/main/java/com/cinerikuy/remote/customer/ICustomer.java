package com.cinerikuy.remote.customer;

import com.cinerikuy.remote.customer.model.CustomerLoginRequest;
import com.cinerikuy.remote.customer.model.CustomerResponse;
import com.cinerikuy.remote.customer.model.CustomerSignInRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ICustomer {

    @POST("signin")
    Call<CustomerResponse> sigInd(@Body CustomerSignInRequest request);

    @POST("login")
    Call<CustomerResponse> login(CustomerLoginRequest request);
}
