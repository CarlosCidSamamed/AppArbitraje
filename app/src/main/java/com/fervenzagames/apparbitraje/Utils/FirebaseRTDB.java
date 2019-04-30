// En esta clase voy a centralizar las operaciones de recuperación de datos de la RTDB de Firebase
// Comnezaré con el acceso a los datos para mostrar la foto de un juez y la suma de sus puntuaciones para un competidor en un asalto determinado.
package com.fervenzagames.apparbitraje.Utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import com.fervenzagames.apparbitraje.Adapters.DatosSumaAdapter;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.DatosSuma;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseRTDB {

    private DatabaseReference mAsaltoDB;
    private DatabaseReference mCombateDB;
    private DatabaseReference mJuezDB;

    private List<Puntuaciones> mListaPunt;

    private HashMap<String, List<DatosSuma>> datosMap;

    public void getDatosSumaArbiComp(final String dniJuez, final String idRojo, final String idAzul,
                                     final String idAsalto, final String idCombate, final String urlFotoJuez,
                                     final Activity context, final ListView listViewRojo, final ListView listViewAzul){
        datosMap = new HashMap<>();
        mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(idCombate).child(idAsalto);
        Query consulta = mAsaltoDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recuperar los datos del asalto
                if(!dataSnapshot.exists()){
                    Log.v("FirebaseRTDB", "getDatosSumaArbiComp --> Error al localizar el Asalto.");
                } else {
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                    try {
                        // Obtener la lista de Puntuaciones
                        mListaPunt = asalto.getListaPuntuaciones();
                        List<Puntuaciones> listaFiltrada = new ArrayList<>();
                        // Filtrar lista de puntos por juez
                        for(int i = 0; i < mListaPunt.size(); i++){
                            Puntuaciones p = mListaPunt.get(i);
                            if(p.getDniJuez().equals(dniJuez)){
                                listaFiltrada.add(p);
                            }
                        }
                        // Calcular suma parcial para cada juez y competidor
                        int sumaRojo = 0;
                        int sumaAzul = 0;
                        for(int j = 0; j < listaFiltrada.size(); j++){
                            Puntuaciones p = listaFiltrada.get(j);
                            if(p.getIdCompetidor().equals(idRojo)){
                                sumaRojo += p.getValor();
                            } else if (p.getIdCompetidor().equals(idAzul)){
                                sumaAzul += p.getValor();
                            }
                        }
                        // Añadir ambas sumas al HashMap
                        DatosSuma datosRojo = new DatosSuma(urlFotoJuez, sumaRojo, dniJuez, idRojo, idAsalto, idCombate, false);
                        DatosSuma datosAzul = new DatosSuma(urlFotoJuez, sumaAzul, dniJuez, idAzul, idAsalto, idCombate, false);
                        List<DatosSuma> listaSumasRojo = new ArrayList<>();
                        listaSumasRojo.add(datosRojo);
                        List<DatosSuma> listaSumasAzul = new ArrayList<>();
                        listaSumasAzul.add(datosAzul);

                        //datosMap.put(dniJuez, listaSumas);

                        mostrarSumasParciales(context, listaSumasRojo, listViewRojo);
                        mostrarSumasParciales(context, listaSumasAzul, listViewAzul);


                    } catch (NullPointerException e) {
                        Log.v("FirebaseRTDB", "Excepción al obtener la lista de Puntuaciones --- " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }


                // Mostrar foto y suma para cada juez y competidor
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mostrarSumasParciales(Activity context, List<DatosSuma> listaSumas, ListView listView) {
        // Crear el adapter
        DatosSumaAdapter datos = new DatosSumaAdapter(context, listaSumas, "Rojo");

        // Asignarlo al ListView correspondiente
        listView.setAdapter(datos);
        datos.notifyDataSetChanged();
    }

    public HashMap<String, List<DatosSuma>> getDatosMap() {
        return datosMap;
    }
}
