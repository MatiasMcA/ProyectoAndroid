package com.example.proyecto.ui.menu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto.R;
import com.example.proyecto.data.VoluntariadoContract.UsuarioEntry;
import com.example.proyecto.data.VoluntariadoDbHelper;
import com.example.proyecto.ui.auth.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerfilActivity extends AppCompatActivity {

    private static final int ANCHO_MAXIMO_FOTO = 400;

    private TextView tvNombrePerfil;
    private TextView tvCorreoPerfil;
    private TextView tvCarreraPerfil;
    private TextView tvSedePerfil;
    private TextView tvHorasPerfil;
    private ImageView ivFotoPerfil;
    private Button btnTomarFoto;
    private Button btnAcerca;
    private Button btnCerrarSesion;

    private final String sede = "Santo Tomás Iquique";
    private long usuarioId = -1;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handlerPrincipal = new Handler(Looper.getMainLooper());
    private ActivityResultLauncher<Intent> lanzadorCamara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil);
        tvCarreraPerfil = findViewById(R.id.tvCarreraPerfil);
        tvSedePerfil = findViewById(R.id.tvSedePerfil);
        tvHorasPerfil = findViewById(R.id.tvHorasPerfil);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnAcerca = findViewById(R.id.btnAcerca);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        lanzadorCamara = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        cargarFotoPerfil();
                        Toast.makeText(this, R.string.msg_foto_guardada, Toast.LENGTH_SHORT).show();
                    }
                });

        String correo = getIntent().getStringExtra("correo");
        tvCorreoPerfil.setText(getString(R.string.perfil_correo, correo));
        tvSedePerfil.setText(getString(R.string.perfil_sede, sede));
        cargarDatosUsuario(correo);

        btnTomarFoto.setOnClickListener(v -> tomarFoto());

        btnAcerca.setOnClickListener(v ->
                startActivity(new Intent(this, AcercaActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void cargarDatosUsuario(String correo) {
        executor.execute(() -> {
            VoluntariadoDbHelper dbHelper = new VoluntariadoDbHelper(this);
            String nombre = "";
            String carrera = "";
            try (Cursor cursor = dbHelper.obtenerUsuario(correo)) {
                if (cursor.moveToFirst()) {
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE));
                    carrera = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_CARRERA));
                }
            }
            long id = dbHelper.obtenerUsuarioId(correo);
            int horas = dbHelper.obtenerTotalHoras(id);
            dbHelper.close();

            String nombreFinal = nombre;
            String carreraFinal = carrera;
            handlerPrincipal.post(() -> {
                if (isDestroyed()) {
                    return;
                }
                usuarioId = id;
                tvNombrePerfil.setText(getString(R.string.perfil_nombre, nombreFinal));
                tvCarreraPerfil.setText(getString(R.string.perfil_carrera, carreraFinal));
                tvHorasPerfil.setText(getString(R.string.perfil_horas, horas));
                cargarFotoPerfil();
            });
        });
    }

    private void tomarFoto() {
        File carpeta = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (carpeta == null || usuarioId < 0) {
            Toast.makeText(this, R.string.error_foto, Toast.LENGTH_SHORT).show();
            return;
        }
        File archivoFoto = new File(carpeta, "perfil_" + usuarioId + ".jpg");
        Uri uriFoto = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivoFoto);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            lanzadorCamara.launch(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_sin_camara, Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarFotoPerfil() {
        File carpeta = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (carpeta == null || usuarioId < 0) {
            return;
        }
        File archivo = new File(carpeta, "perfil_" + usuarioId + ".jpg");
        if (!archivo.exists() || archivo.length() == 0) {
            return;
        }
        executor.execute(() -> {
            Bitmap bitmap = decodificarFoto(archivo);
            handlerPrincipal.post(() -> {
                if (!isDestroyed() && bitmap != null) {
                    ivFotoPerfil.setImageBitmap(bitmap);
                }
            });
        });
    }

    private Bitmap decodificarFoto(File archivo) {
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(archivo.getAbsolutePath(), opciones);
        int muestra = 1;
        while (opciones.outWidth / muestra > ANCHO_MAXIMO_FOTO) {
            muestra *= 2;
        }
        opciones.inJustDecodeBounds = false;
        opciones.inSampleSize = muestra;
        Bitmap bitmap = BitmapFactory.decodeFile(archivo.getAbsolutePath(), opciones);
        if (bitmap == null) {
            return null;
        }
        return rotarSegunExif(bitmap, archivo);
    }

    private Bitmap rotarSegunExif(Bitmap bitmap, File archivo) {
        try {
            ExifInterface exif = new ExifInterface(archivo.getAbsolutePath());
            int orientacion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int grados = 0;
            if (orientacion == ExifInterface.ORIENTATION_ROTATE_90) {
                grados = 90;
            } else if (orientacion == ExifInterface.ORIENTATION_ROTATE_180) {
                grados = 180;
            } else if (orientacion == ExifInterface.ORIENTATION_ROTATE_270) {
                grados = 270;
            }
            if (grados == 0) {
                return bitmap;
            }
            Matrix matriz = new Matrix();
            matriz.postRotate(grados);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matriz, true);
        } catch (IOException e) {
            return bitmap;
        }
    }
}