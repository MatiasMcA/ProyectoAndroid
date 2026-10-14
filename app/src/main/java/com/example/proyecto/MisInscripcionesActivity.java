package com.example.proyecto;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
public class MisInscripcionesActivity extends AppCompatActivity {

    private ListView lvInscripciones;
    private TextView tvVacio;
    private ArrayList<String> inscripciones;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_inscripciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());


        lvInscripciones = findViewById(R.id.lvInscripciones);
        tvVacio = findViewById(R.id.tvVacio);

        inscripciones = new ArrayList<>();
        inscripciones.add("Apoyo escolar comuna\n12/10/2026 - Escuela Básica San Martín");
        inscripciones.add("Jornada de reciclaje\n20/10/2026 - Plaza Prat");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inscripciones);
        lvInscripciones.setAdapter(adapter);
        lvInscripciones.setEmptyView(tvVacio);
        lvInscripciones.setOnItemClickListener((parent, view, position, id) ->
                mostrarDialogoCancelar(position));
    }

    private void mostrarDialogoCancelar(int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cancelar_titulo)
                .setMessage(R.string.cancelar_mensaje)
                .setPositiveButton(R.string.cancelar_si, (dialog, which) -> {
                    inscripciones.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, R.string.msg_inscripcion_cancelada, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancelar_no, null)
                .show();
    }
}
