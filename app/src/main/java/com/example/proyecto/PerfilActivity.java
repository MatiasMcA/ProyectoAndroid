package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombrePerfil;
    private TextView tvCorreoPerfil;
    private TextView tvCarreraPerfil;
    private TextView tvSedePerfil;
    private TextView tvHorasPerfil;
    private Button btnCerrarSesion;

    private String nombre = "Juan Pérez";
    private String carrera = "Ingeniería en Informática";
    private String sede = "Santo Tomás Iquique";
    private int horas = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        tvCarreraPerfil = findViewById(R.id.tvCarreraPerfil);
        tvSedePerfil = findViewById(R.id.tvSedePerfil);
        tvHorasPerfil = findViewById(R.id.tvHorasPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String correo = getIntent().getStringExtra("correo");

        tvNombrePerfil.setText(getString(R.string.perfil_nombre, nombre));
        tvCorreoPerfil.setText(getString(R.string.perfil_correo, correo));
        tvCarreraPerfil.setText(getString(R.string.perfil_carrera, carrera));
        tvSedePerfil.setText(getString(R.string.perfil_sede, sede));
        tvHorasPerfil.setText(getString(R.string.perfil_horas, horas));

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
