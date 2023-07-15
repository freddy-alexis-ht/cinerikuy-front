package com.cinerikuy.utilty.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cinerikuy.MoviesCartelera;
import com.cinerikuy.MoviesEstreno;

public class ViewPagerMovie extends FragmentStateAdapter {

    public ViewPagerMovie(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new MoviesCartelera();
            case 1: return new MoviesEstreno();
            default: return new MoviesCartelera();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
