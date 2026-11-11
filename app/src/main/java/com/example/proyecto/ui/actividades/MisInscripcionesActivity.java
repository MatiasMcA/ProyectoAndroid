package com.example.proyecto.ui.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto.R;
import com.example.proyecto.adapter.InscripcionAdapter;
import com.example.proyecto.data.VoluntariadoDbHelper;
import com.example.proyecto.model.Inscripcion;
import com.example.proyecto.util.EjecutorBd;
import com.example.proyecto.util.RecordatorioHelper;

import java.util.ArrayList;

public class MisInscripcionesActivity extends AppCompatActivity {

    private RecyclerView rvInscripciones;
    private TextView tvVacio;
    private InscripcionAdapter adapter;
    private final ArrayList<Inscripcion> inscripciones = new ArrayList<>();
    private String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_inscripciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        rvInscripciones = findViewById(R.id.rvInscripciones);
        tvVacio = findViewById(R.id.tvVacio);
        correo = getIntent().getStringExtra("correo");

        adapter = new InscripcionAdapter(inscripciones, this::mostrarOpciones);
        rvInscripciones.setLayoutManager(new LinearLayoutManager(this));
        rvInscripciones.setAdapter(adapter);

        cargarInscripciones();
    }

    private void cargarInscripciones() {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            long usuarioId = dbHelper.obtenerUsuarioId(correo);
            ArrayList<Inscripcion> lista = dbHelper.obtenerListaInscripciones(
                    usuarioId, VoluntariadoDbHelper.ESTADO_INSCRITO);
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed()) {
                    return;
                }
                inscripciones.clear();
                inscripciones.addAll(lista);
                adapter.notifyDataSetChanged();
                tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void mostrarOpciones(Inscripcion inscripcion) {
        String[] opciones = {getString(R.string.opcion_completar), getString(R.string.opcion_cancelar)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.opciones_titulo)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        completar(inscripcion);
                    } else {
                        confirmarCancelacion(inscripcion);
                    }
                })
                .show();
    }

    private void confirmarCancelacion(Inscripcion inscripcion) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cancelar_titulo)
                .setMessage(R.string.cancelar_mensaje)
                .setPositiveButton(R.string.cancelar_si, (dialog, which) -> cancelar(inscripcion))
                .setNegativeButton(R.string.cancelar_no, null)
                .show();
    }

    private void completar(Inscripcion inscripcion) {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            dbHelper.completarInscripcion(inscripcion.getInscripcionId());
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed()) {
                    return;
                }
                Toast.makeText(this, R.string.msg_inscripcion_completada, Toast.LENGTH_SHORT).show();
                RecordatorioHelper.cancelar(getApplicationContext(), inscripcion.getActividadId());
                cargarInscripciones();
            });
        });
    }

    private void cancelar(Inscripcion inscripcion) {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            dbHelper.cancelarInscripcion(inscripcion.getInscripcionId(), inscripcion.getActividadId());
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed()) {
                    return;
                }
                Toast.makeText(this, R.string.msg_inscripcion_cancelada, Toast.LENGTH_SHORT).show();
                RecordatorioHelper.cancelar(getApplicationContext(), inscripcion.getActividadId());
                cargarInscripciones();
            });
        });
    }
}