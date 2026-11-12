package com.example.proyecto.data;

import android.provider.BaseColumns;

/**
 * Clase contrato de la base de datos de VoluntariST.
 * <p>
 * Reúne en un solo lugar los nombres de la base, de las tablas y de las columnas, siguiendo la guía
 * de Android para SQLite. Si un nombre cambia, se modifica únicamente aquí.
 */
public final class VoluntariadoContract {

    /** Nombre del archivo de la base de datos local. */
    public static final String DATABASE_NAME = "voluntariado.db";

    /** Versión del esquema; al subirla, el helper ejecuta onUpgrade. */
    public static final int DATABASE_VERSION = 4;

    /** Evita crear instancias: la clase solo contiene constantes. */
    private VoluntariadoContract() {
    }

    /** Tabla usuarios: nombre, correo único, contraseña protegida con hash y carrera. */
    public static class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_CORREO = "correo";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_CARRERA = "carrera";
    }

    /** Tabla actividades: datos de cada actividad, sus cupos y las coordenadas para el mapa. */
    public static class ActividadEntry implements BaseColumns {
        public static final String TABLE_NAME = "actividades";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_LUGAR = "lugar";
        public static final String COLUMN_DESCRIPCION = "descripcion";
        public static final String COLUMN_HORAS = "horas";
        public static final String COLUMN_CUPOS = "cupos";
        public static final String COLUMN_LATITUD = "latitud";
        public static final String COLUMN_LONGITUD = "longitud";
    }

    /** Tabla inscripciones: relaciona a un usuario con una actividad y guarda el estado de la inscripción. */
    public static class InscripcionEntry implements BaseColumns {

        public static final String TABLE_NAME = "inscripciones";
        public static final String COLUMN_USUARIO_ID = "usuario_id";
        public static final String COLUMN_ACTIVIDAD_ID = "actividad_id";
        public static final String COLUMN_ESTADO = "estado";

        /** Estado de una inscripción cuya actividad ya fue realizada. */
        public static final String ESTADO_INSCRITO = "Inscrito";

        /** Estado de una inscripción cuya actividad ya fue realizada. */
        public static final String ESTADO_COMPLETADA = "Completada";

        /** Alias del id de la inscripción en la consulta que une inscripciones y actividades. */
        public static final String ALIAS_INSCRIPCION_ID = "inscripcion_id";

        /** Alias del id de la actividad en la consulta que une inscripciones y actividades. */
        public static final String ALIAS_ACTIVIDAD_ID = "actividad_id";
    }
}