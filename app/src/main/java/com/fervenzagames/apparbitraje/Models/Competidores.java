package com.fervenzagames.apparbitraje.Models;

public class Competidores {

    private String id;
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String fechaNac;
    private String sexo;
    private int edad;
    private float peso;
    private String catEdad;
    private String catPeso;
    private float altura;
    private float envergadura;
    private String guardia;
    private int combatesGanados;
    private int combatesPerdidos;
    private String federacion;
    private String escuelaClub;
    private String pais;
    private String foto; // URL de la foto en el Firebase Storage

    public Competidores() {
    }

    public Competidores(String id, String dni, String nombre, String apellido1, String apellido2,
                        String fechaNac, String sexo, int edad, float peso, String catEdad, String catPeso,
                        float altura, float envergadura, String guardia, int combatesGanados, int combatesPerdidos,
                        String federacion, String escuelaClub, String pais, String foto) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.fechaNac = fechaNac;
        this.sexo = sexo;
        this.edad = edad;
        this.peso = peso;
        this.catEdad = catEdad;
        this.catPeso = catPeso;
        this.altura = altura;
        this.envergadura = envergadura;
        this.guardia = guardia;
        this.combatesGanados = combatesGanados;
        this.combatesPerdidos = combatesPerdidos;
        this.federacion = federacion;
        this.escuelaClub = escuelaClub;
        this.pais = pais;
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getNombreCompleto(){
        String nombreCompleto = nombre + " " + apellido1 + " " + apellido2;
        return nombreCompleto;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getCatEdad() {
        return catEdad;
    }

    public void setCatEdad(String catEdad) {
        this.catEdad = catEdad;
    }

    public String getCatPeso() {
        return catPeso;
    }

    public void setCatPeso(String catPeso) {
        this.catPeso = catPeso;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public float getEnvergadura() {
        return envergadura;
    }

    public void setEnvergadura(float envergadura) {
        this.envergadura = envergadura;
    }

    public String getGuardia() {
        return guardia;
    }

    public void setGuardia(String guardia) {
        this.guardia = guardia;
    }

    public int getCombatesGanados() {
        return combatesGanados;
    }

    public void setCombatesGanados(int combatesGanados) {
        this.combatesGanados = combatesGanados;
    }

    public int getCombatesPerdidos() {
        return combatesPerdidos;
    }

    public void setCombatesPerdidos(int combatesPerdidos) {
        this.combatesPerdidos = combatesPerdidos;
    }

    public String getFederacion() {
        return federacion;
    }

    public void setFederacion(String federacion) {
        this.federacion = federacion;
    }

    public String getEscuelaClub() {
        return escuelaClub;
    }

    public void setEscuelaClub(String escuelaClub) {
        this.escuelaClub = escuelaClub;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
