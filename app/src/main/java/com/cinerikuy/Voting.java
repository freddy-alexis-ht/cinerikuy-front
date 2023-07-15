package com.cinerikuy;

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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cinerikuy.remote.movie.IMovie;
import com.cinerikuy.remote.movie.model.VotingListResponse;
import com.cinerikuy.remote.movie.model.VotingUpRequest;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.Utils;
import com.cinerikuy.utilty.adapters.MoviePeruvianAdapter;
import com.cinerikuy.utilty.adapters.ViewPagerAdapter;
import com.cinerikuy.utilty.listener.VoteLikeClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Voting extends Fragment implements VoteLikeClickListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private ScrollView scrollView;
    private IMovie movieService;
    private List<VotingListResponse> moviesPeruvian;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voting, container, false);
        Fresco.initialize(getActivity());
        init(view);
        getMoviesPeruvianFromBackend();
        return view;
    }

    private void init(View view) {
        recyclerViewList = view.findViewById(R.id.recyclerView);
        scrollView = view.findViewById(R.id.scrollView1);
    }

    private void getMoviesPeruvianFromBackend() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_MOVIE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieService = retrofit.create(IMovie.class);
        //Obtenemos los valores del SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        Call<List<VotingListResponse>> call = movieService.findMoviesInVoting(username);
        call.enqueue(new Callback<List<VotingListResponse>>() {
            @Override
            public void onResponse(Call<List<VotingListResponse>> call, Response<List<VotingListResponse>> response) {
                if (response.isSuccessful()) {
                    Utils.logResponse(response.body());
                    initList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<VotingListResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "Ocurrio un error al cargar las peliculas", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void initList(List<VotingListResponse> moviesPeruvian) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        adapter = new MoviePeruvianAdapter(moviesPeruvian,this);
        recyclerViewList.setAdapter(adapter);
    }

    @Override
    public void onClickVoteLike(String movieCode, ImageView doLike) {
        //Toast.makeText(getActivity(), movieCode, Toast.LENGTH_SHORT).show();
        insertVotePeruvianMovie(movieCode);
    }

    private void insertVotePeruvianMovie(String movieCode) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constans.BACKEND_MOVIE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        movieService = retrofit.create(IMovie.class);
        //Obtenemos los valores del SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString("username","value1");
        VotingUpRequest votingUpRequest = new VotingUpRequest(movieCode, username);
        Utils.logRequest(votingUpRequest);
        Call<String> call = movieService.voteUpForMovie(votingUpRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Utils.logResponse(response.body());
                if(response.isSuccessful()) {
                    String responseBody = response.body();
                    Toast.makeText(getActivity(), responseBody, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Ocurrio un error al votar", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage() + t.toString());
            }
        });

    }
}