package com.cinerikuy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.presenter.Home;
import com.cinerikuy.presenter.NavigationActivity;
import com.cinerikuy.remote.product.IProduct;
import com.cinerikuy.remote.product.model.ProductResponse;
import com.cinerikuy.remote.transaction.ITransaction;
import com.cinerikuy.remote.transaction.model.TransactionProductRequest;
import com.cinerikuy.remote.transaction.model.TransactionTicketRequest;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.adapters.ProductAdapter;
import com.cinerikuy.utilty.listener.ChangeNumberItemListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ITransaction transactionService;
    private String codeTransaction;
    private double totalPago;
    private List<ProductResponse> productAdd = new ArrayList<>();
    private ProgressDialog progressDialog;
    LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_products, container, false);
        Fresco.initialize(getActivity());
        init(view);
        getProductsFromBackend();
        txtTotalCine.setText("S/."+String.valueOf(totalEntradas) + ".0");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String preciTotalEntradas = sharedPreferences.getString("precioTotal","");
        txtTotal.setText("S/."+preciTotalEntradas + ".0");
        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Cancelando Operación", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Intent intent = new Intent(getActivity(), NavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
        linearLayout = view.findViewById(R.id.layout_linear);

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
                        //Log.i("Producto", product.getName() + "-" + product.getCantidad());
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
        linearLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onChangeNumberItem() {
        //Calcular el total de los precios totales por producto
        double totalProductos = 0.0;
        for (ProductResponse product : products) {
            totalProductos += product.getPrecioTotal();
            Log.i("Producto: ", product.getName()!=null?product.getName():"no añadido");
            int cantidad = product.getCantidad();
            if(cantidad > 0) {
                //listProductAdd.put(product.getProductCode(),product.getCantidad());
                //Log.i("HashMap", "-"+listProductAdd.get(product.getProductCode()));
                productAdd.add(product);
            }
        }

        //Mostrar el total en el TextView
        txtTotalProducts.setText("S/."+ String.valueOf(totalProductos));

        totalPago = totalProductos + totalEntradas;
        txtTotal.setText("S/."+String.valueOf(totalPago));
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

        LinearLayout containerLayout = dialog.findViewById(R.id.containerLayout);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Set<String> codigosUnicos = new HashSet<>();
        List<ProductResponse> productosSinDuplicar = new ArrayList<>();
        for (ProductResponse productResponse: productAdd) {
            if (!codigosUnicos.contains(productResponse.getProductCode())) {
                codigosUnicos.add(productResponse.getProductCode());
                productosSinDuplicar.add(productResponse);
            }
        }

        for (ProductResponse productResponse: productosSinDuplicar) {
            LinearLayout itemsProductsLayout = (LinearLayout) inflater.inflate(R.layout.item_product_details, containerLayout, false);
            TextView cantidadProductos = itemsProductsLayout.findViewById(R.id.cantidadProductos);
            cantidadProductos.setText(String.valueOf(productResponse.getCantidad()));

            TextView productName = itemsProductsLayout.findViewById(R.id.productName);
            productName.setText(productResponse.getName());

            TextView precioTotalProduct = itemsProductsLayout.findViewById(R.id.precioTotalProductos);
            precioTotalProduct.setText(String.format("S/. %.2f", productResponse.getPrecioTotal()));
            containerLayout.addView(itemsProductsLayout);
        }


        /*Instanciamos los atributos */
        Button btnVolver = dialog.findViewById(R.id.btn_volver);
        Button btnPagar = dialog.findViewById(R.id.btn_pagar);
        TextView nameMovie = dialog.findViewById(R.id.title_movie);
        TextView nameLocal = dialog.findViewById(R.id.title_local);
        TextView cantEntradas = dialog.findViewById(R.id.cantidadEntradas);
        TextView priceTotalEntradas = dialog.findViewById(R.id.precioTotalEntradas);
        TextView movieHorario = dialog.findViewById(R.id.title_horario);
        TextView total = dialog.findViewById(R.id.total);

        //Obtenemos los valores del SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        String movieName = sharedPreferences.getString("movieName","");
        String cinemaCode = sharedPreferences.getString("cinemaCode","");
        String cineName = sharedPreferences.getString("cinemaName","");
        String cantidadEntradas = sharedPreferences.getString("numberTicket","");
        String preciTotalEntradas = sharedPreferences.getString("precioTotal","");
        String horario = sharedPreferences.getString("horario","");

        nameMovie.setText(movieName);
        nameLocal.setText(cineName);
        cantEntradas.setText(cantidadEntradas);
        priceTotalEntradas.setText("S/."+preciTotalEntradas+".00");
        movieHorario.setText(horario);
        total.setText("S/."+String.valueOf(totalPago)+".0");
        HashMap<String, Integer> listProductAdd = new HashMap<>();
        for (ProductResponse productResponse: productosSinDuplicar) {
            listProductAdd.put(productResponse.getProductCode(),productResponse.getCantidad());
            Log.i("Agregando a HashMap", "-"+listProductAdd.get(productResponse.getProductCode()));
        }

        TransactionProductRequest request = TransactionProductRequest.builder()
                .username(username)
                .cinemaCode(cinemaCode)
                .mapCodeAmount(listProductAdd)
                .build();

        registerTransaction(request, dialog);

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

                //Metodo hacia el Billing
                showProgressDialog("Registrando compra ..");
                registerBillingTransaction(dialog);
            }
        });

        dialog.show();
    }
    private void registerTransaction(TransactionProductRequest request, Dialog dialog) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_TRANSACTION)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        transactionService = retrofit.create(ITransaction.class);
        Call<String> call = transactionService.buyProducts(request);
        //llamar al backend
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    codeTransaction = response.body();
                    Log.i("RESPONSE CODE-TRANSACTION: " , codeTransaction + " ===" + response.body());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ERROR - ", t.getMessage());
            }
        });

    }

    private void registerBillingTransaction(Dialog dialog) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_TRANSACTION)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        transactionService = retrofit.create(ITransaction.class);
        Call<String> call = transactionService.createBilling(codeTransaction);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String rs = response.body();
                    Log.i("RESPONSE", rs);
                    dialog.dismiss();
                    delayAndStartHome();
                }
                Log.i("CodeTransaction", codeTransaction);

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ERROR - ", t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error al procesar la compra", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void delayAndStartHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                //Ir al fragmento Home
                getActivity().finish();
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getActivity(), "Compra realizada", Toast.LENGTH_SHORT).show();
            }
        },3000);
    }
}