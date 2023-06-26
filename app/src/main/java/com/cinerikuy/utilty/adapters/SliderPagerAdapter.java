package com.cinerikuy.utilty.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.cinerikuy.R;
import com.cinerikuy.utilty.Slider;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SliderPagerAdapter extends RecyclerView.Adapter<SliderPagerAdapter.SliderViewHolder> {
    private ViewPager2 viewPager2;
    private List<Slider> mList;
    private TabLayout indicator;

    public SliderPagerAdapter(ViewPager2 viewPager2, List<Slider> mList, TabLayout indicator) {
        this.viewPager2 = viewPager2;
        this.mList = mList;
        this.indicator = indicator;

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                indicator.getTabAt(position).select();
            }
        });
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View slideLayout = inflater.inflate(R.layout.slide_item, parent, false);

        return new SliderViewHolder(slideLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Slider slide = mList.get(position);
        holder.slideImage.setImageResource(slide.getImage());
        holder.slideText.setText(slide.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView slideImage;
        TextView slideText;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            slideImage = itemView.findViewById(R.id.slide_image);
            slideText = itemView.findViewById(R.id.slide_title);
        }
    }
}
