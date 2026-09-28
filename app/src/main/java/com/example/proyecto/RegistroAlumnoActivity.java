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

public class RegistroAlumnoActivity extends AppCompatActivity {

    private TextInputLayout tilNombre;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmar;
    private TextInputEditText etNombre;
    private TextInputEditText etCorreo;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmar;
    private Button btnRegistrarme;
    private TextView tvIrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_alumno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilNombre = findViewById(R.id.tilNombre);
        tilCorreo = findViewById(R.id.tilCorreo);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        btnRegistrarme = findViewById(R.id.btnRegistrarme);
        tvIrLogin = findViewById(R.id.tvIrLogin);

        btnRegistrarme.setOnClickListener(view -> validarRegistro());

        tvIrLogin.setOnClickListener(view -> irALogin());
    }

    private void validarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmar = etConfirmar.getText().toString().trim();

        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);

        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.error_nombre_vacio));
            return;
        }

        if (correo.isEmpty()) {
            tilCorreo.setError(getString(R.string.error_correo_vacio));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.error_correo_invalido));
            return;
        }

        if (password.length() < 6) {
            tilPassword.setError((getString(R.string.error_password_corta)));
            return;
        }

        if (!password.equals(confirmar)) {
            tilConfirmar.setError(getString(R.string.error_passwords_no_coinciden));
            return;
        }

        Toast.makeText(this, R.string.msg_registro_ok, Toast.LENGTH_SHORT).show();
        irALogin();
    }

    private void irALogin() {
        startActivity(new Intent(this, LoginAlumnoActivity.class));
        finish();
    }
}