package com.fervenzagames.apparbitraje;

public class Puntuaciones {

    private String id;
    private String dniJuez;
    private String idAsalto;
    private String idCompetidor;
    private int valor;
    private String concepto;
    private String zonaContacto;
    private String tipoAtaque;
    private String marcaTiempo;

    public Puntuaciones() {
    }

    public Puntuaciones(String id, String dniJuez, String idAsalto, String idCompetidor, int valor, String concepto, String zonaContacto, String tipoAtaque, String marcaTiempo) {
        this.id = id;
        this.dniJuez = dniJuez;
        this.idAsalto = idAsalto;
        this.idCompetidor = idCompetidor;
        this.valor = valor;
        this.concepto = concepto;
        this.zonaContacto = zonaContacto;
        this.tipoAtaque = tipoAtaque;
        this.marcaTiempo = marcaTiempo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDniJuez() {
        return dniJuez;
    }

    public void setDniJuez(String dniJuez) {
        this.dniJuez = dniJuez;
    }

    public String getIdAsalto() {
        return idAsalto;
    }

    public void setIdAsalto(String idAsalto) {
        this.idAsalto = idAsalto;
    }

    public String getIdCompetidor() {
        return idCompetidor;
    }

    public void setIdCompetidor(String idCompetidor) {
        this.idCompetidor = idCompetidor;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getZonaContacto() {
        return zonaContacto;
    }

    public void setZonaContacto(String zonaContacto) {
        this.zonaContacto = zonaContacto;
    }

    public String getTipoAtaque() {
        return tipoAtaque;
    }

    public void setTipoAtaque(String tipoAtaque) {
        this.tipoAtaque = tipoAtaque;
    }

    public String getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(String marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }
}
