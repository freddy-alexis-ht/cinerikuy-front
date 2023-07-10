package com.cinerikuy.presenter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.transition.Transition;
import android.transition.TransitionInflater;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cinerikuy.R;
import com.cinerikuy.presenter.DetailsMovie;
import com.cinerikuy.remote.cinema.ICinema;
import com.cinerikuy.remote.cinema.model.CinemaResponse;
import com.cinerikuy.remote.customer.ICustomer;
import com.cinerikuy.remote.customer.model.CustomerResponse;
import com.cinerikuy.remote.movie.IMovie;
import com.cinerikuy.remote.movie.model.MovieBillboardResponse;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.Slider;
import com.cinerikuy.utilty.Utils;
import com.cinerikuy.utilty.adapters.MovieAdapter;
import com.cinerikuy.utilty.adapters.SliderPagerAdapter;
import com.cinerikuy.utilty.listener.MovieItemClickListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends Fragment implements MovieItemClickListener {
    private List<Slider> lstSlides;
    private ViewPager2 sliderpager;
    private TabLayout indicator;
    private Handler handler;
    private Runnable runnable;
    private RecyclerView moviePopularRecycleView;
    private IMovie movieService;
    private ICinema cinemaService;
    private Button btnFilter;
    private String cinemaCode;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        lstSlides = new ArrayList<>();
        lstSlides.add(new Slider(R.drawable.slide1, "Slide Title1"));
        lstSlides.add(new Slider(R.drawable.slide2, "Slide Title2"));
        lstSlides.add(new Slider(R.drawable.slide1, "Slide Title1"));
        lstSlides.add(new Slider(R.drawable.slide2, "Slide Title2"));
        initView(view);
        SliderPagerAdapter adapter = new SliderPagerAdapter(sliderpager,lstSlides, indicator);
        sliderpager.setAdapter(adapter);
        new TabLayoutMediator(indicator, sliderpager, (tab, position) ->{}).attach();
        handler = new Handler();
        getMoviesFromBackend();
        /*Abrir ventana de filtro*/
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWindowFilterCines(Gravity.CENTER_VERTICAL);
            }
        });
        return view;
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
        getCinemasFromBackend(new CinemaCallback() {
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
                getMovieByCinema(cinemaCode);
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void getMovieByCinema(String cinemaCode) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando peliculas ...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieService = retrofit.create(IMovie.class);
        Call<List<MovieBillboardResponse>> call = movieService.getSpecificBillboard(cinemaCode);
        call.enqueue(new Callback<List<MovieBillboardResponse>>() {
            @Override
            public void onResponse(Call<List<MovieBillboardResponse>> call, Response<List<MovieBillboardResponse>> response) {
                if (response.isSuccessful()) {
                    List<MovieBillboardResponse> movies = response.body();
                    Utils.logResponse(movies);
                    updateMovieList(movies);
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Ocultar el ProgressDialog
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }, 3000);
            }

            @Override
            public void onFailure(Call<List<MovieBillboardResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar las peliculas", Toast.LENGTH_SHORT).show();
                Log.e("Home - Movies", t.getMessage());
            }
        });
    }
    private void getCinemasFromBackend(final CinemaCallback callback) {

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

    public void initView(View view) {
        sliderpager = view.findViewById(R.id.slider_pager);
        indicator = view.findViewById(R.id.indicator);
        moviePopularRecycleView = view.findViewById(R.id.Rv_movies);
        btnFilter = view.findViewById(R.id.btnFiltro);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
    /***
     * Metodo para enviar la información del detalle de la pelicula
     */
    @Override
    public void onMovieClick(MovieBillboardResponse movie, ImageView moviImageView) {
        //Toast.makeText(getActivity(), "Item click: " + movie.getName(), Toast.LENGTH_SHORT).show();
        Fragment fragmentDetailMovie = newInstance(movie);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Transition enterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade);
        Transition exitTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade);
        fragmentDetailMovie.setEnterTransition(enterTransition);
        fragmentDetailMovie.setExitTransition(exitTransition);

        moviImageView.setTransitionName("sharedName");
        transaction.replace(R.id.fragment_container, fragmentDetailMovie);
        transaction.addToBackStack(null);
        transaction.addSharedElement(moviImageView, "sharedName");
        transaction.commit();

        //Guardamos en SharedPreferences los atributos necesarios
        saveMovieSharedPreferences(movie);
    }
    public void saveMovieSharedPreferences(MovieBillboardResponse movie) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("movieName", movie.getName());
        editor.putString("movieCode", movie.getMovieCode());
        editor.apply();
    }
    /**
     * Metodo que carga los atributos para ser enviados a otro fragment
     */
    public static DetailsMovie newInstance(MovieBillboardResponse movie) {
        DetailsMovie fragment = new DetailsMovie();
        Bundle args = new Bundle();
        args.putString("movieCode", movie.getMovieCode());
        fragment.setArguments(args);
        return fragment;
    }

    private void startAutoSlider() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = sliderpager.getCurrentItem();
                int newPosition = currentPosition + 1;
                if (newPosition >= lstSlides.size()) {
                    newPosition = 0;
                }
                sliderpager.setCurrentItem(newPosition, true);
                if (newPosition == 0) {
                    handler.postDelayed(this, 5000);
                } else {
                    handler.postDelayed(this, 5000);
                }
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private void stopAutoSlider() {
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoSlider();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSlider();
    }

    private void getMoviesFromBackend() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieService = retrofit.create(IMovie.class);
        Call<List<MovieBillboardResponse>> call = movieService.getMainBillboard();
        call.enqueue(new Callback<List<MovieBillboardResponse>>() {
            @Override
            public void onResponse(Call<List<MovieBillboardResponse>> call, Response<List<MovieBillboardResponse>> response) {
                if(response.isSuccessful()) {
                    List<MovieBillboardResponse> movies = response.body();
                    Utils.logResponse(movies);
                    initMoviesPopular(movies);
                }
            }
            @Override
            public void onFailure(Call<List<MovieBillboardResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "Ocurrio un error al cargar las imagenes", Toast.LENGTH_SHORT).show();
                Log.e("ERROR CARGANDO LAS IMAGENES", t.getMessage());
            }
        });
    }

    public void initMoviesPopular(List<MovieBillboardResponse> movies) {
        MovieAdapter movieAdapter = new MovieAdapter(getActivity(),movies,this);
        moviePopularRecycleView.setAdapter(movieAdapter);
        moviePopularRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));

    }

    private void updateMovieList(List<MovieBillboardResponse> movies) {
        MovieAdapter movieAdapter = new MovieAdapter(getActivity(), movies, this);
        moviePopularRecycleView.setAdapter(movieAdapter);
        moviePopularRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
    }

    private interface CinemaCallback {
        void onCinemasReceived(ArrayList<CinemaResponse> cines);
    }

}