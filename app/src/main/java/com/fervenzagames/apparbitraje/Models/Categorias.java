package com.fervenzagames.apparbitraje.Models;

import java.util.List;

public class Categorias {

    private String id;
    private String nombre;
    private String edad; // Categoría de Edad : SENIOR (SEN), ABSOLUTO (ABS), JUNIOR (JUN)
    private String sexo;
    private String peso;

    private List<Combates> listaCombates;

    public Categorias() {
    }

    public Categorias(String id, String nombre, String edad, String sexo, String peso, List<Combates> listaCombates) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
        this.peso = peso;
        this.listaCombates = listaCombates;
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

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public List<Combates> getListaCombates() {
        return listaCombates;
    }

    public void setListaCombates(List<Combates> listaCombates) {
        this.listaCombates = listaCombates;
    }
}
