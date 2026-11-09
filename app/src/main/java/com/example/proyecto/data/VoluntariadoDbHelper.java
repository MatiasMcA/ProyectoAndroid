package com.example.proyecto.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyecto.model.Actividad;
import com.example.proyecto.data.VoluntariadoContract.ActividadEntry;
import com.example.proyecto.data.VoluntariadoContract.InscripcionEntry;
import com.example.proyecto.data.VoluntariadoContract.UsuarioEntry;

import java.util.ArrayList;

public class VoluntariadoDbHelper extends SQLiteOpenHelper {

    public static final String ESTADO_INSCRITO = InscripcionEntry.ESTADO_INSCRITO;
    public static final String ESTADO_COMPLETADA = InscripcionEntry.ESTADO_COMPLETADA;
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
                    ActividadEntry.COLUMN_CUPOS + " INTEGER NOT NULL CHECK (" +
                    ActividadEntry.COLUMN_CUPOS + " >= 0), " +
                    ActividadEntry.COLUMN_LATITUD + " REAL NOT NULL, " +
                    ActividadEntry.COLUMN_LONGITUD + " REAL NOT NULL)";

    private static final String SQL_CREATE_INSCRIPCIONES =
            "CREATE TABLE " + InscripcionEntry.TABLE_NAME + " (" +
                    InscripcionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    InscripcionEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL, " +
                    InscripcionEntry.COLUMN_ACTIVIDAD_ID + " INTEGER NOT NULL, " +
                    InscripcionEntry.COLUMN_ESTADO + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + InscripcionEntry.COLUMN_USUARIO_ID + ") REFERENCES " +
                    UsuarioEntry.TABLE_NAME + "(" + UsuarioEntry._ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + InscripcionEntry.COLUMN_ACTIVIDAD_ID + ") REFERENCES " +
                    ActividadEntry.TABLE_NAME + "(" + ActividadEntry._ID + ") ON DELETE CASCADE, " +
                    "UNIQUE (" + InscripcionEntry.COLUMN_USUARIO_ID + ", " +
                    InscripcionEntry.COLUMN_ACTIVIDAD_ID + "))";

    private static final String SQL_DELETE_INSCRIPCIONES =
            "DROP TABLE IF EXISTS " + InscripcionEntry.TABLE_NAME;
    private static final String SQL_DELETE_ACTIVIDADES =
            "DROP TABLE IF EXISTS " + ActividadEntry.TABLE_NAME;
    private static final String SQL_DELETE_USUARIOS =
            "DROP TABLE IF EXISTS " + UsuarioEntry.TABLE_NAME;

    private static final String[] PROYECCION_USUARIO = {
            UsuarioEntry._ID,
            UsuarioEntry.COLUMN_NOMBRE,
            UsuarioEntry.COLUMN_CORREO,
            UsuarioEntry.COLUMN_CARRERA
    };

    private static final String[] PROYECCION_LISTA_ACTIVIDADES = {
            ActividadEntry._ID,
            ActividadEntry.COLUMN_NOMBRE,
            ActividadEntry.COLUMN_FECHA,
            ActividadEntry.COLUMN_LUGAR,
            ActividadEntry.COLUMN_CUPOS
    };

    private static final String[] PROYECCION_ACTIVIDAD_COMPLETA = {
            ActividadEntry._ID,
            ActividadEntry.COLUMN_NOMBRE,
            ActividadEntry.COLUMN_FECHA,
            ActividadEntry.COLUMN_LUGAR,
            ActividadEntry.COLUMN_DESCRIPCION,
            ActividadEntry.COLUMN_HORAS,
            ActividadEntry.COLUMN_CUPOS,
            ActividadEntry.COLUMN_LATITUD,
            ActividadEntry.COLUMN_LONGITUD
    };

    private static final String[] PROYECCION_MAPA = {
            ActividadEntry._ID,
            ActividadEntry.COLUMN_NOMBRE,
            ActividadEntry.COLUMN_LUGAR,
            ActividadEntry.COLUMN_LATITUD,
            ActividadEntry.COLUMN_LONGITUD
    };

