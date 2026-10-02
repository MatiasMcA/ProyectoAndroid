package com.example.proyecto;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HistorialHorasActivity extends AppCompatActivity {
    private ListView lvHistorial;
    private TextView tvTotalHoras;

    private String[] actividadesRealizadas = {
            "Apoyo escolar comuna - 3 horas",
            "Jornada de reciclaje - 2 horas"
    };

    private int totalHoras = 5;

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

        lvHistorial = findViewById(R.id.lvHistorial);
        tvTotalHoras = findViewById(R.id.tvTotalHoras);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actividadesRealizadas);
        lvHistorial.setAdapter(adapter);

        tvTotalHoras.setText(getString(R.string.historial_total, totalHoras));
    }
}
