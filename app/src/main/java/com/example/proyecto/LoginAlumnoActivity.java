package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginAlumnoActivity extends AppCompatActivity {

    private TextInputLayout tilCorreo;
    private TextInputLayout tilPassword;
    private TextInputEditText etCorreo;
    private TextInputEditText etPassword;
    private Button btnIngresar;
    private TextView tvIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_alumno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilCorreo = findViewById(R.id.tilCorreo);
        tilPassword = findViewById(R.id.tilPassword);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnIngresar.setOnClickListener(view -> validarLogin());

        tvIrRegistro.setOnClickListener(view ->
                startActivity(new Intent(this, RegistroAlumnoActivity.class)));
    }

    private void validarLogin() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilCorreo.setError(null);
        tilPassword.setError(null);

        if (correo.isEmpty()) {
            tilCorreo.setError(getString(R.string.error_correo_vacio));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.error_correo_invalido));
            return;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_password_vacia));
            return;
        }

        Toast.makeText(this, R.string.msg_login_ok, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ListaActividadesActivity.class));
        finish();
    }
}