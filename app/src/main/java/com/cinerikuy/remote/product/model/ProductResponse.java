package com.cinerikuy.remote.product.model;

import lombok.Data;

@Data
public class ProductResponse {
    private String productCode;
    private String name;
    private String imageUrl;
    private String price;

    private int cantidad;
    private double precioTotal;
}
