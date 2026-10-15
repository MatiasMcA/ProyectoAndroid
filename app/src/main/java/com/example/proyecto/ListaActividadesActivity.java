package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaActividadesActivity extends AppCompatActivity {

    private RecyclerView rvActividades;
    private Button btnVerHistorial;
    private VoluntariadoDbHelper dbHelper;
    private String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_actividades);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        rvActividades = findViewById(R.id.rvActividades);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);

        correo = getIntent().getStringExtra("correo");
        dbHelper = new VoluntariadoDbHelper(this);
        rvActividades.setLayoutManager(new LinearLayoutManager(this));

        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistorialHorasActivity.class);
            intent.putExtra("correo", correo);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarActividades();
    }

    private void cargarActividades() {
        ArrayList<Actividad> actividades = dbHelper.obtenerActividades();
        ActividadAdapter adapter = new ActividadAdapter(actividades, actividad -> {
            Intent intent = new Intent(this, DetalleActividadActivity.class);
            intent.putExtra("correo", correo);
            intent.putExtra("actividad_id", actividad.getId());
            startActivity(intent);
        });
        rvActividades.setAdapter(adapter);
    }
}