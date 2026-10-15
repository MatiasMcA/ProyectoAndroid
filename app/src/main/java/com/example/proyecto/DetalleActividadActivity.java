package com.example.proyecto;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.VoluntariadoContract.ActividadEntry;

public class DetalleActividadActivity extends AppCompatActivity {

    private TextView tvNombre;
    private TextView tvFecha;
    private TextView tvLugar;
    private TextView tvDescripcion;
    private Button btnInscribirme;
    private VoluntariadoDbHelper dbHelper;
    private String correo;
    private long actividadId;

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
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        tvNombre = findViewById(R.id.tvNombre);
        tvFecha = findViewById(R.id.tvFecha);
        tvLugar = findViewById(R.id.tvLugar);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        btnInscribirme = findViewById(R.id.btnInscribirme);

        correo = getIntent().getStringExtra("correo");
        actividadId = getIntent().getLongExtra("actividad_id", -1);
        dbHelper = new VoluntariadoDbHelper(this);

        Cursor cursor = dbHelper.obtenerActividad(actividadId);
        if (cursor.moveToFirst()) {
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
            String lugar = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
            tvNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE)));
            tvFecha.setText(getString(R.string.detalle_fecha, fecha));
            tvLugar.setText(getString(R.string.detalle_lugar, lugar));
            tvDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_DESCRIPCION)));
        }
        cursor.close();

        btnInscribirme.setOnClickListener(v -> {
            Toast.makeText(this, R.string.msg_inscripcion_ok, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}