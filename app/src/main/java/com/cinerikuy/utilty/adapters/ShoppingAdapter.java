package com.cinerikuy.utilty.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinerikuy.R;
import com.cinerikuy.remote.transaction.model.TransactionBillingResponse;
import com.cinerikuy.utilty.Utils;

import java.util.List;

public class ShoppingAdapter  extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder>{
    private List<TransactionBillingResponse> transactions;
    public ShoppingAdapter(List<TransactionBillingResponse> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View infalte = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoppings,parent,false);
        return new ShoppingAdapter.ViewHolder(infalte);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.transaction.setText(transactions.get(position).getTransactionCode());
        holder.priceTotal.setText("S/."+transactions.get(position).getTotalCost());
        holder.cinemaName.setText(transactions.get(position).getCinemaName());
        holder.movieName.setText(transactions.get(position).getMovieName() + " - " + transactions.get(position).getMovieSchedule());
        String fecha = transactions.get(position).getDate();
        holder.date.setText(Utils.convertDateFormat(fecha));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView transaction, priceTotal,cinemaName, movieName, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction = itemView.findViewById(R.id.title_transaction);
            priceTotal = itemView.findViewById(R.id.total);
            cinemaName = itemView.findViewById(R.id.title_local);
            movieName = itemView.findViewById(R.id.title_movie);
            date = itemView.findViewById(R.id.fecha);
        }
    }
}
