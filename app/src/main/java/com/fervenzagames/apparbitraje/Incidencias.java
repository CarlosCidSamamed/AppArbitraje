package com.fervenzagames.apparbitraje;

public class Incidencias {

    private String id;
    private int valor;
    private String Tipo;
    private String descripcion;
    private String marcaTiempo;

    public Incidencias() {
    }

    public Incidencias(String id, int valor, String tipo, String descripcion, String marcaTiempo) {
        this.id = id;
        this.valor = valor;
        Tipo = tipo;
        this.descripcion = descripcion;
        this.marcaTiempo = marcaTiempo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(String marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }
}
