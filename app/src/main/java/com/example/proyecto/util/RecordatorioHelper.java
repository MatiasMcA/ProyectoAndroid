package com.example.proyecto.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public final class RecordatorioHelper {

    public static final String CANAL_ID = "recordatorios_voluntariado";
    public static final String EXTRA_NOMBRE = "nombre_actividad";
    public static final String EXTRA_FECHA = "fecha_actividad";
    public static final String EXTRA_LUGAR = "lugar_actividad";

    private static final int HORA_RECORDATORIO = 9;
    private static final long ESPERA_MINIMA_MS = 60000;

    private RecordatorioHelper() {
    }

    public static void programar(Context context, long actividadId, String nombre,
                                 String fecha, String lugar) {
        Calendar dia = parsearFecha(fecha);
        if (dia == null) {
            return;
        }
        long ahora = System.currentTimeMillis();

        Calendar finActividad = (Calendar) dia.clone();
        finActividad.add(Calendar.DAY_OF_YEAR, 1);
        if (finActividad.getTimeInMillis() <= ahora) {
            return;
        }

        Calendar recordatorio = (Calendar) dia.clone();
        recordatorio.add(Calendar.DAY_OF_YEAR, -1);
        recordatorio.set(Calendar.HOUR_OF_DAY, HORA_RECORDATORIO);
        long momento = Math.max(recordatorio.getTimeInMillis(), ahora + ESPERA_MINIMA_MS);

        Intent intent = new Intent(context, RecordatorioReceiver.class);
        intent.putExtra(EXTRA_NOMBRE, nombre);
        intent.putExtra(EXTRA_FECHA, fecha);
        intent.putExtra(EXTRA_LUGAR, lugar);
        PendingIntent pendiente = PendingIntent.getBroadcast(context, (int) actividadId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, momento, pendiente);
    }

    public static void cancelar(Context context, long actividadId) {
        Intent intent = new Intent(context, RecordatorioReceiver.class);
        PendingIntent pendiente = PendingIntent.getBroadcast(context, (int) actividadId, intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendiente != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendiente);
            pendiente.cancel();
        }
    }

    private static Calendar parsearFecha(String fecha) {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        formato.setLenient(false);
        try {
            Date date = formato.parse(fecha);
            if (date == null) {
                return null;
            }
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(date);
            return calendario;
        } catch (ParseException e) {
            return null;
        }
    }
}
