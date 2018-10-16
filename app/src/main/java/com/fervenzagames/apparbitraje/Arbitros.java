package com.fervenzagames.apparbitraje;

import java.util.List;

public class Arbitros {

    private String nombre;
    private String dni;
    private String email;
    private String password;

    private String foto; // URL de la foto

    private String nivel;
    private String cargo;

    private int zonaCombate;
    /* En el caso de que en un campeonato haya distintas zonas de combate o competición debermos identificar la zona
     * a la que pertenece tanto el árbitro de Mesa como los de Silla. */

    public List<Combates> listaCombates;

    public Arbitros(String nombre, String dni, String email, String password, String foto, String nivel, String cargo) {
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.foto = foto;
        this.nivel = nivel;
        this.cargo = cargo;
    }

    public Arbitros() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getZonaCombate() {
        return zonaCombate;
    }

    public void setZonaCombate(int zonaCombate) {
        this.zonaCombate = zonaCombate;
    }

    public List<Combates> getListaCombates() {
        return listaCombates;
    }

    public void setListaCombates(List<Combates> listaCombates) {
        this.listaCombates = listaCombates;
    }
}

