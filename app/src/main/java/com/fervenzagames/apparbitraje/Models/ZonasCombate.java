package com.fervenzagames.apparbitraje.Models;

import java.util.ArrayList;
import java.util.List;

public class ZonasCombate {

    private String idZona; // ID para la BD
    private int numZona;   // Número de la Zona de Combate
    private String idCamp; // ID del Campeonato al que pertenece la Zona de Combate
    private List<DatosExtraZonasCombate> listaDatosExtraCombates; // Ver clase correspondiente.

    public ZonasCombate(String idZona, int numZona, String idCamp, List<DatosExtraZonasCombate> listaDatosExtraCombates) {
        this.idZona = idZona;
        this.numZona = numZona;
        this.idCamp = idCamp;
        this.listaDatosExtraCombates = listaDatosExtraCombates;
    }

    public ZonasCombate() {
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public int getNumZona() {
        return numZona;
    }

    public void setNumZona(int numZona) {
        this.numZona = numZona;
    }

    public String getIdCamp() {
        return idCamp;
    }

    public void setIdCamp(String idCamp) {
        this.idCamp = idCamp;
    }

    public List<DatosExtraZonasCombate> getListaDatosExtraCombates() {
        return listaDatosExtraCombates;
    }

    public void setListaDatosExtraCombates(List<DatosExtraZonasCombate> listaDatosExtraCombates) {
        this.listaDatosExtraCombates = listaDatosExtraCombates;
    }

    public void addToListaDatosExtraCombate(DatosExtraZonasCombate datos){
        if(this.listaDatosExtraCombates == null){
            List<DatosExtraZonasCombate> nuevaLista = new ArrayList<>();
            nuevaLista.add(datos);
            this.listaDatosExtraCombates = nuevaLista;
        } else {
            this.listaDatosExtraCombates.add(datos);
        }
    }

    public void removeFromListaDatosExtraCombates(int posicion){
        if(this.listaDatosExtraCombates == null){
            return;
        } else {
            listaDatosExtraCombates.remove(posicion);
        }
    }
}
