package com.fervenzagames.apparbitraje.Models;

public class MensajesCrono {

    public static final String TITULO_CRONO = "Mensaje Crono";

    public static final String CUERPO_INICIO = "Iniciando el CRONO";
    public static final String CUERPO_PAUSA = "Pausando el CRONO";
    public static final String CUERPO_REANUDAR = "Reanudando el CRONO";
    public static final String CUERPO_REINICIAR = "Reiniciando el CRONO";
    public static final String CUERPO_FIN_ASALTO = "Fin ASALTO -- Deteniendo el CRONO";
    public static final String CUERPO_FIN_COMBATE = "Fin COMBATE -- Deteniendo el CRONO";

    private String idMensaje;
    private String emisor;
    private String receptor;
    private String fechaHora;
    private boolean leido;

    private String titulo;
    private String cuerpo;
    private String tipo;

    private long tiempoAsalto; // Tiempo del Asalto en ms
    private boolean finAsalto;
    private boolean finCombate;

    public MensajesCrono(String idMensaje, String emisor, String receptor, String fechaHora,
                         boolean leido, String titulo, String cuerpo, String tipo,
                         long tiempoAsalto, boolean finAsalto, boolean finCombate) {
        this.idMensaje = idMensaje;
        this.emisor = emisor;
        this.receptor = receptor;
        this.fechaHora = fechaHora;
        this.leido = leido;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.tipo = tipo;
        this.tiempoAsalto = tiempoAsalto;
        this.finAsalto = finAsalto;
        this.finCombate = finCombate;
    }

    public MensajesCrono() {
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

    public long getTiempoAsalto() {
        return tiempoAsalto;
    }

    public void setTiempoAsalto(long tiempoAsalto) {
        this.tiempoAsalto = tiempoAsalto;
    }

    public boolean isFinAsalto() {
        return finAsalto;
    }

    public void setFinAsalto(boolean finAsalto) {
        this.finAsalto = finAsalto;
    }

    public boolean isFinCombate() {
        return finCombate;
    }

    public void setFinCombate(boolean finCombate) {
        this.finCombate = finCombate;
    }
}
