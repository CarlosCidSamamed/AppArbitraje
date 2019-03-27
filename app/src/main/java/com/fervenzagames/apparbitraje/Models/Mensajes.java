package com.fervenzagames.apparbitraje.Models;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Mensajes {

    public static final String TITULO_LOGIN = "Combate Pendiente. Inicie Sesión en la app.";
    public static final String CUERPO_LOGIN = "¿Desea acceder a la pantalla de Inicio de Sesión de la app?";
    public static final String TIPO_LOGIN = "login";

    public static final String TITULO_CONF = "El Juez de Mesa espera su confirmación para comenzar el Asalto";
    public static final String CUERPO_CONF = "¿Desea confirmar su disponibilidad para comenzar a arbitrar?";
    public static final String TIPO_CONF = "confirmacion";

    public static final String TITULO_ASALTO = "Inicio del Asalto";
    public static final String CUERPO_ASALTO = "¿Desea cargar la interfaz para comenzar con el Arbitraje?";
    public static final String TIPO_ASALTO = "inicioAsalto";

    // Datos comunes a todos los tipos de mensajes
    private String idMensaje;
    private String emisor;
    private String receptor;
    private String fechaHora;
    private boolean leido;

    private String titulo;
    private String cuerpo;
    private String tipo;

    // Datos extra para el mensaje de Inicio de Sesión
    private String idCamp;
    private String idZona;
    private String idCat;
    private String idCombateActual;
    private String idAsaltoActual;
    private String idRojo;
    private String idAzul;

    public Mensajes(String idMensaje, String emisor, String receptor, String fechaHora, String titulo, String cuerpo, String tipo, String idCamp, String idZona, String idCat, String idCombateActual, String idAsaltoActual, String idRojo, String idAzul) {
        this.idMensaje = idMensaje;
        this.emisor = emisor;
        this.receptor = receptor;
        this.fechaHora = fechaHora;
        this.leido = false;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.tipo = tipo;
        this.idCamp = idCamp;
        this.idZona = idZona;
        this.idCat = idCat;
        this.idCombateActual = idCombateActual;
        this.idAsaltoActual = idAsaltoActual;
        this.idRojo = idRojo;
        this.idAzul = idAzul;
    }

    public Mensajes() {
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdCamp() {
        return idCamp;
    }

    public void setIdCamp(String idCamp) {
        this.idCamp = idCamp;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getIdCat() {
        return idCat;
    }

    public void setIdCat(String idCat) {
        this.idCat = idCat;
    }

    public String getIdCombateActual() {
        return idCombateActual;
    }

    public void setIdCombateActual(String idCombateActual) {
        this.idCombateActual = idCombateActual;
    }

    public String getIdAsaltoActual() {
        return idAsaltoActual;
    }

    public void setIdAsaltoActual(String idAsaltoActual) {
        this.idAsaltoActual = idAsaltoActual;
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

    // Métodos para serializar y deserializar objetos de la clase Mensaje usando GSON
    public String serializeToJson(Mensajes mensaje){
        Gson gson = new Gson();
        Type type = new TypeToken<Mensajes>(){}.getType();
        String j = gson.toJson(mensaje, type);
        Log.w("Mensaje serializado", j);
        return j;
    }

    public Mensajes deserializeFromJson(String jsonString){
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Mensajes.class);
    }
}
