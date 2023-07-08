package com.cinerikuy.remote.product;

import com.cinerikuy.remote.product.model.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IProduct {

    @GET("/products")
    Call<List<ProductResponse>> findAll();
}
