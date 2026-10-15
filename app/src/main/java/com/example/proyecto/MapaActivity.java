package com.example.proyecto;

import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.VoluntariadoContract.ActividadEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private VoluntariadoDbHelper dbHelper;

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
        findViewById(R.id.btnVolver).setOnClickListener(view -> finish());

        dbHelper = new VoluntariadoDbHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        agregarMarcadoresActividades();
    }

    private void agregarMarcadoresActividades() {
        Cursor cursor = dbHelper.obtenerTodasLasActividades();
        LatLng primera = null;
        while (cursor.moveToNext()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
            String lugar = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
            double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LATITUD));
            double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LONGITUD));
            LatLng posicion = new LatLng(latitud, longitud);
            mMap.addMarker(new MarkerOptions().position(posicion).title(nombre).snippet(lugar));
            if (primera == null) {
                primera = posicion;
            }
        }
        cursor.close();
        if (primera != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(primera, 13f));
        }
    }
}
