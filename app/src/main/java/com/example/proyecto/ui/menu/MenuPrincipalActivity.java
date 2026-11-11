package com.example.proyecto.ui.menu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.R;
import com.example.proyecto.data.VoluntariadoContract.UsuarioEntry;
import com.example.proyecto.data.VoluntariadoDbHelper;
import com.example.proyecto.ui.actividades.HistorialHorasActivity;
import com.example.proyecto.ui.actividades.ListaActividadesActivity;
import com.example.proyecto.ui.actividades.MapaActivity;
import com.example.proyecto.ui.actividades.MisInscripcionesActivity;
import com.example.proyecto.util.EjecutorBd;

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView tvBienvenida;

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
        String correo = getIntent().getStringExtra("correo");
        cargarNombre(correo);
        pedirPermisoNotificaciones();

        findViewById(R.id.btnVerActividades).setOnClickListener(v ->
                abrirPantalla(ListaActividadesActivity.class, correo));
        findViewById(R.id.btnMisInscripciones).setOnClickListener(v ->
                abrirPantalla(MisInscripcionesActivity.class, correo));
        findViewById(R.id.btnVerMapa).setOnClickListener(v ->
                abrirPantalla(MapaActivity.class, correo));
        findViewById(R.id.btnMiHistorial).setOnClickListener(v ->
                abrirPantalla(HistorialHorasActivity.class, correo));
        findViewById(R.id.btnMiPerfil).setOnClickListener(v ->
                abrirPantalla(PerfilActivity.class, correo));
    }

    private void cargarNombre(String correo) {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            String[] nombre = {""};
            try (Cursor cursor = dbHelper.obtenerUsuario(correo)) {
                if (cursor.moveToFirst()) {
                    nombre[0] = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE));
                }
            }
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed()) {
                    return;
                }
                tvBienvenida.setText(getString(R.string.menu_bienvenida, nombre[0]));
            });
        });
    }

    private void abrirPantalla(Class<?> destino, String correo) {
        Intent intent = new Intent(this, destino);
        intent.putExtra("correo", correo);
        startActivity(intent);
    }

    private void pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
        }
    }
}