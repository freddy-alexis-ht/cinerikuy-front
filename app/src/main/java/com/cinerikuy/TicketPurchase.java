package com.cinerikuy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cinerikuy.remote.movie.IMovie;
import com.cinerikuy.remote.movie.model.MovieDetailsResponse;
import com.cinerikuy.remote.movie.model.Schedule;
import com.cinerikuy.utilty.Constans;
import com.cinerikuy.utilty.adapters.ScheduleAdapter;
import com.cinerikuy.utilty.listener.ScheduleItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_purchase, container, false);
        initView(view);
        //setupShedules();
        return view;
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

    }
}