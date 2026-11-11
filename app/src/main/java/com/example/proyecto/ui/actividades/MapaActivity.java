package com.example.proyecto.ui.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.R;
import com.example.proyecto.data.VoluntariadoContract.ActividadEntry;
import com.example.proyecto.data.VoluntariadoDbHelper;
import com.example.proyecto.util.EjecutorBd;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQ_CODE = 100;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedClient;
    private boolean ubicacionCentrada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        agregarMarcadoresActividades();
        verificarPermisoUbicacion();
    }

    private void agregarMarcadoresActividades() {
        EjecutorBd.enSegundoPlano(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(getApplicationContext());
            ArrayList<String> nombres = new ArrayList<>();
            ArrayList<String> lugares = new ArrayList<>();
            ArrayList<LatLng> posiciones = new ArrayList<>();
            try (Cursor cursor = dbHelper.obtenerTodasLasActividades()) {
                while (cursor.moveToNext()) {
                    nombres.add(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE)));
                    lugares.add(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR)));
                    double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LATITUD));
                    double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LONGITUD));
                    posiciones.add(new LatLng(latitud, longitud));
                }
            }
            dbHelper.close();

            EjecutorBd.enPrincipal(() -> {
                if (isDestroyed() || mMap == null) {
                    return;
                }
                for (int i = 0; i < posiciones.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(posiciones.get(i))
                            .title(nombres.get(i))
                            .snippet(lugares.get(i)));
                }
                if (!posiciones.isEmpty() && !ubicacionCentrada) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posiciones.get(0), 13f));
                }
            });
        });
    }

    private void verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQ_CODE);
        } else {
            mostrarUbicacionUsuario();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQ_CODE) {
            boolean concedido = false;
            for (int resultado : grantResults) {
                if (resultado == PackageManager.PERMISSION_GRANTED) {
                    concedido = true;
                }
            }
            if (concedido) {
                mostrarUbicacionUsuario();
            } else {
                Toast.makeText(this, R.string.msg_permiso_denegado, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void mostrarUbicacionUsuario() {
        mMap.setMyLocationEnabled(true);
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location == null) {
                        Toast.makeText(this, R.string.msg_ubicacion_no_disponible, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
                    Marker marcador = mMap.addMarker(new MarkerOptions()
                            .position(posicion)
                            .title(getString(R.string.marcador_usuario))
                            .snippet(location.getLatitude() + ", " + location.getLongitude())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    if (marcador != null) {
                        marcador.showInfoWindow();
                    }
                    ubicacionCentrada = true;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 14f));
                });
    }
}