package com.example.proyecto.ui.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.R;
import com.example.proyecto.adapter.InscripcionAdapter;
import com.example.proyecto.data.VoluntariadoDbHelper;
import com.example.proyecto.model.Inscripcion;
import com.example.proyecto.util.EjecutorBd;

import java.util.ArrayList;

public class HistorialHorasActivity extends AppCompatActivity {

    private TextView tvTotalHoras;
    private TextView tvVacioHistorial;
    private InscripcionAdapter adapter;
    private final ArrayList<Inscripcion> completadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_horas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        RecyclerView rvHistorial = findViewById(R.id.rvHistorial);
        tvTotalHoras = findViewById(R.id.tvTotalHoras);
        tvVacioHistorial = findViewById(R.id.tvVacioHistorial);

        adapter = new InscripcionAdapter(completadas, null);
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        rvHistorial.setAdapter(adapter);

        cargarHistorial(getIntent().getStringExtra("correo"));
    }

    private void cargarHistorial(String correo) {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            long usuarioId = dbHelper.obtenerUsuarioId(correo);
            ArrayList<Inscripcion> lista = dbHelper.obtenerListaInscripciones(
                    usuarioId, VoluntariadoDbHelper.ESTADO_COMPLETADA);
            int total = dbHelper.obtenerTotalHoras(usuarioId);
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed()) {
                    return;
                }
                completadas.clear();
                completadas.addAll(lista);
                adapter.notifyDataSetChanged();
                tvTotalHoras.setText(getString(R.string.historial_total, total));
                tvVacioHistorial.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}