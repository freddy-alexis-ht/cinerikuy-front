package com.cinerikuy.utilty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cinerikuy.R;
import com.cinerikuy.remote.product.model.ProductResponse;
import com.cinerikuy.utilty.listener.ChangeNumberItemListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductResponse> products;
    private ChangeNumberItemListener changeNumberItemListener;

    public ProductAdapter(List<ProductResponse> products, ChangeNumberItemListener changeNumberItemListener) {
        this.products = products;
        this.changeNumberItemListener = changeNumberItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View infalte = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(infalte);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(products.get(position).getName());
        holder.priceUnit.setText(String.valueOf(products.get(position).getPrice()));
        String imgUrl = products.get(position).getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(imgUrl)
                .into(holder.pic);

        double total = products.get(position).getPrecioTotal();


        holder.precioTotal.setText(String.valueOf(total));

        holder.plusItem.setOnClickListener( v -> {
            int quantity = products.get(position).getCantidad();
            int newQuantity = quantity + 1;
            holder.cantidad.setText(String.valueOf(newQuantity));

            double priceUnit = Double.parseDouble(products.get(position).getPrice());
            double newTotal = newQuantity * priceUnit;
            holder.precioTotal.setText(String.valueOf(newTotal));

            products.get(position).setCantidad(newQuantity);
            products.get(position).setPrecioTotal(newTotal);

            // Notificar al listener el cambio en la cantidad
            if (changeNumberItemListener != null) {
                changeNumberItemListener.onChangeNumberItem();
            }
        });

        holder.minusItem.setOnClickListener(v -> {
            int quantity = products.get(position).getCantidad();
            if (quantity > 0) {
                int newQuantity = quantity - 1;
                holder.cantidad.setText(String.valueOf(newQuantity));

                double priceUnit = Double.parseDouble(products.get(position).getPrice());
                double newTotal = newQuantity * priceUnit;
                holder.precioTotal.setText(String.valueOf(newTotal));

                products.get(position).setCantidad(newQuantity);
                products.get(position).setPrecioTotal(newTotal);

                // Notificar al listener el cambio en la cantidad
                if (changeNumberItemListener != null) {
                    changeNumberItemListener.onChangeNumberItem();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, priceUnit;
        ImageView pic, plusItem, minusItem;
        TextView precioTotal, cantidad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.nameProduct);
            priceUnit = itemView.findViewById(R.id.feeEachItem);
            pic = itemView.findViewById(R.id.picCine);
            plusItem = itemView.findViewById(R.id.plusBtn);
            minusItem = itemView.findViewById(R.id.minusBtn);
            precioTotal = itemView.findViewById(R.id.totalEachItem);
            cantidad = itemView.findViewById(R.id.cantidad);
        }
    }
}
