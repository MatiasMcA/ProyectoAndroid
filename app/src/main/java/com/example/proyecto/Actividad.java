package com.example.proyecto;

public class Actividad {

    private final long id;
    private final String nombre;
    private final String fecha;
    private final String lugar;
    private final int cupos;

    public Actividad(long id, String nombre, String fecha, String lugar, int cupos) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.cupos = cupos;
    }

    public long getId() {
        return id;
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

    public int getCupos() {
        return cupos;
    }
}
