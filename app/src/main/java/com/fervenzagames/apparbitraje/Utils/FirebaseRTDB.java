// En esta clase voy a centralizar las operaciones de recuperación de datos de la RTDB de Firebase
// Comnezaré con el acceso a los datos para mostrar la foto de un juez y la suma de sus puntuaciones para un competidor en un asalto determinado.
package com.fervenzagames.apparbitraje.Utils;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Adapters.DatosSumaAdapter;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.DatosSuma;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRTDB {

    private static DatabaseReference mAsaltoDB;
    private static DatabaseReference mCombateDB;
    private static DatabaseReference mJuezDB;
    private static DatabaseReference mRootDB;

    private static List<Puntuaciones> mListaPunt;

    //private static HashMap<String, List<DatosSuma>> datosMap;

    public static void getDatosSumaArbiComp(final String dniJuez, final String idRojo, final String idAzul,
                                            final String idAsalto, final String idCombate, final String urlFotoJuez,
                                            final Activity context, final ListView listViewRojo, final ListView listViewAzul,
                                            final List<DatosSuma> listaSumasRojo, final List<DatosSuma> listaSumasAzul,
                                            final TextView mediaRojoText, final TextView mediaAzulText){
        //datosMap = new HashMap<>();
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

                        listaSumasRojo.add(datosRojo);

                        listaSumasAzul.add(datosAzul);

                        //datosMap.put(dniJuez, listaSumas);

                        mostrarSumasParciales(context, listaSumasRojo, listViewRojo, "Rojo");
                        mostrarSumasParciales(context, listaSumasAzul, listViewAzul, "Azul");

                        int mediaRojo = calcularMediaCompetidor(listaSumasRojo);
                        int mediaAzul = calcularMediaCompetidor(listaSumasAzul);

                        actualizarPuntuacionCompetidor(mediaRojo,"Rojo", idCombate, idAsalto);
                        actualizarPuntuacionCompetidor(mediaAzul,"Azul", idCombate, idAsalto);

                        mostrarMedia(mediaRojo, "Rojo", mediaRojoText, context);
                        mostrarMedia(mediaAzul, "Azul", mediaAzulText, context);


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

    public static void mostrarSumasParciales(Activity context, List<DatosSuma> listaSumas, ListView listView, String lado) {
        // Crear el adapter
        DatosSumaAdapter datos = new DatosSumaAdapter(context, listaSumas, lado);

        // Asignarlo al ListView correspondiente
        listView.setAdapter(datos);
        datos.notifyDataSetChanged();
    }

    /*public HashMap<String, List<DatosSuma>> getDatosMap() {
        return datosMap;
    }*/

    public static int calcularMediaCompetidor(List<DatosSuma> sumasParciales){
        int suma = 0;
        // Recorrer la lista de sumas parciales
        for(int i = 0; i < sumasParciales.size(); i++){
            // Suma acumulativa
            suma += sumasParciales.get(i).getSumaPuntos();
        }
        // División entre el tamaño de la lista
        int res = suma / sumasParciales.size();
        // Devolver el resultado
        return res;
    }

    public static void mostrarMedia(int media, String lado, TextView textView, Activity context){
        Resources resources = context.getResources();
        if(lado.equals("Rojo")){
            textView.setTextColor(resources.getColor(R.color.colorRojo));
        } else if(lado.equals("Azul")){
            textView.setTextColor(resources.getColor(R.color.colorAccent2));
        }
        String m = String.valueOf(media);
        textView.setText(m);
    }

    public static void actualizarPuntuacionCompetidor(final int media, final String lado, final String idCombate, final String idAsalto){
        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(idCombate).child(idAsalto);
        Query consulta = mAsaltoDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("actualizarPunt", "Error al localizar el Asalto");
                } else {
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                    try {
                        if(lado.equals("Rojo")){
                            asalto.setPuntuacionRojo(media);
                        } else if(lado.equals("Azul")){
                            asalto.setPuntuacionAzul(media);
                        }
                        Map<String, Object> asaltoMap = asalto.toMap();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("/Asaltos/" + idCombate + "/" + idAsalto, asaltoMap);
                        mRootDB.updateChildren(updates);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
