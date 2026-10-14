package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.database.Cursor;
import com.example.proyecto.VoluntariadoContract.UsuarioEntry;

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
    private Button btnAcerca;
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

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        tvCarreraPerfil = findViewById(R.id.tvCarreraPerfil);
        tvSedePerfil = findViewById(R.id.tvSedePerfil);
        tvHorasPerfil = findViewById(R.id.tvHorasPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnAcerca = findViewById(R.id.btnAcerca);

        String correo = getIntent().getStringExtra("correo");

        VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(this);
        Cursor cursor = dbHelper.obtenerUsuario(correo);
        String nombre = "";
        String carrera = "";
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE));
            carrera = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_CARRERA));
        }
        cursor.close();

        tvNombrePerfil.setText(getString(R.string.perfil_nombre, nombre));
        tvCorreoPerfil.setText(getString(R.string.perfil_correo, correo));
        tvCarreraPerfil.setText(getString(R.string.perfil_carrera, carrera));
        tvSedePerfil.setText(getString(R.string.perfil_sede, sede));
        tvHorasPerfil.setText(getString(R.string.perfil_horas, horas));

        btnAcerca.setOnClickListener(v ->
                startActivity(new Intent(this, AcercaActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
