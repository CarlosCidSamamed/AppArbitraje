package com.fervenzagames.apparbitraje.Models;

import com.fervenzagames.apparbitraje.Models.Asaltos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Combates {

    private String id;
    private String numCombate; // Número de ese combate dentro de un Campeonato.
    private String ganador;
    private String perdedor;
    private String motivo;
    private String enlaceVideo;
    private String idRojo;
    private String idAzul;
    private List<Asaltos> listaAsaltos;
    private String modalidad;
    private String categoria;
    private String campeonato;
    private String idZonaCombate;
    public enum EstadoCombate {Pendiente, Finalizado, Cancelado};
    private EstadoCombate estadoCombate;

    public Combates() {
    }

    public Combates(String id, String numCombate, String ganador, String perdedor,String motivo, String enlaceVideo,
                    String idRojo, String idAzul, List<Asaltos> listaAsaltos, String campeonato, String idZonaCombate,
                    String modalidad, String categoria, EstadoCombate estadoCombate) {
        this.id = id;
        this.numCombate = numCombate;
        this.ganador = ganador;
        this.perdedor = perdedor;
        this.motivo = motivo;
        this.enlaceVideo = enlaceVideo;
        this.idRojo = idRojo;
        this.idAzul = idAzul;
        this.listaAsaltos = listaAsaltos;
        this.modalidad = modalidad;
        this.categoria = categoria;
        this.campeonato = campeonato;
        this.idZonaCombate = idZonaCombate;
        this.estadoCombate = estadoCombate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumCombate() {
        return numCombate;
    }

    public void setNumCombate(String numCombate) {
        this.numCombate = numCombate;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public String getPerdedor() { return perdedor; }

    public void setPerdedor(String perdedor) { this.perdedor = perdedor; }

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

    public String getIdZonaCombate() {
        return idZonaCombate;
    }

    public void setIdZonaCombate(String idZonaCombate) {
        this.idZonaCombate = idZonaCombate;
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
            default:
            {
                res = "Estado no especificado";
                break;
            }
        }

        return res;
    }

    public EstadoCombate estadoFromString(String cadena){
        EstadoCombate estado = EstadoCombate.Pendiente;

        switch(cadena){
            case "Pendiente":
            {
                break;
            }
            case "Finalizado":
            {
                estado = EstadoCombate.Finalizado;
                break;
            }
            case "Cancelado":
            {
                estado = EstadoCombate.Cancelado;
                break;
            }
        }

        return estado;
    }

    public HashMap<String , String> getHashMapDetalleCombate(Combates comb){

        HashMap<String, String> hashMapDetalle = new HashMap<>();

        List<String> estados = new ArrayList<>();

        hashMapDetalle.put(comb.getNumCombate(), comb.getEstadoCombate().toString());

        return hashMapDetalle;
    }

    // Para actualizar un Combate
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("numCombate", numCombate);
        result.put("ganador", ganador);
        result.put("perdedor", perdedor);
        result.put("motivo", motivo);
        result.put("enlaceVideo", enlaceVideo);
        result.put("idRojo", idRojo);
        result.put("idAzul", idAzul);
        result.put("listaAsaltos", listaAsaltos);
        result.put("modalidad", modalidad);
        result.put("categoria", categoria);
        result.put("campeonato",campeonato);
        result.put("idZonaCombate", idZonaCombate);
        result.put("estadoCombate", estadoCombate);

        return result;
    }
}