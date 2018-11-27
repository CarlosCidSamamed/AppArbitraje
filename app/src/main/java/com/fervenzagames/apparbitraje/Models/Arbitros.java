package com.fervenzagames.apparbitraje.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arbitros {

    private String idArbitro;
    private String nombre;
    private String dni;
    private String email;
    private String password;

    private String foto; // URL de la foto

    private String nivel;
    private String cargo;

    private int zonaCombate;
    /* En el caso de que en un campeonato haya distintas zonas de combate o competición deberemos identificar la zona
     * a la que pertenece tanto el árbitro de Mesa como los de Silla. */
    private String idCamp; // ID del Campeonato al que están asignados. Al añadir a un Árbitro a la lista correspondiente en un campeonato
                           // se actualizará este campo para que indique el campeonato a cuya lista se ha añadido.

    private List<String> listaCamps; // Lista de Campeonatos en los que ha arbitrado este árbitro. Almacena los IDs de los Campeonatos en orden cronológico,
                                     // es decir, el más reciente será el último de la lista.
                                     // Al asignar a este árbitro a un nuevo Campeonato deberemos actualizar el idCamp para que muestre el ID de ese campeonato, así como añadir dicho ID a la lista.

    // Usando los datos del idCamp y la zonaCombate se podrá identificar de manera clar si un árbitro está asignado a un campeonato y, en caso afirmativo,
    // determinar la zona de combate que le corresponde. A partir de la zona de combate se le podrá asignar la lista de combates correspondientes a dicha zona de combate.

    private boolean conectado; // Especifica si el árbitro está conectado mediante su usuario a la aplicación (Firebase Auth)

    public List<Combates> listaCombates;

    public Arbitros(String idArbitro, String nombre, String dni, String email, String password, String foto, String nivel, String cargo, int zonaCombate, String idCamp, boolean conectado, List<Combates> listaCombates) {
        this.idArbitro = idArbitro;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.foto = foto;
        this.nivel = nivel;
        this.cargo = cargo;
        this.zonaCombate = zonaCombate;
        this.idCamp = idCamp;

        // Añadir el ID del Campeonato a la lista de Campeonatos que en este caso está vacía al crear el registro del Árbitro.
        this.listaCamps.add(this.idCamp);

        this.conectado = conectado;
        this.listaCombates = listaCombates;
    }

    public Arbitros() {
    }

    public String getIdArbitro() {
        return idArbitro;
    }

    public void setIdArbitro(String idArbitro) {
        this.idArbitro = idArbitro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getZonaCombate() {
        return zonaCombate;
    }

    public void setZonaCombate(int zonaCombate) {
        this.zonaCombate = zonaCombate;
    }

    public String getIdCamp() {
        return idCamp;
    }

    public void setIdCamp(String idCamp) {
        this.idCamp = idCamp;
    }

    public List<String> getListaCamps() { return listaCamps; }

    public void setListaCamps(List<String> listaCamps) { this.listaCamps = listaCamps; }

    public List<Combates> getListaCombates() {
        return listaCombates;
    }

    public void setListaCombates(List<Combates> listaCombates) {
        this.listaCombates = listaCombates;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public Map<String, Object> toMap(){

        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("dni", dni);
        result.put("email", email);
        result.put("password", password);
        result.put("foto", foto);
        result.put("nivel", nivel);
        result.put("cargo", cargo);
        result.put("zonaCombate", zonaCombate);
        result.put("listaCombates", listaCombates);
        result.put("conectado", conectado);

        return result;
    }
}

