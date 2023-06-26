package com.cinerikuy.utilty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinerikuy.R;
import com.cinerikuy.remote.movie.model.Schedule;
import com.cinerikuy.utilty.listener.ScheduleItemClickListener;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.SheduleViewHolder> {
    private Context context;
    List<String> schedules;
    ScheduleItemClickListener scheduleItemClickListener;
    private Button previusSelectdButton = null;

    public ScheduleAdapter(Context context, List<String> schedules, ScheduleItemClickListener listener) {
        this.context = context;
        this.schedules = schedules;
        scheduleItemClickListener = listener;
    }

    @NonNull
    @Override
    public SheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_horario, parent, false);
        return new SheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SheduleViewHolder holder, int position) {
        holder.btnHorario.setText(schedules.get(position).concat(" pm"));
        if (holder.btnHorario == previusSelectdButton) {
            holder.btnHorario.setBackgroundResource(R.drawable.bg_horario_selected);
        } else {
            holder.btnHorario.setBackgroundResource(R.drawable.bg_horario);
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class SheduleViewHolder extends RecyclerView.ViewHolder {
        Button btnHorario;
        public SheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            btnHorario = itemView.findViewById(R.id.btn_schedules);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scheduleItemClickListener.onMovieClick(schedules.get(getAdapterPosition()),btnHorario);

                    //Restablece el fondo del boton anteriormente seleccionado
                    if (previusSelectdButton != null && previusSelectdButton != btnHorario) {
                        previusSelectdButton.setBackgroundResource(R.drawable.bg_horario);
                    }
                    //Actualizar boton seleccionado y su fondo
                    previusSelectdButton = btnHorario;
                    btnHorario.setBackgroundResource(R.drawable.bg_horario_selected);

                }
            });
        }
    }
}
