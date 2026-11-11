package com.example.proyecto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.R;
import com.example.proyecto.model.Inscripcion;

import java.util.ArrayList;

public class InscripcionAdapter extends RecyclerView.Adapter<InscripcionAdapter.InscripcionViewHolder> {

    public interface OnInscripcionClickListener {
        void onInscripcionClick(Inscripcion inscripcion);
    }

    private final ArrayList<Inscripcion> inscripciones;
    private final OnInscripcionClickListener listener;

    public InscripcionAdapter(ArrayList<Inscripcion> inscripciones, OnInscripcionClickListener listener) {
        this.inscripciones = inscripciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InscripcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inscripcion, parent, false);
        return new InscripcionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull InscripcionViewHolder holder, int position) {
        Inscripcion inscripcion = inscripciones.get(position);
        Context context = holder.itemView.getContext();
        holder.tvNombre.setText(inscripcion.getNombre());
        holder.tvFechaLugar.setText(context.getString(R.string.item_fecha_lugar,
                inscripcion.getFecha(), inscripcion.getLugar()));
        holder.tvHoras.setText(context.getString(R.string.item_horas, inscripcion.getHoras()));
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onInscripcionClick(inscripcion));
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return inscripciones.size();
    }

    static class InscripcionViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        TextView tvFechaLugar;
        TextView tvHoras;

        InscripcionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreInscripcion);
            tvFechaLugar = itemView.findViewById(R.id.tvFechaLugarInscripcion);
            tvHoras = itemView.findViewById(R.id.tvHorasInscripcion);
        }
    }
}