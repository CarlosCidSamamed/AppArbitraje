package com.fervenzagames.apparbitraje.Models;


public class Emparejamientos{
    private String id;
    private String numeroCombate;
    private String idRojo;
    private String idAzul;
    private String sigCombateGanador;
    private String sigCombatePerdedor;
    public enum EsFinal {SI, NO, TERCEROS};
    private EsFinal esFinal;
    private String idGanador;
    private String idPerdedor;

    public Emparejamientos() {
    }

    public Emparejamientos(String id, String numeroCombate, String idRojo, String idAzul, String sigCombateGanador, String sigCombatePerdedor, EsFinal esFinal, String idGanador, String idPerdedor) {
        this.id = id;
        this.numeroCombate = numeroCombate;
        this.idRojo = idRojo;
        this.idAzul = idAzul;
        this.sigCombateGanador = sigCombateGanador;
        this.sigCombatePerdedor = sigCombatePerdedor;
        this.esFinal = esFinal;
        this.idGanador = idGanador;
        this.idPerdedor = idPerdedor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroCombate() {
        return numeroCombate;
    }

    public void setNumeroCombate(String numeroCombate) {
        this.numeroCombate = numeroCombate;
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

    public String getSigCombateGanador() {
        return sigCombateGanador;
    }

    public void setSigCombateGanador(String sigCombateGanador) {
        this.sigCombateGanador = sigCombateGanador;
    }

    public String getSigCombatePerdedor() {
        return sigCombatePerdedor;
    }

    public void setSigCombatePerdedor(String sigCombatePerdedor) {
        this.sigCombatePerdedor = sigCombatePerdedor;
    }

    public EsFinal getEsFinal() {
        return esFinal;
    }

    public void setEsFinal(EsFinal esFinal) {
        this.esFinal = esFinal;
    }

    public String getIdGanador() {
        return idGanador;
    }

    public void setIdGanador(String idGanador) {
        this.idGanador = idGanador;
    }

    public String getIdPerdedor() {
        return idPerdedor;
    }

    public void setIdPerdedor(String idPerdedor) {
        this.idPerdedor = idPerdedor;
    }
}
