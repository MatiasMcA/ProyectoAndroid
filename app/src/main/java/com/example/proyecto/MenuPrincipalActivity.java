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

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnVerActividades;
    private Button btnMiHistorial;
    private Button btnMiPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnVerActividades = findViewById(R.id.btnVerActividades);
        btnMiHistorial = findViewById(R.id.btnMiHistorial);
        btnMiPerfil = findViewById(R.id.btnMiPerfil);

        String correo = getIntent().getStringExtra("correo");
        tvBienvenida.setText(getString(R.string.menu_bienvenida, correo));

        btnVerActividades.setOnClickListener(v ->
                startActivity(new Intent(this, ListaActividadesActivity.class)));

        btnMiHistorial.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialHorasActivity.class)));

        btnMiPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra("correo", correo);
            startActivity(intent);
        });
    }
}
