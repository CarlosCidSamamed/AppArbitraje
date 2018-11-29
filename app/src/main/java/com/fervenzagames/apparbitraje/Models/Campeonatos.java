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

    public List<Arbitros> listaArbitros; // Lista de los árbitros asignados al Campeonato en cuestión.
    // Esta lista se usará para limitar el acceso y login a los árbitros que estén registrados en la app (o se registren como nuevos usuarios)
    // y no estén en la lista de árbitros asignados al campeonato. En el caso de que no estén en dicha lista e inicien sesión
    // se les mostrará un mensaje en pantalla informándoles de que no podrán acceder a los datos de la BD al no estar en la lista de
    // usuarios con los permisos necesarios.

    public List<Modalidades> listaModalidades; // Firebase Database --> Rama Camp_Mod almacena las modalidades para un campeonato dado.

    public int numZonasCombate; // Número de zonas de combate para cada uno de los campeonatos.

    public Campeonatos() {
    }

    public Campeonatos(String idCamp, String nombre, String fecha, String lugar, String tipo, List<Arbitros> listaArbitros, List<Modalidades> listaModalidades, int numZonasCombate) {
        this.idCamp = idCamp;
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
        this.listaArbitros = listaArbitros;
        this.listaModalidades = listaModalidades;
        this.numZonasCombate = numZonasCombate;
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

    public List<Arbitros> getListaArbitros() {
        return listaArbitros;
    }

    public void setListaArbitros(List<Arbitros> listaArbitros) {
       this.listaArbitros = listaArbitros;
    }

    public void addToListaArbitros(Arbitros arbi){
        if(this.listaArbitros == null){
            List<Arbitros> nuevaLista = new ArrayList<>();
            this.listaArbitros = nuevaLista;
        }
        this.listaArbitros.add(arbi);
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

        return result;
    }
}
