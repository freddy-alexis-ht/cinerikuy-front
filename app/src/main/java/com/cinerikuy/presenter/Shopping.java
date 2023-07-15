package com.cinerikuy.presenter;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cinerikuy.R;
import com.cinerikuy.remote.transaction.ITransaction;
import com.cinerikuy.remote.transaction.model.TransactionBillingResponse;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.Utils;
import com.cinerikuy.utilty.adapters.ProductAdapter;
import com.cinerikuy.utilty.adapters.ShoppingAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Shopping extends Fragment {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private ITransaction transactionService;
    private List<TransactionBillingResponse> transactions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        init(view);
        getTransactionFromBackend();
        return view;
    }
    private void init(View view) {
        recyclerViewList = view.findViewById(R.id.recyclerView12);
    }

    private void getTransactionFromBackend() {
        //Obtenemos los valores del SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_TRANSACTION)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        transactionService = retrofit.create(ITransaction.class);
        Call<List<TransactionBillingResponse>> call = transactionService.findBillings(username);
        call.enqueue(new Callback<List<TransactionBillingResponse>>() {
            @Override
            public void onResponse(Call<List<TransactionBillingResponse>> call, Response<List<TransactionBillingResponse>> response) {
                if (response.isSuccessful()) {
                    transactions = response.body();
                    Utils.logResponse(transactions);
                    initList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<TransactionBillingResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al consultar Transacciones", Toast.LENGTH_SHORT).show();
                Log.e("Error - Transactions", t.getMessage());
            }
        });
    }
    private void initList(List<TransactionBillingResponse> response) {
        transactions = response;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        adapter = new ShoppingAdapter(transactions);
        recyclerViewList.setAdapter(adapter);
    }
}