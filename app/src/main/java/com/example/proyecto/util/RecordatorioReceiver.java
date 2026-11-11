package com.example.proyecto.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.proyecto.R;
import com.example.proyecto.ui.auth.MainActivity;

public class RecordatorioReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String nombre = intent.getStringExtra(RecordatorioHelper.EXTRA_NOMBRE);
        String fecha = intent.getStringExtra(RecordatorioHelper.EXTRA_FECHA);
        String lugar = intent.getStringExtra(RecordatorioHelper.EXTRA_LUGAR);
        if (nombre == null) {
            nombre = "";
        }

        NotificationChannel canal = new NotificationChannel(RecordatorioHelper.CANAL_ID,
                context.getString(R.string.canal_recordatorios),
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(canal);

        Intent abrir = new Intent(context, MainActivity.class);
        PendingIntent pendiente = PendingIntent.getActivity(context, 0, abrir,
                PendingIntent.FLAG_IMMUTABLE);

        Notification notificacion = new NotificationCompat.Builder(context, RecordatorioHelper.CANAL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setContentTitle(context.getString(R.string.recordatorio_titulo))
                .setContentText(context.getString(R.string.recordatorio_texto, nombre, fecha, lugar))
                .setContentIntent(pendiente)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(context).notify(nombre.hashCode(), notificacion);
    }
}
