package com.example.proyecto.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyecto.model.Actividad;
import com.example.proyecto.model.Inscripcion;
import com.example.proyecto.data.VoluntariadoContract.ActividadEntry;
import com.example.proyecto.data.VoluntariadoContract.InscripcionEntry;
import com.example.proyecto.data.VoluntariadoContract.UsuarioEntry;
import com.example.proyecto.util.SeguridadUtil;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Ayudante de SQLite de VoluntariST.
 * <p>
 * Crea las tablas definidas en {@link VoluntariadoContract}, activa las claves foráneas y ofrece las
 * operaciones de guardar, consultar, actualizar y eliminar (CRUD) de usuarios, actividades e
 * inscripciones. Todas las consultas usan argumentos parametrizados para evitar inyección SQL.
 * <p>
 * Como accede a la base de datos, sus métodos deben llamarse fuera del hilo principal
 * (por ejemplo, con {@code EjecutorBd}).
 */
public class VoluntariadoDbHelper extends SQLiteOpenHelper {

    /** Estado de una inscripción vigente. */
    public static final String ESTADO_INSCRITO = InscripcionEntry.ESTADO_INSCRITO;

    /** Estado de una inscripción completada. */
    public static final String ESTADO_COMPLETADA = InscripcionEntry.ESTADO_COMPLETADA;

    /** Resultado de inscribirUsuario: la inscripción se guardó correctamente. */
    public static final int RESULTADO_OK = 0;

    /** Resultado de inscribirUsuario: el usuario ya estaba inscrito en la actividad. */
    public static final int RESULTADO_YA_INSCRITO = 1;

    /** Resultado de inscribirUsuario: la actividad no tiene cupos disponibles. */
    public static final int RESULTADO_SIN_CUPOS = 2;

    /** Resultado de inscribirUsuario: ocurrió un error al guardar la inscripción. */
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

    /**
     * Crea el ayudante con el nombre y la versión definidos en el contrato.
     *
     * @param context contexto de la aplicación, usado para ubicar el archivo de la base de datos
     */
    public VoluntariadoDbHelper(Context context) {
        super(context, VoluntariadoContract.DATABASE_NAME, null,
                VoluntariadoContract.DATABASE_VERSION);
    }

    /**
     * Se ejecuta al abrir la conexión y activa las claves foráneas de SQLite.
     *
     * @param db base de datos que se está configurando
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Crea las tablas usuarios, actividades e inscripciones y carga las actividades de ejemplo.
     *
     * @param db base de datos recién creada
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USUARIOS);
        db.execSQL(SQL_CREATE_ACTIVIDADES);
        db.execSQL(SQL_CREATE_INSCRIPCIONES);
        insertarActividadesIniciales(db);
    }

    /**
     * Se ejecuta cuando sube la versión del esquema: elimina las tablas (primero las que dependen de
     * otras) y las vuelve a crear. Los datos existentes se pierden.
     *
     * @param db         base de datos que se actualiza
     * @param oldVersion versión anterior del esquema
     * @param newVersion versión nueva del esquema
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_INSCRIPCIONES);
        db.execSQL(SQL_DELETE_ACTIVIDADES);
        db.execSQL(SQL_DELETE_USUARIOS);
        onCreate(db);
    }

    /**
     * Se ejecuta cuando la versión del esquema baja; aplica el mismo procedimiento que onUpgrade.
     *
     * @param db         base de datos que se reconstruye
     * @param oldVersion versión anterior del esquema
     * @param newVersion versión nueva del esquema
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Inserta una actividad en la tabla actividades.
     *
     * @param db          base de datos en la que se inserta
     * @param nombre      nombre de la actividad
     * @param fecha       fecha con formato dd/MM/yyyy
     * @param lugar       lugar donde se realiza
     * @param descripcion descripción breve de la actividad
     * @param horas       horas de voluntariado que otorga
     * @param cupos       cupos disponibles
     * @param latitud     latitud usada para el marcador del mapa
     * @param longitud    longitud usada para el marcador del mapa
     */
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

