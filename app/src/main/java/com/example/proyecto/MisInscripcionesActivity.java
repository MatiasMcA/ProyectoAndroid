package com.example.proyecto;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.VoluntariadoContract.ActividadEntry;

import java.util.ArrayList;

public class MisInscripcionesActivity extends AppCompatActivity {

    private ListView lvInscripciones;
    private TextView tvVacio;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> textos = new ArrayList<>();
    private final ArrayList<Long> inscripcionIds = new ArrayList<>();
    private final ArrayList<Long> actividadIds = new ArrayList<>();
    private VoluntariadoDbHelper dbHelper;
    private long usuarioId;

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

        lvInscripciones = findViewById(R.id.lvInscripciones);
        tvVacio = findViewById(R.id.tvVacio);

        String correo = getIntent().getStringExtra("correo");
        dbHelper = new VoluntariadoDbHelper(this);
        usuarioId = dbHelper.obtenerUsuarioId(correo);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textos);
        lvInscripciones.setAdapter(adapter);
        lvInscripciones.setEmptyView(tvVacio);
        lvInscripciones.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpciones(position));

        cargarInscripciones();
    }

    private void cargarInscripciones() {
        textos.clear();
        inscripcionIds.clear();
        actividadIds.clear();
        Cursor cursor = dbHelper.obtenerInscripciones(usuarioId, VoluntariadoDbHelper.ESTADO_INSCRITO);
        while (cursor.moveToNext()) {
            inscripcionIds.add(cursor.getLong(cursor.getColumnIndexOrThrow("inscripcion_id")));
            actividadIds.add(cursor.getLong(cursor.getColumnIndexOrThrow("actividad_id")));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
            String lugar = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
            textos.add(nombre + "\n" + fecha + " - " + lugar);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void mostrarOpciones(int position) {
        String[] opciones = {getString(R.string.opcion_completar), getString(R.string.opcion_cancelar)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.opciones_titulo)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        dbHelper.completarInscripcion(inscripcionIds.get(position));
                        cargarInscripciones();
                        Toast.makeText(this, R.string.msg_inscripcion_completada, Toast.LENGTH_SHORT).show();
                    } else {
                        mostrarDialogoCancelar(position);
                    }
                })
                .show();
    }

    private void mostrarDialogoCancelar(int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cancelar_titulo)
                .setMessage(R.string.cancelar_mensaje)
                .setPositiveButton(R.string.cancelar_si, (dialog, which) -> {
                    dbHelper.cancelarInscripcion(inscripcionIds.get(position), actividadIds.get(position));
                    cargarInscripciones();
                    Toast.makeText(this, R.string.msg_inscripcion_cancelada, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancelar_no, null)
                .show();
    }
}