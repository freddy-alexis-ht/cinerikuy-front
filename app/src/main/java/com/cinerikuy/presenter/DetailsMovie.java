package com.cinerikuy.presenter;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cinerikuy.MoviePlayer;
import com.cinerikuy.R;
import com.cinerikuy.TicketPurchase;
import com.cinerikuy.remote.movie.IMovie;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;
import com.cinerikuy.utilty.Constans;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsMovie extends Fragment {
    private ImageView movieImageView;
    private ImageView movieTrailer;
    private TextView movieName, movieSinopsis, movieGenero,movieDirector, movieDuration, movieIdioma, movieUrlTrailer, movieActors;
    private FloatingActionButton play;
    private IMovie movieService;
    private Button btnComprar;
    private String movieCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_details_movie, container, false);
        iniView(view);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Regresar al fragmento anterior
                getParentFragmentManager().popBackStack();
                //Cerrar el fragmento actual
                //getParentFragmentManager().beginTransaction().remove(YourFragment.this).commit();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Fragment fragmentDetailMovie = newInstance(movieUrlTrailer.getText().toString());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragmentDetailMovie);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mandar los datos
                Fragment fragmentTicketPurchase = newInstancePurchase(movieCode);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragmentTicketPurchase);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
    public static TicketPurchase newInstancePurchase(String movieCode) {
        TicketPurchase fragment = new TicketPurchase();
        Bundle args = new Bundle();
        args.putString("movieCode", movieCode);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Metodo que carga los atributos para ser enviados a otro fragment
     */
    public static MoviePlayer newInstance(String movieUrl) {
        MoviePlayer fragment = new MoviePlayer();
        Bundle args = new Bundle();
        args.putString("movieUrl", movieUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public void iniView(View view) {
        play = view.findViewById(R.id.play_fab);
        movieImageView = view.findViewById(R.id.detail_movie_img);
        movieTrailer = view.findViewById(R.id.detail_movie_cover);
        movieName = view.findViewById(R.id.detail_movie_name);
        movieSinopsis = view.findViewById(R.id.movie_detail_sinopsis);
        movieGenero = view.findViewById(R.id.movie_detail_genero);
        movieDirector = view.findViewById(R.id.movide_detail_director);
        movieDuration = view.findViewById(R.id.detail_movie_duration);
        movieIdioma = view.findViewById(R.id.subtitulo);
        movieUrlTrailer = view.findViewById(R.id.url_Trailer);
        movieActors = view.findViewById(R.id.movide_detail_actors);
        btnComprar = view.findViewById(R.id.btnComprar);
        movieUrlTrailer.setVisibility(view.GONE);

        //Obtenermos los valores
        Bundle args = getArguments();
        if (args != null) {
            movieCode = args.getString("movieCode");
            movieImageView.setTransitionName(args.getString("sharedName"));
            getDetailsMovieBackend(movieCode);
            play.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation));
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
        Glide.with(getActivity()).load(movie.getImageUrl()).into(movieImageView);
        movieName.setText(movie.getName());
        movieSinopsis.setText(movie.getSynopsis());
        movieGenero.setText(movie.getGenre());
        movieDirector.setText(movie.getDirector());
        movieIdioma.setText(movie.getLanguage());
        movieUrlTrailer.setText(movie.getTrailerUrl());
        movieDuration.setText("Duración: " + movie.getDuration());
        movieActors.setText(movie.getActors());
        Glide.with(getActivity()).load(movie.getImageCover()).into(movieTrailer);
        //Animations
        movieTrailer.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_animation));
    }
}