    /**
     * Carga cuatro actividades de ejemplo con fechas relativas al día en que se crea la base de datos.
     *
     * @param db base de datos en la que se insertan las actividades
     */
    private void insertarActividadesIniciales(SQLiteDatabase db) {
        insertarActividad(db, "Apoyo escolar comuna", fechaEnDias(1),
                "Escuela Básica San Martín",
                "Apoyo en tareas y reforzamiento escolar para niños.",
                3, 10, -20.2150, -70.1480);
        insertarActividad(db, "Jornada de reciclaje", fechaEnDias(5),
                "Plaza Prat",
                "Recolección y clasificación de materiales reciclables.",
                2, 15, -20.2133, -70.1520);
        insertarActividad(db, "Acompañamiento adulto mayor", fechaEnDias(9),
                "Hogar de Ancianos Santo Tomás",
                "Actividades recreativas con adultos mayores del hogar.",
                4, 8, -20.2250, -70.1450);
        insertarActividad(db, "Limpieza de playa", fechaEnDias(14),
                "Playa Cavancha",
                "Limpieza de la playa junto a otros voluntarios.",
                3, 20, -20.2349, -70.1417);
    }

    /**
     * Guarda un usuario nuevo. La contraseña se almacena como hash con sal, nunca en texto plano.
     *
     * @param nombre   nombre completo del usuario
     * @param correo   correo del usuario; debe ser único
     * @param password contraseña escrita por el usuario, antes de protegerla
     * @param carrera  carrera elegida en el registro
     * @return identificador de la fila insertada, o -1 si no se pudo guardar (por ejemplo, correo repetido)
     */
    public long insertarUsuario(String nombre, String correo, String password, String carrera) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(UsuarioEntry.COLUMN_NOMBRE, nombre);
        valores.put(UsuarioEntry.COLUMN_CORREO, correo);
        valores.put(UsuarioEntry.COLUMN_PASSWORD, SeguridadUtil.generarHash(password));
        valores.put(UsuarioEntry.COLUMN_CARRERA, carrera);
        return db.insert(UsuarioEntry.TABLE_NAME, null, valores);
    }

    /**
     * Comprueba si ya existe un usuario con ese correo.
     *
     * @param correo correo que se quiere comprobar
     * @return true si el correo ya está registrado
     */
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

    /**
     * Verifica las credenciales de inicio de sesión: busca al usuario por correo y compara la
     * contraseña con el hash guardado.
     *
     * @param correo   correo escrito por el usuario
     * @param password contraseña escrita por el usuario
     * @return true si el correo existe y la contraseña coincide
     * @throws IllegalArgumentException si la tabla no tiene la columna de contraseña (error de esquema)
     */
    public boolean validarLogin(String correo, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry.COLUMN_PASSWORD},
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null)) {
            if (!cursor.moveToFirst()) {
                return false;
            }
            String almacenado = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_PASSWORD));
            return SeguridadUtil.verificar(password, almacenado);
        }
    }

    /**
     * Consulta los datos de un usuario (id, nombre, correo y carrera) a partir de su correo.
     *
     * @param correo correo del usuario
     * @return cursor con a lo más una fila; quien lo recibe debe cerrarlo
     */
    public Cursor obtenerUsuario(String correo) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(UsuarioEntry.TABLE_NAME,
                PROYECCION_USUARIO,
                UsuarioEntry.COLUMN_CORREO + " = ?",
                new String[]{correo},
                null, null, null);
    }

    /**
     * Obtiene el identificador de un usuario a partir de su correo.
     *
     * @param correo correo del usuario
     * @return id del usuario, o -1 si no existe
     * @throws IllegalArgumentException si la tabla no tiene la columna de id (error de esquema)
     */
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

    /**
     * Obtiene todas las actividades ordenadas por id.
     *
     * @return lista de actividades (vacía si no hay ninguna)
     * @throws IllegalArgumentException si falta una columna esperada en el resultado (error de esquema)
     */
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

    /**
     * Consulta todos los datos de una actividad.
     *
     * @param id identificador de la actividad
     * @return cursor con a lo más una fila; quien lo recibe debe cerrarlo
     */
    public Cursor obtenerActividad(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ActividadEntry.TABLE_NAME,
                PROYECCION_ACTIVIDAD_COMPLETA,
                ActividadEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    /**
     * Consulta nombre, lugar y coordenadas de todas las actividades para dibujar los marcadores del mapa.
     *
     * @return cursor con una fila por actividad; quien lo recibe debe cerrarlo
     */
    public Cursor obtenerTodasLasActividades() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ActividadEntry.TABLE_NAME,
                PROYECCION_MAPA,
                null, null, null, null,
                ActividadEntry._ID + " ASC");
    }

    /**
     * Inscribe a un usuario en una actividad dentro de una transacción: comprueba que no esté
     * inscrito y que queden cupos, guarda la inscripción y descuenta un cupo.
     *
     * @param usuarioId   identificador del usuario
     * @param actividadId identificador de la actividad
     * @return {@link #RESULTADO_OK}, {@link #RESULTADO_YA_INSCRITO}, {@link #RESULTADO_SIN_CUPOS}
     *         o {@link #RESULTADO_ERROR}
     * @throws IllegalArgumentException si falta la columna de cupos (error de esquema)
     */
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

    /**
     * Consulta las inscripciones de un usuario junto con los datos de cada actividad (une las tablas
     * inscripciones y actividades).
     *
     * @param usuarioId identificador del usuario
     * @param estado    estado que se filtra (Inscrito o Completada)
     * @return cursor con las inscripciones; quien lo recibe debe cerrarlo
     */
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

    /**
     * Elimina una inscripción y devuelve el cupo a la actividad, dentro de una transacción.
     *
     * @param inscripcionId identificador de la inscripción que se elimina
     * @param actividadId   identificador de la actividad a la que se devuelve el cupo
     */
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

    /**
     * Marca una inscripción como completada (actualiza su estado).
     *
     * @param inscripcionId identificador de la inscripción
     */
    public void completarInscripcion(long inscripcionId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(InscripcionEntry.COLUMN_ESTADO, ESTADO_COMPLETADA);
        db.update(InscripcionEntry.TABLE_NAME, valores,
                InscripcionEntry._ID + " = ?",
                new String[]{String.valueOf(inscripcionId)});
    }

    /**
     * Suma las horas de las actividades completadas por un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return total de horas de voluntariado (0 si no tiene ninguna)
     */
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

    /**
     * Convierte el resultado de {@link #obtenerInscripciones(long, String)} en una lista de objetos
     * {@link Inscripcion}.
     *
     * @param usuarioId identificador del usuario
     * @param estado    estado que se filtra (Inscrito o Completada)
     * @return lista de inscripciones (vacía si no hay ninguna)
     * @throws IllegalArgumentException si falta una columna esperada en el resultado (error de esquema)
     */
    public ArrayList<Inscripcion> obtenerListaInscripciones(long usuarioId, String estado) {
        ArrayList<Inscripcion> lista = new ArrayList<>();
        try (Cursor cursor = obtenerInscripciones(usuarioId, estado)) {
            while (cursor.moveToNext()) {
                long inscripcionId = cursor.getLong(cursor.getColumnIndexOrThrow(InscripcionEntry.ALIAS_INSCRIPCION_ID));
                long actividadId = cursor.getLong(cursor.getColumnIndexOrThrow(InscripcionEntry.ALIAS_ACTIVIDAD_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_NOMBRE));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
                String lugar = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_LUGAR));
                int horas = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_HORAS));
                lista.add(new Inscripcion(inscripcionId, actividadId, nombre, fecha, lugar, horas));
            }
        }
        return lista;
    }

    /**
     * Calcula la fecha que corresponde a cierta cantidad de días a partir de hoy.
     *
     * @param dias días que se suman a la fecha actual
     * @return fecha con formato dd/MM/yyyy
     */
    private String fechaEnDias(int dias) {
        Calendar calendario = Calendar.getInstance();
        calendario.add(Calendar.DAY_OF_YEAR, dias);
        return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(calendario.getTime());
    }
}