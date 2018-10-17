package com.fervenzagames.apparbitraje.Models;

import java.util.List;

public class Modalidades {

    private String id;
    private String nombre;
    private String descripcion;
    private List<Categorias> listaCategorias;

    public Modalidades() {
    }

    public Modalidades(String id, String nombre, String descripcion, List<Categorias> listaCategorias) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.listaCategorias = listaCategorias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Categorias> getListaCategorias() {
        return listaCategorias;
    }

    public void setListaCategorias(List<Categorias> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }
}
