package com.fervenzagames.apparbitraje.Models;

import com.fervenzagames.apparbitraje.Models.Asaltos;

import java.util.List;

public class Combates {

    private String id;
    private int numCombate; // Número de ese combate dentro de un Campeonato.
    private String ganador;
    private String motivo;
    private String enlaceVideo;
    private String idRojo;
    private String idAzul;
    private List<Asaltos> listaAsaltos;
    private String modalidad;
    private String categoria;
    private String campeonato;
    public enum EstadoCombate {Pendiente, Finalizado, Cancelado};
    private EstadoCombate estadoCombate;

    public Combates() {
    }

    public Combates(String id, int numCombate, String ganador, String motivo, String enlaceVideo,
                    String idRojo, String idAzul, List<Asaltos> listaAsaltos, String campeonato,
                    String modalidad, String categoria, EstadoCombate estadoCombate) {
        this.id = id;
        this.numCombate = numCombate;
        this.ganador = ganador;
        this.motivo = motivo;
        this.enlaceVideo = enlaceVideo;
        this.idRojo = idRojo;
        this.idAzul = idAzul;
        this.listaAsaltos = listaAsaltos;
        this.modalidad = modalidad;
        this.categoria = categoria;
        this.campeonato = campeonato;
        this.estadoCombate = estadoCombate;
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

    public String getEnlaceVideo() {
        return enlaceVideo;
    }

    public void setEnlaceVideo(String enlaceVideo) {
        this.enlaceVideo = enlaceVideo;
    }

    public String getIdRojo() {
        return idRojo;
    }

    public void setIdRojo(String idRojo) {
        this.idRojo = idRojo;
    }

    public String getIdAzul() {
        return idAzul;
    }

    public void setIdAzul(String idAzul) {
        this.idAzul = idAzul;
    }

    public List<Asaltos> getListaAsaltos() {
        return listaAsaltos;
    }

    public void setListaAsaltos(List<Asaltos> listaAsaltos) {
        this.listaAsaltos = listaAsaltos;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(String campeonato) {
        this.campeonato = campeonato;
    }

    public EstadoCombate getEstadoCombate() { return estadoCombate; }

    public void setEstadoCombate(EstadoCombate estadoCombate) { this.estadoCombate = estadoCombate; }

    public String estadoToString (EstadoCombate estado){
        String res = "";

        switch (estado){
            case Pendiente:{
                res = "Pendiente";
                break;
            }
            case Finalizado:{
                res = "Finalizado";
                break;
            }
            case Cancelado:{
                res = "Cancelado";
                break;
            }
        }

        return res;
    }
}
