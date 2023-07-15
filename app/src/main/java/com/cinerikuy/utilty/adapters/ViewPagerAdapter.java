package com.cinerikuy.utilty.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cinerikuy.Voting;
import com.cinerikuy.VotingResult;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Voting();
            case 1: return new VotingResult();
            default: return new Voting();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
