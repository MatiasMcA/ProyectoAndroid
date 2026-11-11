package com.example.proyecto.ui.actividades;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.R;
import com.example.proyecto.data.VoluntariadoContract.ActividadEntry;
import com.example.proyecto.data.VoluntariadoDbHelper;
import com.example.proyecto.util.EjecutorBd;
import com.example.proyecto.util.RecordatorioHelper;

public class DetalleActividadActivity extends AppCompatActivity {

    private TextView tvNombre;
    private TextView tvFecha;
    private TextView tvLugar;
    private TextView tvDescripcion;
    private Button btnInscribirme;
    private String correo;
    private long actividadId;
    private double latitud;
    private double longitud;
    private String nombreActividad;
    private String fechaActividad;
    private String lugarActividad;

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

        btnInscribirme.setOnClickListener(v -> inscribirse());
        findViewById(R.id.btnVerMapaDetalle).setOnClickListener(v -> abrirEnMapa());

        cargarActividad();
    }

    private void cargarActividad() {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            String[] datos = new String[4];
            double[] coordenadas = new double[2];
            try (Cursor cursor = dbHelper.obtenerActividad(actividadId)) {
                if (cursor.moveToFirst()) {
                    datos[0] = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
                    datos[1] = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
                    datos[2] = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
                    datos[3] = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_DESCRIPCION));
                    coordenadas[0] = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LATITUD));
                    coordenadas[1] = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LONGITUD));
                }
            }
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed() || datos[0] == null) {
                    return;
                }
                tvNombre.setText(datos[0]);
                tvFecha.setText(getString(R.string.detalle_fecha, datos[1]));
                tvLugar.setText(getString(R.string.detalle_lugar, datos[2]));
                tvDescripcion.setText(datos[3]);
                latitud = coordenadas[0];
                longitud = coordenadas[1];
                nombreActividad = datos[0];
                fechaActividad = datos[1];
                lugarActividad = datos[2];
            });
        });
    }

    private void inscribirse() {
        btnInscribirme.setEnabled(false);
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            long usuarioId = dbHelper.obtenerUsuarioId(correo);
            int resultado = dbHelper.inscribirUsuario(usuarioId, actividadId);
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed()) {
                    return;
                }
                btnInscribirme.setEnabled(true);
                if (resultado == VoluntariadoDbHelper.RESULTADO_OK) {
                    Toast.makeText(this, R.string.msg_inscripcion_ok, Toast.LENGTH_SHORT).show();
                    RecordatorioHelper.programar(getApplicationContext(), actividadId,
                            nombreActividad, fechaActividad, lugarActividad);
                    finish();
                } else if (resultado == VoluntariadoDbHelper.RESULTADO_YA_INSCRITO) {
                    Toast.makeText(this, R.string.error_ya_inscrito, Toast.LENGTH_SHORT).show();
                } else if (resultado == VoluntariadoDbHelper.RESULTADO_SIN_CUPOS) {
                    Toast.makeText(this, R.string.error_sin_cupos, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_inscripcion, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void abrirEnMapa() {
        String nombre = Uri.encode(tvNombre.getText().toString());
        Uri uri = Uri.parse("geo:" + latitud + "," + longitud + "?q=" + latitud + "," + longitud + "(" + nombre + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.msg_sin_app_mapas, Toast.LENGTH_SHORT).show();
        }
    }
}