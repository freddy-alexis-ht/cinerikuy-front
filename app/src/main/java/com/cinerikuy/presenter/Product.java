package com.cinerikuy.presenter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.R;
import com.cinerikuy.TicketPurchase;
import com.cinerikuy.remote.cinema.ICinema;
import com.cinerikuy.remote.cinema.model.CinemaResponse;
import com.cinerikuy.remote.product.IProduct;
import com.cinerikuy.remote.product.model.ProductResponse;
import com.cinerikuy.remote.transaction.ITransaction;
import com.cinerikuy.remote.transaction.model.TransactionProductRequest;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.adapters.ProductAdapter;
import com.cinerikuy.utilty.listener.ChangeNumberItemListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

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

public class Product extends Fragment implements ChangeNumberItemListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private TextView txtTotal, nameLocal;
    private ScrollView scrollView;
    private IProduct productService;
    private List<ProductResponse> products;
    private Button btnCancelar, btnPagar, btnFiltro;
    private ITransaction transactionService;
    private String codeTransaction;
    private double totalPago;
    private List<ProductResponse> productAdd = new ArrayList<>();
    private ProgressDialog progressDialog;
    LinearLayout linearLayout;
    private String cinemaCode;
    private String cinemaName = "";
    private ICinema cinemaService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        Fresco.initialize(getActivity());
        init(view);
        getProductsFromBackend();
        btnCancelar.setOnClickListener(v -> {

        });

        btnPagar.setOnClickListener(v -> {
            if(!StringUtils.isNotBlank(cinemaName)) {
                Toast.makeText(getActivity(), "Debe seleccionar un local", Toast.LENGTH_SHORT).show();
            }
            if(totalPago <= 0) {
                Toast.makeText(getActivity(), "Debe escoger por lo menos un producto", Toast.LENGTH_SHORT).show();
            }
            if(StringUtils.isNotBlank(cinemaName) && totalPago >0) {
                showDetailsPurchase(Gravity.CENTER_VERTICAL);
            }

        });

        btnFiltro.setOnClickListener(filtro -> {
            showWindowFilterCines(Gravity.CENTER_VERTICAL);
        });
        return view;

    }

    private void init(View view) {
        recyclerViewList = view.findViewById(R.id.recyclerView);
        txtTotal = view.findViewById(R.id.txtTotal);
        scrollView = view.findViewById(R.id.scrollView1);
        btnPagar = view.findViewById(R.id.btn_accept);
        btnCancelar = view.findViewById(R.id.btn_cancel);
        linearLayout = view.findViewById(R.id.layout_linear);
        btnFiltro = view.findViewById(R.id.btnFiltro);
        nameLocal = view.findViewById(R.id.name_local);
    }

    public void showWindowFilterCines(int gravity){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_filter_location);
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
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnAcept = dialog.findViewById(R.id.btn_accept);

        Spinner spinner = dialog.findViewById(R.id.spinner);

        getCinemasFromBackend(new Product.CinemaCallback() {
            @Override
            public void onCinemasReceived(ArrayList<CinemaResponse> cines) {
                ArrayList<String> cineNames = new ArrayList<>();
                cineNames.add("--Seleccione--");

                for (CinemaResponse cine: cines){
                    cineNames.add(cine.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.style_spinner, cineNames);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position > 0) {
                            CinemaResponse selectedCine = cines.get(position - 1);
                            cinemaCode = selectedCine.getCinemaCode();
                            cinemaName = cines.get(position - 1).getName() + " - " + cines.get(position - 1).getAddress();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cerrar el dialog y aplicar el filtro para mostrar las peliculas
                nameLocal.setText(cinemaName);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void getCinemasFromBackend(final Product.CinemaCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_CINEMA)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cinemaService = retrofit.create(ICinema.class);
        Call<List<CinemaResponse>> call = cinemaService.findAll();
        call.enqueue(new Callback<List<CinemaResponse>>() {
            @Override
            public void onResponse(Call<List<CinemaResponse>> call, Response<List<CinemaResponse>> response) {
                if (response.isSuccessful()) {
                    List<CinemaResponse> listCines = response.body();
                    ArrayList<CinemaResponse> cines = new ArrayList<>();
                    if (listCines != null) {
                        cines.addAll(listCines);
                    }
                    callback.onCinemasReceived(cines);
                }
            }
            @Override
            public void onFailure(Call<List<CinemaResponse>> call, Throwable t) {
                Log.e("Error: ", t.getMessage());
            }
        });
    }
    private interface CinemaCallback {
        void onCinemasReceived(ArrayList<CinemaResponse> cines);
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

    private void showDetailsPurchase(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_product_detail);
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
        TextView nameUser = dialog.findViewById(R.id.userName);
        TextView total = dialog.findViewById(R.id.total);
        TextView nameLocal = dialog.findViewById(R.id.title_local);

        //Obtenemos los valores del SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        nameUser.setText(username);
        nameLocal.setText(cinemaName);
        total.setText("S/."+String.valueOf(totalPago));
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
        txtTotal.setText("S/."+ String.valueOf(totalProductos));
        totalPago = totalProductos;
        //txtTotal.setText("S/."+String.valueOf(totalPago));
    }



    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
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

    private void delayAndStartHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Compra realizada", Toast.LENGTH_SHORT).show();
            }
        },3000);
    }
}