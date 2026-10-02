package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ListaActividadesActivity extends AppCompatActivity {

    private ListView lvActividades;
    private Button btnVerHistorial;

    private String[] nombres = {
            "Apoyo escolar comuna",
            "Jornada de reciclaje",
            "Acompañamiento adulto mayor"
    };

    private String[] fechas = {
            "12/10/2026",
            "20/10/2026",
            "28/10/2026"
    };

    private String[] lugares = {
            "Escuela Básica San Martín",
            "Plaza Prat",
            "Hogar de Ancianos Santo Tomás"
    };

    private String[] descripciones = {
            "Apoyo en tareas y reforzamiento escolar para niños.",
            "Recolección y clasificación de materiales reciclables.",
            "Actividades recreativas con adultos mayores del hogar"
    };

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

        lvActividades = findViewById(R.id.lvActividades);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);

        String[] items = new String[nombres.length];
        for (int i = 0; i < nombres.length; i++) {
            items[i] = nombres[i] + "\n" + fechas[i] + " - " + lugares[i];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvActividades.setAdapter(adapter);

        lvActividades.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DetalleActividadActivity.class);
            intent.putExtra("nombre", nombres[position]);
            intent.putExtra("fecha", fechas[position]);
            intent.putExtra("lugar", lugares[position]);
            intent.putExtra("descripcion", descripciones[position]);
            startActivity(intent);
        });

        btnVerHistorial.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialHorasActivity.class)));
    }
}
