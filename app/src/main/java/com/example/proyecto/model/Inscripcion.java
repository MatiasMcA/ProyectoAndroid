package com.example.proyecto.model;

public class Inscripcion {

    private final long inscripcionId;
    private final long actividadId;
    private final String nombre;
    private final String fecha;
    private final String lugar;
    private final int horas;

    public Inscripcion(long inscripcionId, long actividadId, String nombre,
                       String fecha, String lugar, int horas) {
        this.inscripcionId = inscripcionId;
        this.actividadId = actividadId;
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.horas = horas;
    }

    public long getInscripcionId() {
        return inscripcionId;
    }

    public long getActividadId() {
        return actividadId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public int getHoras() {
        return horas;
    }
}
