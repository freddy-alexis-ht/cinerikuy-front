package com.cinerikuy.remote.transaction;



import com.cinerikuy.remote.transaction.model.TransactionBillingResponse;
import com.cinerikuy.remote.transaction.model.TransactionProductRequest;
import com.cinerikuy.remote.transaction.model.TransactionTicketRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ITransaction {

    @POST("buyTickets")
    Call<String> buyTickets(@Body TransactionTicketRequest request);

    @POST("buyProducts")
    Call<String> buyProducts(@Body TransactionProductRequest request);

    @POST("createBilling/{transactionCode}")
    Call<String> createBilling(@Path("transactionCode") String transactionCode);

    @GET("billings/{username}")
    Call<List<TransactionBillingResponse>> findBillings(@Path("username") String username);
}
