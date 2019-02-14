package com.fervenzagames.apparbitraje.Models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Asaltos {

    private String id;
    private int numAsalto;
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
    public enum EstadoAsalto {Pendiente, Finalizado, Cancelado};
    private EstadoAsalto estado;

    public Asaltos() {
    }

    public Asaltos(String id, int numAsalto, String ganador, String motivo,
                   String descripcion, int puntuacionRojo, int puntuacionAzul,
                   String duracion, List<Puntuaciones> listaPuntuaciones, List<Incidencias> listaIncidencias,
                   EstadoAsalto estado) {
        this.id = id;
        this.numAsalto = numAsalto;
        this.ganador = ganador;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.puntuacionRojo = puntuacionRojo;
        this.puntuacionAzul = puntuacionAzul;
        this.duracion = duracion;
        this.listaPuntuaciones = listaPuntuaciones;
        this.listaIncidencias = listaIncidencias;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumAsalto() {
        return numAsalto;
    }

    public void setNumAsalto(int numAsalto) {
        this.numAsalto = numAsalto;
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

    public EstadoAsalto getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsalto estado) {
        this.estado = estado;
    }

    public String estadoToString(EstadoAsalto estado){

        String res = "";

        switch(estado){
            case Pendiente:
            {
                res = "Pendiente";
                break;
            }
            case Finalizado:
            {
                res = "Finalizado";
                break;
            }
            case Cancelado:
            {
                res = "Cancelado";
                break;
            }
        }

        return res;
    }

    // Para actualizar un Asalto
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("numAsalto", numAsalto);
        result.put("ganador", ganador);
        result.put("motivo", motivo);
        result.put("descripcion", descripcion);
        result.put("puntuacionRojo", puntuacionRojo);
        result.put("puntuacionAzul", puntuacionAzul);
        result.put("duracion", duracion);
        result.put("listaPuntuaciones", listaPuntuaciones);
        result.put("listaIncidencias", listaIncidencias);
        result.put("estado", estado);

        return result;
    }
}
