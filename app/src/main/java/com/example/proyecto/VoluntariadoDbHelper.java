package com.example.proyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import com.example.proyecto.VoluntariadoContract.ActividadEntry;
import com.example.proyecto.VoluntariadoContract.InscripcionEntry;
import com.example.proyecto.VoluntariadoContract.UsuarioEntry;
public class VoluntariadoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "voluntariado.db";
    private static final int DATABASE_VERSION = 1;
    public static final String ESTADO_INSCRITO = "Inscrito";
    public static final String ESTADO_COMPLETADA = "Completada";
    public static final int RESULTADO_OK = 0;
    public static final int RESULTADO_YA_INSCRITO = 1;
    public static final int RESULTADO_SIN_CUPOS = 2;
    public static final int RESULTADO_ERROR = 3;


    private static final String SQL_CREATE_USUARIOS =
            "CREATE TABLE " + UsuarioEntry.TABLE_NAME + " (" +
                    UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UsuarioEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    UsuarioEntry.COLUMN_CORREO + " TEXT NOT NULL UNIQUE, " +
                    UsuarioEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    UsuarioEntry.COLUMN_CARRERA + " TEXT NOT NULL)";

    private static final String SQL_CREATE_ACTIVIDADES =
            "CREATE TABLE " + ActividadEntry.TABLE_NAME + " (" +
                    ActividadEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ActividadEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    ActividadEntry.COLUMN_FECHA + " TEXT NOT NULL, " +
                    ActividadEntry.COLUMN_LUGAR + " TEXT NOT NULL, " +
                    ActividadEntry.COLUMN_DESCRIPCION + " TEXT NOT NULL, " +
                    ActividadEntry.COLUMN_HORAS + " INTEGER NOT NULL, " +
                    ActividadEntry.COLUMN_CUPOS + " INTEGER NOT NULL, " +
                    ActividadEntry.COLUMN_LATITUD + " REAL NOT NULL, " +
                    ActividadEntry.COLUMN_LONGITUD + " REAL NOT NULL)";

    private static final String SQL_CREATE_INSCRIPCIONES =
            "CREATE TABLE " + InscripcionEntry.TABLE_NAME + " (" +
                    InscripcionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    InscripcionEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL, " +
                    InscripcionEntry.COLUMN_ACTIVIDAD_ID + " INTEGER NOT NULL, " +
                    InscripcionEntry.COLUMN_ESTADO + " TEXT NOT NULL)";

    public VoluntariadoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USUARIOS);
        db.execSQL(SQL_CREATE_ACTIVIDADES);
        db.execSQL(SQL_CREATE_INSCRIPCIONES);
        insertarActividadesIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InscripcionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ActividadEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsuarioEntry.TABLE_NAME);
        onCreate(db);
    }

    private void insertarActividad(SQLiteDatabase db, String nombre, String fecha, String lugar,
                                   String descripcion, int horas, int cupos,
                                   double latitud, double longitud) {
        ContentValues valores = new ContentValues();
        valores.put(ActividadEntry.COLUMN_NOMBRE, nombre);
        valores.put(ActividadEntry.COLUMN_FECHA, fecha);
        valores.put(ActividadEntry.COLUMN_LUGAR, lugar);
        valores.put(ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        valores.put(ActividadEntry.COLUMN_HORAS, horas);
        valores.put(ActividadEntry.COLUMN_CUPOS, cupos);
        valores.put(ActividadEntry.COLUMN_LATITUD, latitud);
        valores.put(ActividadEntry.COLUMN_LONGITUD, longitud);
        db.insert(ActividadEntry.TABLE_NAME, null, valores);
    }

    private void insertarActividadesIniciales(SQLiteDatabase db) {
        insertarActividad(db, "Apoyo escolar comuna", "20/10/2026",
                "Escuela Básica San Martín",
                "Apoyo en tareas y reforzamiento escolar para niños.",
                3, 10, -20.2150, -70.1480);
        insertarActividad(db, "Jornada de reciclaje", "24/10/2026",
                "Plaza Prat",
                "Recolección y clasificación de materiales reciclables.",
                2, 15, -20.2133, -70.1520);
        insertarActividad(db, "Acompañamiento adulto mayor", "28/10/2026",
                "Hogar de Ancianos Santo Tomás",
                "Actividades recreativas con adultos mayores del hogar.",
                4, 8, -20.2250, -70.1450);
        insertarActividad(db, "Limpieza de playa", "31/10/2026",
                "Playa Cavancha",
                "Limpieza de la playa junto a otros voluntarios.",
                3, 20, -20.2349, -70.1417);
    }

    public long insertarUsuario(String nombre, String correo, String password, String carrera) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(UsuarioEntry.COLUMN_NOMBRE, nombre);
        valores.put(UsuarioEntry.COLUMN_CORREO, correo);
        valores.put(UsuarioEntry.COLUMN_PASSWORD, password);
        valores.put(UsuarioEntry.COLUMN_CARRERA, carrera);
        return db.insert(UsuarioEntry.TABLE_NAME, null, valores);
    }

    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    public boolean validarLogin(String correo, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                UsuarioEntry.COLUMN_CORREO + " = ? AND " + UsuarioEntry.COLUMN_PASSWORD + " = ?",
                new String[]{correo, password},
                null, null, null);
        boolean valido = cursor.getCount() > 0;
        cursor.close();
        return valido;
    }

    public Cursor obtenerUsuario(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(UsuarioEntry.TABLE_NAME,
                null,
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
    }

    public ArrayList<Actividad> obtenerActividades() {
        ArrayList<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ActividadEntry.TABLE_NAME, null, null, null, null, null,
                ActividadEntry._ID + " ASC");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ActividadEntry._ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
            String lugar = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
            int cupos = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_CUPOS));
            lista.add(new Actividad(id, nombre, fecha, lugar, cupos));
        }
        cursor.close();
        return lista;
    }

    public Cursor obtenerActividad(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ActividadEntry.TABLE_NAME,
                null,
                ActividadEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public long obtenerUsuarioId(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    public int inscribirUsuario(long usuarioId, long actividadId) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor existente = db.query(InscripcionEntry.TABLE_NAME,
                new String[]{InscripcionEntry._ID},
                InscripcionEntry.COLUMN_USUARIO_ID + " = ? AND " + InscripcionEntry.COLUMN_ACTIVIDAD_ID + " = ?",
                new String[]{String.valueOf(usuarioId), String.valueOf(actividadId)},
                null, null, null);
        boolean yaInscrito = existente.getCount() > 0;
        existente.close();
        if (yaInscrito) {
            return RESULTADO_YA_INSCRITO;
        }

        Cursor actividad = obtenerActividad(actividadId);
        int cupos = 0;
        if (actividad.moveToFirst()) {
            cupos = actividad.getInt(actividad.getColumnIndexOrThrow(ActividadEntry.COLUMN_CUPOS));
        }
        actividad.close();
        if (cupos <= 0) {
            return RESULTADO_SIN_CUPOS;
        }

        ContentValues valores = new ContentValues();
        valores.put(InscripcionEntry.COLUMN_USUARIO_ID, usuarioId);
        valores.put(InscripcionEntry.COLUMN_ACTIVIDAD_ID, actividadId);
        valores.put(InscripcionEntry.COLUMN_ESTADO, ESTADO_INSCRITO);
        long resultado = db.insert(InscripcionEntry.TABLE_NAME, null, valores);
        if (resultado == -1) {
            return RESULTADO_ERROR;
        }

        ContentValues nuevosCupos = new ContentValues();
        nuevosCupos.put(ActividadEntry.COLUMN_CUPOS, cupos - 1);
        db.update(ActividadEntry.TABLE_NAME, nuevosCupos,
                ActividadEntry._ID + " = ?",
                new String[]{String.valueOf(actividadId)});
        return RESULTADO_OK;
    }

    public Cursor obtenerInscripciones(long usuarioId, String estado) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT i." + InscripcionEntry._ID + " AS inscripcion_id, " +
                "a." + ActividadEntry._ID + " AS actividad_id, " +
                "a." + ActividadEntry.COLUMN_NOMBRE + ", " +
                "a." + ActividadEntry.COLUMN_FECHA + ", " +
                "a." + ActividadEntry.COLUMN_LUGAR + ", " +
                "a." + ActividadEntry.COLUMN_HORAS +
                " FROM " + InscripcionEntry.TABLE_NAME + " i INNER JOIN " +
                ActividadEntry.TABLE_NAME + " a ON i." + InscripcionEntry.COLUMN_ACTIVIDAD_ID +
                " = a." + ActividadEntry._ID +
                " WHERE i." + InscripcionEntry.COLUMN_USUARIO_ID + " = ? AND i." +
                InscripcionEntry.COLUMN_ESTADO + " = ?";
        return db.rawQuery(sql, new String[]{String.valueOf(usuarioId), estado});
    }

    public void cancelarInscripcion(long inscripcionId, long actividadId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(InscripcionEntry.TABLE_NAME,
                InscripcionEntry._ID + " = ?",
                new String[]{String.valueOf(inscripcionId)});
        db.execSQL("UPDATE " + ActividadEntry.TABLE_NAME + " SET " + ActividadEntry.COLUMN_CUPOS +
                        " = " + ActividadEntry.COLUMN_CUPOS + " + 1 WHERE " + ActividadEntry._ID + " = ?",
                new Object[]{actividadId});
    }

    public void completarInscripcion(long inscripcionId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(InscripcionEntry.COLUMN_ESTADO, ESTADO_COMPLETADA);
        db.update(InscripcionEntry.TABLE_NAME, valores,
                InscripcionEntry._ID + " = ?",
                new String[]{String.valueOf(inscripcionId)});
    }

    public int obtenerTotalHoras(long usuarioId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT SUM(a." + ActividadEntry.COLUMN_HORAS + ") FROM " +
                InscripcionEntry.TABLE_NAME + " i INNER JOIN " + ActividadEntry.TABLE_NAME +
                " a ON i." + InscripcionEntry.COLUMN_ACTIVIDAD_ID + " = a." + ActividadEntry._ID +
                " WHERE i." + InscripcionEntry.COLUMN_USUARIO_ID + " = ? AND i." +
                InscripcionEntry.COLUMN_ESTADO + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(usuarioId), ESTADO_COMPLETADA});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public Cursor obtenerTodasLasActividades() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ActividadEntry.TABLE_NAME, null, null, null, null, null,
                ActividadEntry._ID + " ASC");
    }
}
