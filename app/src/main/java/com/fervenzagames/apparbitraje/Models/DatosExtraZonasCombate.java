package com.fervenzagames.apparbitraje.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int numArbisConfirmados;

    public DatosExtraZonasCombate(String idCombate, String numCombate, int numArbisAsignados, List<String> listaIDsArbis, int numArbisConfirmados) {
        this.idCombate = idCombate;
        this.numCombate = numCombate;
        this.numArbisAsignados = numArbisAsignados;
        this.listaIDsArbis = listaIDsArbis;
        this.numArbisConfirmados = numArbisConfirmados;
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

    public void addToListaArbis(String idArbitro){
        if(this.listaIDsArbis == null){
            List<String> nuevaLista = new ArrayList<>();
            nuevaLista.add(idArbitro);
            this.listaIDsArbis = nuevaLista;
        } else {
            this.listaIDsArbis.add(idArbitro);
        }
    }

    public void removeFromListaArbis(int posicion){
        if(this.listaIDsArbis == null){
            return;
        } else {
            this.listaIDsArbis.remove(posicion);
        }
    }

    public int getNumArbisConfirmados() {
        return numArbisConfirmados;
    }

    public void setNumArbisConfirmados(int numArbisConfirmados) {
        this.numArbisConfirmados = numArbisConfirmados;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("idCombate", idCombate);
        result.put("numCombate", numCombate);
        result.put("numArbisAsignados", numArbisAsignados);
        result.put("listaIDsArbis", listaIDsArbis);
        result.put("numArbisConfirmados", numArbisConfirmados);
        return  result;
    }
}


