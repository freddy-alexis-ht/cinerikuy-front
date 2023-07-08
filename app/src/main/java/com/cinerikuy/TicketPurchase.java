package com.cinerikuy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cinerikuy.presenter.DetailsMovie;
import com.cinerikuy.presenter.Home;
import com.cinerikuy.remote.cinema.ICinema;
import com.cinerikuy.remote.cinema.model.Cinema;
import com.cinerikuy.remote.cinema.model.CinemaResponse;
import com.cinerikuy.remote.movie.IMovie;
import com.cinerikuy.remote.movie.model.MovieBillboardResponse;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;
import com.cinerikuy.remote.movie.model.Schedule;
import com.cinerikuy.remote.transaction.ITransaction;
import com.cinerikuy.remote.transaction.model.TransactionTicketRequest;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.adapters.ScheduleAdapter;
import com.cinerikuy.utilty.listener.ScheduleItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketPurchase extends Fragment implements ScheduleItemClickListener {
    private RecyclerView rvShedules;
    private ImageView imgCoverMovieView, imgMovieView;
    private TextView nameMovieView, nameLocal, movieIdioma, duration;
    private ScheduleAdapter scheduleAdapter;
    private FloatingActionButton play;
    private IMovie movieService;
    private CountDownTimer countDownTimer;
    private TextView txtCantidad, txtTotal;
    private Button btnCancel, btnContinuar, btnFiltro;
    private ImageView plus, minus;
    private int precio;
    private int value;
    private int precioTotal;
    private ICinema cinemaService;
    private String cinemaCode;
    private String cinemaName = "";
    private String horario = "";
    private ITransaction transactionService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_purchase, container, false);
        initView(view);
        btnCancel.setOnClickListener(cancel -> {
            Toast.makeText(getActivity(), "Cancelando Operación", Toast.LENGTH_SHORT).show();
            Fragment fragmentDetailMovie = new Home();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentDetailMovie);
            //transaction.addToBackStack(null);
            transaction.commit();
        });

        btnContinuar.setOnClickListener(next -> {
            String name = nameLocal.getText().toString();
            int cant = Integer.parseInt(txtCantidad.getText().toString());
            if (!StringUtils.isBlank(name) && !StringUtils.isBlank(horario) && cant >0) {
                Toast.makeText(getActivity(), "Campos elegidos - " + horario, Toast.LENGTH_SHORT).show();

                //Guardamos en SharedPreference
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("horario",horario);
                editor.apply();
                registeTransactionTicket();
                Fragment fragmentDetailMovie = newInstance(precioTotal);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentDetailMovie);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getActivity(), "Campos no ingresados", Toast.LENGTH_SHORT).show();
            }
        });

        plus.setOnClickListener(add -> {
            String cantidad = txtCantidad.getText().toString();
            value = Integer.parseInt(cantidad);
            value ++;
            precioTotal = value * precio;
            txtCantidad.setText(String.valueOf(value));
            txtTotal.setText("S/." + precioTotal + ".00");
            //Guardarlos en SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("numberTicket",txtCantidad.getText().toString());
            editor.putString("precioTotal",String.valueOf(precioTotal));
            editor.apply();
        });

        minus.setOnClickListener(remove -> {
            String cantidad = txtCantidad.getText().toString();
            int value = Integer.parseInt(cantidad);
            if (value > 0) {
                value --;
                txtCantidad.setText(String.valueOf(value));
                precioTotal = value * precio;
                txtCantidad.setText(String.valueOf(value));
                txtTotal.setText("S/." + precioTotal + ".00");
                //Guardarlos en SharedPreferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("numberTicket",txtCantidad.getText().toString());
                editor.putString("precioTotal",String.valueOf(precioTotal));
                editor.apply();
            }
        });

        btnFiltro.setOnClickListener(filtro -> {
            showWindowFilterCines(Gravity.CENTER_VERTICAL);
        });
        return view;
    }

    private void registeTransactionTicket() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        String movieCode = sharedPreferences.getString("movieCode","");
        String cinemaCode = sharedPreferences.getString("cinemaCode","");
        String horario = sharedPreferences.getString("horario","");
        String cantidadEntradas = sharedPreferences.getString("numberTicket","");
        TransactionTicketRequest request = TransactionTicketRequest.builder()
                .username(username)
                .cinemaCode(cinemaCode)
                .movieCode(movieCode)
                .movieSchedule(horario)
                .movieNumberOfTickets(Integer.parseInt(cantidadEntradas))
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_TRANSACTION)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        transactionService = retrofit.create(ITransaction.class);
        Call<String> call = transactionService.buyTickets(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String result = response.body();
                    Log.i("RESPONSE", result);
                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Error-", t.getMessage());
            }
        });
    }

    public ShoppingProducts newInstance(int priceTotalEntradas){
        ShoppingProducts fragment = new ShoppingProducts();
        Bundle args = new Bundle();
        args.putInt("priceTotalEntradas",priceTotalEntradas);
        fragment.setArguments(args);
        return fragment;
    }
    public void initView(View view){
        rvShedules = view.findViewById(R.id.rv_schedules);
        imgCoverMovieView = view.findViewById(R.id.detail_movie_cover);
        imgMovieView = view.findViewById(R.id.detail_movie_img);
        nameMovieView = view.findViewById(R.id.detail_movie_name);
        movieIdioma = view.findViewById(R.id.idioma_movie);
        nameLocal = view.findViewById(R.id.name_local);
        play = view.findViewById(R.id.play_fab);
        duration = view.findViewById(R.id.ticket_purchase_duration);
        txtCantidad = view.findViewById(R.id.incremento);
        txtTotal = view.findViewById(R.id.cantidad_total);
        minus = view.findViewById(R.id.btn_minimize);
        plus = view.findViewById(R.id.btn_add);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnContinuar = view.findViewById(R.id.btn_accept);
        btnFiltro = view.findViewById(R.id.btnFiltro);
        //Obtenermos los valores
        Bundle args = getArguments();
        if (args != null) {
            String movieCode = args.getString("movieCode");
            getDetailsMovieBackend(movieCode);
            play.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation));
        }
        showCronometro(duration);
    }
    public void showCronometro(TextView textView) {
        long totalTimeMillis = 5*60*1000;
        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            @Override
            public void onTick(long l) {
                long minutos = l/1000/60;
                long seconds = (l/1000) % 60;
                String timeLeftFormatted = String.format("%02d:%02d",minutos, seconds);
                duration.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                duration.setText("00:00");
                Toast.makeText(getActivity(), "Tiempo Terminado", Toast.LENGTH_SHORT).show();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void getDetailsMovieBackend(String movieCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieService = retrofit.create(IMovie.class);
        Call<MovieDetailsResponse> call = movieService.getMovieDetails(movieCode);
        call.enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                if (response.isSuccessful()) {
                    MovieDetailsResponse movieDetail = response.body();
                    assert movieDetail != null;
                    showMovieDetails(movieDetail);
                } else {
                    Log.e("DETAILS-ERROR","Error en el llamado");
                }
            }
            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {

            }
        });
    }

    public void showMovieDetails(MovieDetailsResponse movie) {
        Glide.with(getActivity()).load(movie.getImageUrl()).into(imgMovieView);
        nameMovieView.setText(movie.getName());
        Glide.with(getActivity()).load(movie.getImageCover()).into(imgCoverMovieView);
        //Animations
        imgCoverMovieView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation));
        movieIdioma.setText(movie.getLanguage());
        setupShedules(movie.getSchedules());
    }

    public void setupShedules(List<String> schedules) {
        scheduleAdapter = new ScheduleAdapter(getActivity(), schedules, this);
        rvShedules.setAdapter(scheduleAdapter);
        rvShedules.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false));
    }

    @Override
    public void onMovieClick(String schedule, Button button) {
        Toast.makeText(getActivity(), schedule, Toast.LENGTH_SHORT).show();
        horario = schedule;
    }


    public void showWindowFilterCines(int gravity) {
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
        getCinemasFromBackend(new TicketPurchase.CinemaCallback() {
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
                            precio = (int)Double.parseDouble(selectedCine.getTicketPrice());
                            saveCinemaSharedPreference(cinemaCode, cinemaName, precio);
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
                Toast.makeText(getActivity(), "Aplicando filtro: " + cinemaCode, Toast.LENGTH_SHORT).show();
                //getDetalleCinema(cinemaCode);
                nameLocal.setText(cinemaName);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void saveCinemaSharedPreference(String cinemaCode, String cinemaName, int precio) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cinemaCode", cinemaCode);
        editor.putString("cinemaName", cinemaName);
        editor.putInt("cinemaPrice", precio);
        editor.apply();
    }
    private void getCinemasFromBackend(final TicketPurchase.CinemaCallback callback) {

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

}