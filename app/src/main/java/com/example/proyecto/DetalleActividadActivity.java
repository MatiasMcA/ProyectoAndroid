package com.example.proyecto;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleActividadActivity extends AppCompatActivity {

    private TextView tvNombre;
    private TextView tvFecha;
    private TextView tvLugar;
    private TextView tvDescripcion;
    private Button btnInscribirme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_actividad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvNombre = findViewById(R.id.tvNombre);
        tvFecha = findViewById(R.id.tvFecha);
        tvLugar = findViewById(R.id.tvLugar);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        btnInscribirme = findViewById(R.id.btnInscribirme);

        String nombre = getIntent().getStringExtra("nombre");
        String fecha = getIntent().getStringExtra("fecha");
        String lugar = getIntent().getStringExtra("lugar");
        String descripcion = getIntent().getStringExtra("descripcion");

        tvNombre.setText(nombre);
        tvFecha.setText(getString(R.string.detalle_fecha, fecha));
        tvLugar.setText(getString(R.string.detalle_lugar, lugar));
        tvDescripcion.setText(descripcion);

        btnInscribirme.setOnClickListener(v -> {
            Toast.makeText(this, R.string.msg_inscripcion_ok, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
