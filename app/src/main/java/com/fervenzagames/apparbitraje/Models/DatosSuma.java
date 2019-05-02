package com.fervenzagames.apparbitraje.Models;

// Esta clase sirve para encapsular la información necesaria para poder mostrar la suma de las puntuaciones de un juez par un competidor en un asalto
// Es decir, la foto del juez y la suma de sus puntuaciones para un competidor en concreto

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatosSuma {

    private String fotoJuez;
    private int sumaPuntos;
    private String dniJuez;

    private String idJuez;
    private String idComp;
    private String idAsalto;
    private String idCombate;

    private Arbitros mDatosJuez;

    private DatabaseReference mJuezDB;
    private DatabaseReference mAsaltoDB;

    private List<Puntuaciones> mListaPunts;


    public DatosSuma() {
    }

    public DatosSuma(String fotoJuez, int sumaPuntos, String dniJuez, String idComp, String idAsalto, String idCombate) {
        //this.sumaPuntos = sumaPuntos;
        this.mDatosJuez = new Arbitros();
        this.dniJuez = dniJuez;
        this.fotoJuez = getFotoJuez(dniJuez);
        this.idComp = idComp;
        this.idAsalto = idAsalto;
        this.idCombate = idCombate;
        this.mListaPunts = new ArrayList<>();
        getmListaPunts(idAsalto, idCombate, idComp); // mListaPunts
        Log.v("DatosSuma", "ListaPuntos.size() --> " + mListaPunts.size());
        //this.sumaPuntos = calcularSumaPuntos(idJuez, idComp, mListaPunts);
    }

    public DatosSuma(String fotoJuez, int sumaPuntos, String dniJuez, String idComp, String idAsalto, String idCombate, boolean extra){
        this.fotoJuez = fotoJuez;
        this.dniJuez = dniJuez;
        this.sumaPuntos = sumaPuntos;
        this.idComp = idComp;
        this.idAsalto = idAsalto;
        this.idCombate = idCombate;
    }

    public String getFotoJuez(String dniJuez) {
        getDatosJuez(dniJuez);
        //fotoJuez = mDatosJuez.getFoto();
        return fotoJuez;
    }

    public String getDniJuez() {
        if(mDatosJuez != null) {
            dniJuez = mDatosJuez.getDni();
        }
        return dniJuez;
    }

    public void setDniJuez(String dniJuez) {
        this.dniJuez = dniJuez;
    }

    public void setFotoJuez(String fotoJuez) {
        this.fotoJuez = fotoJuez;
    }

    public void getDatosJuez(final String dniJuez){
        mJuezDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        Query consulta = mJuezDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("DatosSuma", "getDatosJuez --> Error al localizar al juez cuyo DNI es " + dniJuez);
                } else {
                    for(DataSnapshot juezSnap: dataSnapshot.getChildren()){
                        Arbitros arbi = juezSnap.getValue(Arbitros.class);
                        Log.v("DatosSuma" , "getDatosJuez --- arbi.getIdArbitro : " + arbi.getIdArbitro());
                        try {
                            if(arbi.getDni().equals(dniJuez)){
                                mDatosJuez = arbi;
                                idJuez = arbi.getIdArbitro();
                                fotoJuez = arbi.getFoto();
                            }
                        } catch (NullPointerException e) {
                            Log.v("DatosSuma", "getDatosJuez --> Excepción : " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getIdJuez(){
        return idJuez;
    }

    public void setIdJuez(String id){
        idJuez = id;
    }

    public int calcularSumaPuntos(String idJuez, String idCompetidor, List<Puntuaciones> listaPunts) {
        // En este método se deberá calcular la suma de los puntos de un juez para un competidor en un asalto concreto.
        List<Puntuaciones> listaFiltrada = new ArrayList<>();
        Log.v("DatosSuma", "calcularSumaPuntos --- Tamaño de la lista de ENTRADA ---> " + listaPunts.size());
        if(listaPunts.size() > 0) {
            for (int i = 0; i < listaPunts.size(); i++) {
                Puntuaciones p = listaPunts.get(i);
                Log.v("DatosSuma", "calcularSumaPuntos --> Puntuación " + i + " : DNI" + p.getDniJuez() + " ... Valor " + p.getValor());
                if (p.getDniJuez().equals(mDatosJuez.getDni())) {   // El DNI del Juez es el correcto
                    if (p.getIdCompetidor().equals(idCompetidor)) { // El idCompetidor es el correcto
                        listaFiltrada.add(p);                     // Añadimos la puntuación a la lista
                    }
                }
            }
            Log.v("calcularSumaPuntos", "Tamaño de la lista filtrada " + listaFiltrada.size());
            if (listaFiltrada.size() > 0) {                        // Si tenemos alguna puntuación para ese juez y ese competidor
                for (int i = 0; i < listaFiltrada.size(); i++) {   // Recorremos la lista
                    sumaPuntos += listaFiltrada.get(i).getValor(); // Sumamos los valores.
                }
            }
        } else {
            sumaPuntos = 0;
        }
        Log.v("calcularSumaPuntos", "Suma de los puntos para el Competidor con ID " + idCompetidor + " según el JUEZ con ID " + idJuez + " --> SUMA = " + sumaPuntos);
        return sumaPuntos;
    }

    public int getSumaPuntos(){
        return sumaPuntos;
    }

    public void setSumaPuntos(int sumaPuntos) {
        this.sumaPuntos = sumaPuntos;
    }

    public void getmListaPunts(final String idAsalto, String idCombate, final String idCompetidor){
        mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(idCombate).child(idAsalto);
        Query consulta = mAsaltoDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("DatosSuma", "Error al localizar el asalto indicado");
                } else {
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                    try {
                        mListaPunts = asalto.getListaPuntuaciones();
                        Log.v("DatosSuma", "getmListaPunts --> Asalto ID es " + idAsalto + " --- Tamaño lista puntuaciones del asalto ---> " +mListaPunts.size());
                        sumaPuntos = calcularSumaPuntos(idJuez, idCompetidor, mListaPunts);
                        //Log.v("DatosSuma", "Valor de la Suma de Puntos para el Juez " + idJuez + " y el Competidor " + idComp + " --> " + sumaPuntos);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Log.v("DatosSuma", "getmListaPunts --> Excepción  : " + e.getLocalizedMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public List<Puntuaciones> getmListaPunts(){
        return mListaPunts;
    }

    public String getIdComp() {
        return idComp;
    }

    public void setIdComp(String idComp) {
        this.idComp = idComp;
    }

    public String getIdAsalto() {
        return idAsalto;
    }

    public void setIdAsalto(String idAsalto) {
        this.idAsalto = idAsalto;
    }

    public String getIdCombate() {
        return idCombate;
    }

    public void setIdCombate(String idCombate) {
        this.idCombate = idCombate;
    }
}
