package com.cinerikuy.utilty.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinerikuy.R;
import com.cinerikuy.remote.movie.model.VotingListResponse;
import com.cinerikuy.utilty.listener.VoteLikeClickListener;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MoviePeruvianAdapter extends RecyclerView.Adapter<MoviePeruvianAdapter.ViewHolder>{
    private List<VotingListResponse> moviesPeruvian;
    private VoteLikeClickListener votingVoteLikeClickListener;
    private ImageView previusSelected = null;

    public MoviePeruvianAdapter(List<VotingListResponse> moviesPeruvian, VoteLikeClickListener votingVoteLikeClickListener) {
        this.moviesPeruvian = moviesPeruvian;
        this.votingVoteLikeClickListener = votingVoteLikeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View infalte = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote,parent,false);
        return new MoviePeruvianAdapter.ViewHolder(infalte);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.movieName.setText(moviesPeruvian.get(position).getName());
        holder.movieSinopsis.setText(moviesPeruvian.get(position).getSynopsis());
        holder.movieGenre.setText(moviesPeruvian.get(position).getGenre());
        String imageUrl = moviesPeruvian.get(position).getImageUrl();
        holder.imageMovie.setImageURI(imageUrl);
        holder.movieDirector.setText(moviesPeruvian.get(position).getDirector());
        holder.movieDuration.setText(moviesPeruvian.get(position).getDuration());
        boolean isVote = moviesPeruvian.get(position).getVoted();

        if (isVote) {
            holder.btnLike.setImageResource(R.drawable.ic_vote_selected); // Establecer el recurso de imagen como ic_vote_selected
            previusSelected = holder.btnLike; // Establecer el botón como previamente seleccionado
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_vote); // Establecer el recurso de imagen como ic_vote
        }

        /*if (holder.btnLike == previusSelected) {
            holder.btnLike.setImageResource(R.drawable.ic_vote);
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_vote);
        }*/
    }

    @Override
    public int getItemCount() {
        return moviesPeruvian.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView movieName, movieSinopsis, movieGenre, movieDirector, movieDuration;
        SimpleDraweeView imageMovie;
        ImageView btnLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            movieName = itemView.findViewById(R.id.txtNameMovie);
            movieSinopsis = itemView.findViewById(R.id.txtSinopsis);
            imageMovie = itemView.findViewById(R.id.picCine);
            movieGenre = itemView.findViewById(R.id.txtGenero);
            movieDirector = itemView.findViewById(R.id.txtDirector);
            movieDuration = itemView.findViewById(R.id.txtDuration);
            btnLike = itemView.findViewById(R.id.voting);
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Obtener la posición del elemento en el adaptador
                    int position = getAdapterPosition();

                    // Verificar si se hizo clic en el mismo ImageView
                    if (btnLike == previusSelected) {
                        btnLike.setImageResource(R.drawable.ic_vote); // Establecer el recurso de imagen como ic_vote
                        previusSelected = null; // No hay ningún ImageView seleccionado
                    } else {
                        // Hacer el resto de la lógica como antes
                        votingVoteLikeClickListener.onClickVoteLike(moviesPeruvian.get(position).getMovieCode(), btnLike);

                        // Restablecer el fondo del botón anteriormente seleccionado
                        if (previusSelected != null && previusSelected != btnLike) {
                            previusSelected.setImageResource(R.drawable.ic_vote);
                        }

                        // Actualizar el botón seleccionado y su fondo
                        previusSelected = btnLike;
                        btnLike.setImageResource(R.drawable.ic_vote_selected);
                    }
                }
            });
        }
    }
}
