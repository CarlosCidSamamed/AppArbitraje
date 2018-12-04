package com.fervenzagames.apparbitraje.Models;

import java.util.ArrayList;
import java.util.List;

public class DatosExtraZonasCombate {
    // La estructura de los datos a almacenar en la Lista de Combates de la clase ZonasCombate
    // Contiene los siguientes datos para cada Combate
    // idCombate
    // numArbisAsignados --> Número actual de árbitros asignados a ese combate. Para comprobar si ya se han asignado suficientes árbitros a un combate.
    // listaIDsArbis --> Lista con los IDs de los árbitros asignados en un momento dado al combate en cuestión.

    private String idCombate;
    private String numCombate;
    private int numArbisAsignados;
    private List<String> listaIDsArbis;

    public DatosExtraZonasCombate(String idCombate, String numCombate, int numArbisAsignados, List<String> listaIDsArbis) {
        this.idCombate = idCombate;
        this.numCombate = numCombate;
        this.numArbisAsignados = numArbisAsignados;
        this.listaIDsArbis = listaIDsArbis;
    }

    public DatosExtraZonasCombate() {
    }

    public String getIdCombate() {
        return idCombate;
    }

    public void setIdCombate(String idCombate) {
        this.idCombate = idCombate;
    }

    public String getNumCombate() {
        return numCombate;
    }

    public void setNumCombate(String numCombate) {
        this.numCombate = numCombate;
    }

    public int getNumArbisAsignados() {
        return numArbisAsignados;
    }

    public void setNumArbisAsignados(int numArbisAsignados) {
        this.numArbisAsignados = numArbisAsignados;
    }

    public List<String> getListaIDsArbis() {
        return listaIDsArbis;
    }

    public void setListaIDsArbis(List<String> listaIDsArbis) {
        this.listaIDsArbis = listaIDsArbis;
    }

    public void addToListaArbis(String id){
        if(this.listaIDsArbis == null){
            List<String> nuevaLista = new ArrayList<>();
            nuevaLista.add(id);
            this.listaIDsArbis = nuevaLista;
        } else {
            this.listaIDsArbis.add(id);
        }
    }

    public void removeFromListaArbis(int posicion){
        if(this.listaIDsArbis == null){
            return;
        } else {
            this.listaIDsArbis.remove(posicion);
        }
    }
}
