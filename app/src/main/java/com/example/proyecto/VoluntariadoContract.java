package com.example.proyecto;

import android.provider.BaseColumns;

public final class VoluntariadoContract {

    private VoluntariadoContract() {
    }

    public static class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_CORREO = "correo";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_CARRERA = "carrera";
    }

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

    public static class InscripcionEntry implements BaseColumns {
        public static final String TABLE_NAME = "inscripciones";
        public static final String COLUMN_USUARIO_ID = "usuario_id";
        public static final String COLUMN_ACTIVIDAD_ID = "actividad_id";
        public static final String COLUMN_ESTADO = "estado";
    }
}
