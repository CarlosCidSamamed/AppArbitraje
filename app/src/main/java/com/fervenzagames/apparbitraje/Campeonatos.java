package com.fervenzagames.apparbitraje;

public class Campeonatos {

    public Long id;
    public String nombre;
    public String fecha;
    public String lugar;
    public String tipo;

    public Campeonatos() {
    }

    public Campeonatos(String id, String nombre, String fecha, String lugar, String tipo) {
        this.id = Long.parseLong(id);
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
