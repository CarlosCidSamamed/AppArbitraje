package com.fervenzagames.apparbitraje.Models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arbitros {

    private String idArbitro;
    private String nombre;
    private String dni;
    private String email;
    private String password;

    private String tokenIDFCM;

    private String foto; // URL de la foto
    private String pais;
    private String federacion;

    private String nivel;
    private String cargo;

    private String idZona;
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
    private boolean listo; // Especifica si el árbitro está listo para comenzar a arbitrar.

    public List<Combates> listaCombates;

    private String idCombate; // ID del Combate actual de un Árbitro. Este ID se actulizará cuando el árbitro confirme su disponibilidad para arbitrar un combate.
    // El idCombate se enviará con el mensaje de confirmación
    private String idCat;

    public Arbitros(String idArbitro, String nombre, String dni,
                    String email, String password, String tokenIDFCM,
                    String foto, String pais, String federacion,
                    String nivel, String cargo, String idZona, int zonaCombate,
                    String idCamp, boolean conectado, List<Combates> listaCombates,
                    String idCombate, String idCat) {
        this.idArbitro = idArbitro;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.tokenIDFCM = tokenIDFCM;
        this.foto = foto;
        this.pais = pais;
        this.federacion = federacion;
        this.nivel = nivel;
        this.cargo = cargo;
        this.idZona = idZona;
        this.zonaCombate = zonaCombate;
        this.idCamp = idCamp;

        // Añadir el ID del Campeonato a la lista de Campeonatos que en este caso está vacía al crear el registro del Árbitro.
        List<String> nuevaLista = new ArrayList<>();
        nuevaLista.add(this.idCamp);
        this.listaCamps = nuevaLista;

        this.conectado = conectado;
        this.listaCombates = listaCombates;

        this.idCombate = idCombate;
        this.idCat = idCat;
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

    public String getTokenIDFCM() {
        return tokenIDFCM;
    }

    public void setTokenIDFCM(String tokenIDFCM) {
        this.tokenIDFCM = tokenIDFCM;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getFederacion() {
        return federacion;
    }

    public void setFederacion(String federacion) {
        this.federacion = federacion;
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

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
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

    public void addToListaCamps(String idCamp){
        if(this.listaCamps == null)
        {
            List<String> nuevaLista = new ArrayList<>();
            this.listaCamps = nuevaLista;
        }
        this.listaCamps.add(idCamp);
    }

    public List<Combates> getListaCombates() {
        return listaCombates;
    }

    public void setListaCombates(List<Combates> listaCombates) {
        this.listaCombates = listaCombates;
    }

    public String getIdCombate() {
        return idCombate;
    }

    public String getIdCat() {
        return idCat;
    }

    public void setIdCat(String idCat) {
        this.idCat = idCat;
    }

    public void setIdCombate(String idCombate) {
        this.idCombate = idCombate;
    }

    public boolean getConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public boolean getListo() {
        return listo;
    }

    public void setListo(boolean listo) {
        this.listo = listo;
    }

    public Map<String, Object> toMap(){

        String nivel_cargo = nivel + "_" + cargo;

        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("dni", dni);
        result.put("idArbitro", idArbitro);
        result.put("email", email);
        result.put("password", password);
        result.put("foto", foto);
        result.put("pais", pais);
        result.put("federacion", federacion);
        result.put("nivel", nivel);
        result.put("cargo", cargo);
        result.put("nivel_cargo", nivel_cargo);
        result.put("zonaCombate", zonaCombate);
        result.put("listaCombates", listaCombates);
        result.put("conectado", conectado);
        result.put("idCamp", idCamp);
        result.put("idZona", idZona);
        result.put("listaCamps", listaCamps);
        result.put("listo", listo);
        result.put("tokenIDFCM", tokenIDFCM);
        result.put("idCombate", idCombate);
        result.put("idCat", idCat);

        return result;
    }
}

