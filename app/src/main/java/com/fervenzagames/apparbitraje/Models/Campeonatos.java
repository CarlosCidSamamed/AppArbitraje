package com.fervenzagames.apparbitraje.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Campeonatos {

    public String idCamp;
    public String nombre;
    public String fecha;
    public String lugar;
    public String tipo;

    public List<String> listaArbitros; // Lista de los árbitros asignados al Campeonato en cuestión.

    public List<Modalidades> listaModalidades; // Firebase Database --> Rama Camp_Mod almacena las modalidades para un campeonato dado.

    public int numZonasCombate; // Número de zonas de combate para cada uno de los campeonatos.

    private List<ZonasCombate> listaZonasCombate;


    public Campeonatos() {
    }

    public Campeonatos(String idCamp, String nombre, String fecha, String lugar, String tipo, List<String> listaArbitros, List<Modalidades> listaModalidades, int numZonasCombate) {
        this.idCamp = idCamp;
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
        this.listaArbitros = listaArbitros;
        this.listaModalidades = listaModalidades;
        this.numZonasCombate = numZonasCombate;
        this.listaZonasCombate = new ArrayList<>();
    }

    public String getIdCamp() {
        return idCamp;
    }

    public void setIdCamp(String idCamp) {
        this.idCamp = idCamp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getListaArbitros() {
        return listaArbitros;
    }

    public void setListaArbitros(List<String> listaArbitros) {
       this.listaArbitros = listaArbitros;
    }

    public void addToListaArbitros(String idArbi){
        if(this.listaArbitros == null){
            List<String> nuevaLista = new ArrayList<>();
            this.listaArbitros = nuevaLista;
        }
        this.listaArbitros.add(idArbi);
    }

    public void removeFromListaArbitros(int posicion){
        if(this.listaArbitros == null){
            return;
        } else {
            this.listaArbitros.remove(posicion);
        }
    }

    public List<Modalidades> getListaModalidades() {
        return listaModalidades;
    }

    public void setListaModalidades(List<Modalidades> listaModalidades) {
        this.listaModalidades = listaModalidades;
    }

    public int getNumZonasCombate() {
        return numZonasCombate;
    }

    public void setNumZonasCombate(int numZonasCombate) {
        this.numZonasCombate = numZonasCombate;
    }

    public List<ZonasCombate> getListaZonasCombate() {
        return listaZonasCombate;
    }

    public void setListaZonasCombate(List<ZonasCombate> listaZonasCombate) {
        this.listaZonasCombate = listaZonasCombate;
    }

    public void addToListaZonasCombate(ZonasCombate zona){
        if(this.listaZonasCombate == null){
            List<ZonasCombate> nuevaLista = new ArrayList<>();
            nuevaLista.add(zona);
            this.listaZonasCombate = nuevaLista;
        } else {
            this.listaZonasCombate.add(zona);
        }
    }

    public void removeFromListaZonasCombate(int posicion){
        if(this.listaZonasCombate == null){
            return;
        } else {
            this.listaZonasCombate.remove(posicion);
        }
    }

    public HashMap<String, List<String>> getHashMapZonasCombate(Campeonatos camp){

        HashMap<String, List<String>> hashMapZonas = new HashMap<>();

        // Recuperar el número de las zonas de combate
        List<String> zonas = new ArrayList<>();
        for(int i = 0; i < camp.getNumZonasCombate(); i++){
            String numero = String.valueOf(camp.getListaZonasCombate().get(i).getNumZona());
            zonas.add(numero);
        }

        hashMapZonas.put(camp.getNombre(), zonas);

        return hashMapZonas;
    }

    public Map<String, Object> toMap(){

        HashMap<String, Object> result = new HashMap<>();
        result.put("idCamp", idCamp);
        result.put("nombre", nombre);
        result.put("fecha", fecha);
        result.put("lugar",lugar);
        result.put("tipo", tipo);
        result.put("listaArbitros", listaArbitros);
        result.put("listaModalidades", listaModalidades);
        result.put("numZonasCombate" ,numZonasCombate);
        result.put("listaZonasCombate", listaZonasCombate);

        return result;
    }
}
