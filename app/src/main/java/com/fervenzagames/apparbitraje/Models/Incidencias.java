package com.fervenzagames.apparbitraje.Models;

public class Incidencias {

    private String id;
    private String dniJuez;
    private String idComp;
    private int valor;
    private String tipo;
    private String descripcion;
    private String marcaTiempo;

    public Incidencias() {
    }

    public Incidencias(String id, String dniJuez, String idComp, int valor, String tipo, String descripcion, String marcaTiempo) {
        this.id = id;
        this.dniJuez = dniJuez;
        this.idComp = idComp;
        this.valor = valor;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.marcaTiempo = marcaTiempo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDniJuez() {
        return dniJuez;
    }

    public void setDniJuez(String dniJuez) {
        this.dniJuez = dniJuez;
    }

    public String getIdComp() {
        return idComp;
    }

    public void setIdComp(String idComp) {
        this.idComp = idComp;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
