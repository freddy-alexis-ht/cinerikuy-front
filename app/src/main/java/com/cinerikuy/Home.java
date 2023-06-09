package com.cinerikuy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cinerikuy.remote.movie.model.MovieDetailsResponse;
import com.cinerikuy.utilty.Slide;
import com.cinerikuy.utilty.adapters.MovieAdapter;
import com.cinerikuy.utilty.adapters.SliderPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    private List<Slide> lstSlides;
    private ViewPager2 sliderpager;
    private TabLayout indicator;
    private Handler handler;
    private Runnable runnable;
    private RecyclerView movieRecycleView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        lstSlides = new ArrayList<>();
        lstSlides.add(new Slide(R.drawable.slide1, "Slide Title1"));
        lstSlides.add(new Slide(R.drawable.slide2, "Slide Title2"));
        lstSlides.add(new Slide(R.drawable.slide1, "Slide Title1"));
        lstSlides.add(new Slide(R.drawable.slide2, "Slide Title2"));
        sliderpager = view.findViewById(R.id.slider_pager);
        indicator = view.findViewById(R.id.indicator);
        movieRecycleView = view.findViewById(R.id.Rv_movies);
        SliderPagerAdapter adapter = new SliderPagerAdapter(sliderpager,lstSlides, indicator);
        sliderpager.setAdapter(adapter);
        new TabLayoutMediator(indicator, sliderpager, (tab, position) ->{}).attach();
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
                    handler.postDelayed(this, 3000);
                } else {
                    handler.postDelayed(this, 3000);
                }
            }
        };
        handler.postDelayed(runnable, 3000);

        /*Iniciamos data para mostrar las peliculas*/
        List<MovieDetailsResponse> movies = new ArrayList<>();
        movies.add(new MovieDetailsResponse("Mohana",R.drawable.moana));
        movies.add(new MovieDetailsResponse("Black Panther",R.drawable.blackp));
        movies.add(new MovieDetailsResponse("Movie3",R.drawable.mov2));
        movies.add(new MovieDetailsResponse("Thermathian",R.drawable.themartian));
        movies.add(new MovieDetailsResponse("Super Mario Bros",R.drawable.mariobros));

        MovieAdapter movieAdapter = new MovieAdapter(getActivity(),movies);
        movieRecycleView.setAdapter(movieAdapter);
        movieRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}