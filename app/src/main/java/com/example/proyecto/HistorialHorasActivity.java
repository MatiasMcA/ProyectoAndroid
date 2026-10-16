package com.example.proyecto;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.VoluntariadoContract.ActividadEntry;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistorialHorasActivity extends AppCompatActivity {

    private ListView lvHistorial;
    private TextView tvTotalHoras;

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

        lvHistorial = findViewById(R.id.lvHistorial);
        tvTotalHoras = findViewById(R.id.tvTotalHoras);

        String correo = getIntent().getStringExtra("correo");
        VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(this);
        cargarHistorial(dbHelper, correo);
    }

    private void cargarHistorial(VoluntariadoDbHelper dbHelper, String correo) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handlerPrincipal = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            long usuarioId = dbHelper.obtenerUsuarioId(correo);
            ArrayList<String> textos = new ArrayList<>();
            Cursor cursor = dbHelper.obtenerInscripciones(usuarioId, VoluntariadoDbHelper.ESTADO_COMPLETADA);
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
                int horas = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_HORAS));
                textos.add(getString(R.string.historial_item, nombre, horas));
            }
            cursor.close();
            int total = dbHelper.obtenerTotalHoras(usuarioId);

            handlerPrincipal.post(() -> {
                if (isDestroyed()) {
                    return;
                }
                lvHistorial.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textos));
                tvTotalHoras.setText(getString(R.string.historial_total, total));
            });
        });
        executor.shutdown();
    }
}