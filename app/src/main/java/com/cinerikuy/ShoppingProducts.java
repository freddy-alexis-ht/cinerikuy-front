package com.cinerikuy;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.remote.product.IProduct;
import com.cinerikuy.remote.product.model.ProductResponse;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.adapters.ProductAdapter;
import com.cinerikuy.utilty.listener.ChangeNumberItemListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShoppingProducts extends Fragment implements ChangeNumberItemListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private TextView txtTotalProducts, txtTotalCine, txtTotal;
    private ScrollView scrollView;
    private IProduct productService;
    private List<ProductResponse> products;
    private int totalEntradas;
    private Button btnCancelar, btnPagar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_products, container, false);
        init(view);
        getProductsFromBackend();
        txtTotalCine.setText("S/."+String.valueOf(totalEntradas) + ".00");
        btnCancelar.setOnClickListener(v -> {

        });

        btnPagar.setOnClickListener(v -> {
            showDetailsPurchase(Gravity.CENTER_VERTICAL);
        });

        return view;
    }



    private void init(View view) {
        recyclerViewList = view.findViewById(R.id.recyclerView);
        txtTotalProducts = view.findViewById(R.id.txtTotalProducto);
        txtTotalCine = view.findViewById(R.id.txtTotalEntrada);
        txtTotal = view.findViewById(R.id.txtTotal);
        scrollView = view.findViewById(R.id.scrollView1);
        btnPagar = view.findViewById(R.id.btn_accept);
        btnCancelar = view.findViewById(R.id.btn_cancel);

        Bundle args = getArguments();
        if (args != null) {
            totalEntradas = args.getInt("priceTotalEntradas");

        }
    }
    private void getProductsFromBackend() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_PRODUCTS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        productService = retrofit.create(IProduct.class);
        Call<List<ProductResponse>> call = productService.findAll();
        call.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if(response.isSuccessful()) {
                    products = response.body();
                    for (ProductResponse product: products) {
                        product.setCantidad(0);
                        product.setPrecioTotal(0.0);
                        Log.i("Producto", product.getName() + "-" + product.getCantidad());
                    }
                    initList(products);
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("ERROR",t.getMessage());
            }
        });
    }
    private void initList(List<ProductResponse> productResponses) {
        products = productResponses;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        adapter = new ProductAdapter(productResponses, this);
        recyclerViewList.setAdapter(adapter);
    }


    @Override
    public void onChangeNumberItem() {
        //Calcular el total de los precios totales por producto
        double totalProductos = 0.0;
        for (ProductResponse product : products) {
            totalProductos += product.getPrecioTotal();
        }

        //Mostrar el total en el TextView
        txtTotalProducts.setText("S/."+ String.valueOf(totalProductos));

        double total = totalProductos + totalEntradas;
        txtTotal.setText("S/."+String.valueOf(total));
    }

    public void showDetailsPurchase(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_purchase_detail);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);

        /*Instanciamos los atributos */
        Button btnVolver = dialog.findViewById(R.id.btn_volver);
        Button btnPagar = dialog.findViewById(R.id.btn_pagar);
        TextView nameMovie = dialog.findViewById(R.id.title_movie);
        TextView nameLocal = dialog.findViewById(R.id.title_local);
        TextView cantEntradas = dialog.findViewById(R.id.cantidadEntradas);
        TextView priceTotalEntradas = dialog.findViewById(R.id.precioTotalEntradas);

        //Obtenemos los valores del SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        String movieName = sharedPreferences.getString("movieName","");
        String movieCode = sharedPreferences.getString("movieCode","");
        String cinemaCode = sharedPreferences.getString("cinemaCode","");
        String cineName = sharedPreferences.getString("cinemaName","");
        String cantidadEntradas = sharedPreferences.getString("numberTicket","");
        String preciTotalEntradas = sharedPreferences.getString("precioTotal","");

        nameMovie.setText(movieName);
        nameLocal.setText(cineName);
        cantEntradas.setText(cantidadEntradas);
        priceTotalEntradas.setText(preciTotalEntradas);


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cerrar el dialog y aplicar el filtro para mostrar las peliculas
                Toast.makeText(getActivity(), "Realizando Transacción", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void getValueSharedPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        Log.i("USERNAME", username);
    }
}