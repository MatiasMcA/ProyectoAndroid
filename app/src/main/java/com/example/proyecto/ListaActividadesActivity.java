package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListaActividadesActivity extends AppCompatActivity implements SensorEventListener {

    private static final float UMBRAL_SACUDIDA = 2.5f;
    private static final long INTERVALO_MS = 1500;

    private RecyclerView rvActividades;
    private Button btnVerHistorial;
    private VoluntariadoDbHelper dbHelper;
    private String correo;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handlerPrincipal = new Handler(Looper.getMainLooper());

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private HandlerThread hiloSensor;
    private long ultimaSacudida = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_actividades);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        rvActividades = findViewById(R.id.rvActividades);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);

        correo = getIntent().getStringExtra("correo");
        dbHelper = new VoluntariadoDbHelper(this);
        rvActividades.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistorialHorasActivity.class);
            intent.putExtra("correo", correo);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarActividades();
        registrarSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detenerSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void cargarActividades() {
        executor.execute(() -> {
            ArrayList<Actividad> actividades = dbHelper.obtenerActividades();
            handlerPrincipal.post(() -> {
                if (isDestroyed()) {
                    return;
                }
                rvActividades.setAdapter(new ActividadAdapter(actividades, actividad -> {
                    Intent intent = new Intent(this, DetalleActividadActivity.class);
                    intent.putExtra("correo", correo);
                    intent.putExtra("actividad_id", actividad.getId());
                    startActivity(intent);
                }));
            });
        });
    }

    private void registrarSensor() {
        if (acelerometro == null) {
            return;
        }
        hiloSensor = new HandlerThread("hilo-sensor");
        hiloSensor.start();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL,
                new Handler(hiloSensor.getLooper()));
    }

    private void detenerSensor() {
        sensorManager.unregisterListener(this);
        if (hiloSensor != null) {
            hiloSensor.quitSafely();
            hiloSensor = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float fuerzaG = (float) Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;
        long ahora = System.currentTimeMillis();

        if (fuerzaG > UMBRAL_SACUDIDA && ahora - ultimaSacudida > INTERVALO_MS) {
            ultimaSacudida = ahora;
            runOnUiThread(() ->
                    Toast.makeText(this, R.string.msg_lista_actualizada, Toast.LENGTH_SHORT).show());
            cargarActividades();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}