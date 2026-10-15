package com.example.proyecto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    public interface OnActividadClickListener {
        void onActividadClick(Actividad actividad);
    }

    private final ArrayList<Actividad> actividades;
    private final OnActividadClickListener listener;

    public ActividadAdapter(ArrayList<Actividad> actividades, OnActividadClickListener listener) {
        this.actividades = actividades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);
        Context context = holder.itemView.getContext();
        holder.tvNombre.setText(actividad.getNombre());
        holder.tvFecha.setText(context.getString(R.string.detalle_fecha, actividad.getFecha()));
        holder.tvLugar.setText(context.getString(R.string.detalle_lugar, actividad.getLugar()));
        holder.tvCupos.setText(context.getString(R.string.item_cupos, actividad.getCupos()));
        holder.itemView.setOnClickListener(v -> listener.onActividadClick(actividad));
    }

    @Override
    public int getItemCount() {
        return actividades.size();
    }

    static class ActividadViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        TextView tvFecha;
        TextView tvLugar;
        TextView tvCupos;

        ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreItem);
            tvFecha = itemView.findViewById(R.id.tvFechaItem);
            tvLugar = itemView.findViewById(R.id.tvLugarItem);
            tvCupos = itemView.findViewById(R.id.tvCuposItem);
        }
    }
}