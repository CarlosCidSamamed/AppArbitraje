package com.fervenzagames.apparbitraje.Models;

import java.util.List;

public class Campeonatos {

    public Long id;
    public String nombre;
    public String fecha;
    public String lugar;
    public String tipo;

    // public List<Arbitros> listaArbitros; // Lista de los árbitros asignados al Campeonato en cuestión.
    // Esta lista se usará para limitar el acceso y login a los árbitros que estén registrados en la app (o se registren como nuevos usuarios)
    // y no estén en lal lista de árbitros asignados al campeonato. En el caso de que no estén en dicha lista e inicien sesión
    // se les mostrará un mensaje en pantalla informándoles de que no podrán acceder a los datos de la BD al no estar en la lista de
    // usuarios con los permisos necesarios.

    public List<Modalidades> listaModalidades; // Firebase Database --> Rama Camp_Mod almacena las modalidades para un campeonato dado.

    public Campeonatos() {
    }

    public Campeonatos(String id, String nombre, String fecha, String lugar, String tipo/*, List<Arbitros> listaArbitros*/) {
        this.id = Long.parseLong(id);
        this.nombre = nombre;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipo = tipo;
  //      this.listaArbitros = listaArbitros;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

//    public List<Arbitros> getListaArbitros() {
//        return listaArbitros;
//    }
//
//    public void setListaArbitros(List<Arbitros> listaArbitros) {
//        this.listaArbitros = listaArbitros;
//    }
}