    public VoluntariadoDbHelper(Context context) {
        super(context, VoluntariadoContract.DATABASE_NAME, null,
                VoluntariadoContract.DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
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
        db.execSQL(SQL_DELETE_INSCRIPCIONES);
        db.execSQL(SQL_DELETE_ACTIVIDADES);
        db.execSQL(SQL_DELETE_USUARIOS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
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
        try (Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public boolean validarLogin(String correo, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                UsuarioEntry.COLUMN_CORREO + " = ? AND " + UsuarioEntry.COLUMN_PASSWORD + " = ?",
                new String[]{correo, password},
                null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public Cursor obtenerUsuario(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(UsuarioEntry.TABLE_NAME,
                PROYECCION_USUARIO,
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
    }

    public long obtenerUsuarioId(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioEntry._ID));
            }
            return -1;
        }
    }

    public ArrayList<Actividad> obtenerActividades() {
        ArrayList<Actividad> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(ActividadEntry.TABLE_NAME,
                PROYECCION_LISTA_ACTIVIDADES,
                null, null, null, null,
                ActividadEntry._ID + " ASC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(ActividadEntry._ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
                String lugar = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
                int cupos = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_CUPOS));
                lista.add(new Actividad(id, nombre, fecha, lugar, cupos));
            }
        }
        return lista;
    }

    public Cursor obtenerActividad(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ActividadEntry.TABLE_NAME,
                PROYECCION_ACTIVIDAD_COMPLETA,
                ActividadEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public Cursor obtenerTodasLasActividades() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ActividadEntry.TABLE_NAME,
                PROYECCION_MAPA,
                null, null, null, null,
                ActividadEntry._ID + " ASC");
    }

    public int inscribirUsuario(long usuarioId, long actividadId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            boolean yaInscrito = false;
            try (Cursor existente = db.query(InscripcionEntry.TABLE_NAME,
                    new String[]{InscripcionEntry._ID},
                    InscripcionEntry.COLUMN_USUARIO_ID + " = ? AND " +
                            InscripcionEntry.COLUMN_ACTIVIDAD_ID + " = ?",
                    new String[]{String.valueOf(usuarioId), String.valueOf(actividadId)},
                    null, null, null)) {
                yaInscrito = existente.getCount() > 0;
            }
            if (yaInscrito) {
                return RESULTADO_YA_INSCRITO;
            }

            int cupos = 0;
            try (Cursor actividad = db.query(ActividadEntry.TABLE_NAME,
                    new String[]{ActividadEntry.COLUMN_CUPOS},
                    ActividadEntry._ID + " = ?",
                    new String[]{String.valueOf(actividadId)},
                    null, null, null)) {
                if (actividad.moveToFirst()) {
                    cupos = actividad.getInt(actividad.getColumnIndexOrThrow(ActividadEntry.COLUMN_CUPOS));
                }
            }
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

            db.setTransactionSuccessful();
            return RESULTADO_OK;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor obtenerInscripciones(long usuarioId, String estado) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT i." + InscripcionEntry._ID + " AS " + InscripcionEntry.ALIAS_INSCRIPCION_ID + ", " +
                "a." + ActividadEntry._ID + " AS " + InscripcionEntry.ALIAS_ACTIVIDAD_ID + ", " +
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
        db.beginTransaction();
        try {
            db.delete(InscripcionEntry.TABLE_NAME,
                    InscripcionEntry._ID + " = ?",
                    new String[]{String.valueOf(inscripcionId)});
            db.execSQL("UPDATE " + ActividadEntry.TABLE_NAME + " SET " + ActividadEntry.COLUMN_CUPOS +
                            " = " + ActividadEntry.COLUMN_CUPOS + " + 1 WHERE " + ActividadEntry._ID + " = ?",
                    new Object[]{actividadId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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
        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(usuarioId), ESTADO_COMPLETADA})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        }
    }
}