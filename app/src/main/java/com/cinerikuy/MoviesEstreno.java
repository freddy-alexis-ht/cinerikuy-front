package com.cinerikuy;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cinerikuy.presenter.DetailsMovie;
import com.cinerikuy.remote.movie.IMovie;
import com.cinerikuy.remote.movie.model.MovieBillboardResponse;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.Utils;
import com.cinerikuy.utilty.adapters.MovieCartProxAdapter;
import com.cinerikuy.utilty.listener.MovieItemClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MoviesEstreno extends Fragment implements MovieItemClickListener {
    private List<MovieBillboardResponse> moviesEstreno;
    private RecyclerView recyclerView;
    private MovieCartProxAdapter adapter;
    private IMovie movieService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_estreno, container, false);
        init(view);
        showMoviesCartelera();
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rv_movie);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
    }

    private void showMoviesCartelera() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_MOVIE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieService = retrofit.create(IMovie.class);
        Call<List<MovieBillboardResponse>> call = movieService.getComingSoon();
        call.enqueue(new Callback<List<MovieBillboardResponse>>() {
            @Override
            public void onResponse(Call<List<MovieBillboardResponse>> call, Response<List<MovieBillboardResponse>> response) {
                if(response.isSuccessful()) {
                    moviesEstreno = response.body();
                    Utils.logResponse(moviesEstreno);
                    initMovies(moviesEstreno);
                }
            }

            @Override
            public void onFailure(Call<List<MovieBillboardResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar las imagenes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initMovies(List<MovieBillboardResponse> movies) {
        adapter = new MovieCartProxAdapter(movies, getActivity(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMovieClick(MovieBillboardResponse movie, ImageView moviImageView) {
        //Toast.makeText(getContext(), movieDetailsResponse.getMovieCode(), Toast.LENGTH_SHORT).show();
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

    public static DetailsMovie newInstance(MovieBillboardResponse movie) {
        DetailsMovie fragment = new DetailsMovie();
        Bundle args = new Bundle();
        args.putString("movieCode", movie.getMovieCode());
        fragment.setArguments(args);
        return fragment;
    }

    public void saveMovieSharedPreferences(MovieBillboardResponse movie) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("movieName", movie.getName());
        editor.putString("movieCode", movie.getMovieCode());
        editor.putBoolean("isEstreno", true);
        editor.apply();
    }
}