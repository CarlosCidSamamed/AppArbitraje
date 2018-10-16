package com.fervenzagames.apparbitraje;

import java.util.List;

import javax.security.auth.Destroyable;

public class Asaltos {

    private String id;
    private int numCombate;
    private String ganador;
    private String motivo;
    private String descripcion;
    /* Del motivo de la victoria
    * Ejemplo:
    *   Motivo : DiferenciaPuntos
    *   Descripcion : Diferencia de Puntos entre ambos Competidores.*/
    private int puntuacionRojo;
    private int puntuacionAzul;
    private String duracion;
    private List<Puntuaciones> listaPuntuaciones;
    private List<Incidencias> listaIncidencias;

    public Asaltos() {
    }

    public Asaltos(String id, int numCombate, String ganador, String motivo, String descripcion, int puntuacionRojo, int puntuacionAzul, String duracion, List<Puntuaciones> listaPuntuaciones, List<Incidencias> listaIncidencias) {
        this.id = id;
        this.numCombate = numCombate;
        this.ganador = ganador;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.puntuacionRojo = puntuacionRojo;
        this.puntuacionAzul = puntuacionAzul;
        this.duracion = duracion;
        this.listaPuntuaciones = listaPuntuaciones;
        this.listaIncidencias = listaIncidencias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumCombate() {
        return numCombate;
    }

    public void setNumCombate(int numCombate) {
        this.numCombate = numCombate;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntuacionRojo() {
        return puntuacionRojo;
    }

    public void setPuntuacionRojo(int puntuacionRojo) {
        this.puntuacionRojo = puntuacionRojo;
    }

    public int getPuntuacionAzul() {
        return puntuacionAzul;
    }

    public void setPuntuacionAzul(int puntuacionAzul) {
        this.puntuacionAzul = puntuacionAzul;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public List<Puntuaciones> getListaPuntuaciones() {
        return listaPuntuaciones;
    }

    public void setListaPuntuaciones(List<Puntuaciones> listaPuntuaciones) {
        this.listaPuntuaciones = listaPuntuaciones;
    }

    public List<Incidencias> getListaIncidencias() {
        return listaIncidencias;
    }

    public void setListaIncidencias(List<Incidencias> listaIncidencias) {
        this.listaIncidencias = listaIncidencias;
    }
}
