package com.cinerikuy.utilty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinerikuy.R;
import com.cinerikuy.remote.movie.model.Actor;

import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {
    private Context context;
    List<Actor> actors;

    public ActorAdapter(Context context, List<Actor> actors) {
        this.context = context;
        this.actors = actors;
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actors, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        holder.imgActor.setImageResource(actors.get(position).getImg());
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public class ActorViewHolder extends RecyclerView.ViewHolder {
        ImageView imgActor;
        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgActor = itemView.findViewById(R.id.img_actors);
        }
    }
}
