package com.fervenzagames.apparbitraje.TabletFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CampeonatosExpandableListAdapter;
import com.fervenzagames.apparbitraje.Adapters.CampeonatosList;
import com.fervenzagames.apparbitraje.Adapters.CampeonatosMiniList;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TabletCampsListFragment extends Fragment {

    //private ExpandableListView mLista;
    private ListView mLista;
    private AutoCompleteTextView mNombreAuto;
    private Spinner mFecha;
    private Spinner mLugar;
    private Spinner mTipo;

    private DatabaseReference mCampsDB;
    private Query consultaCamps;
    private List<String> mListaTitulos;
    private List<String> mListaDetalleFecha;
    private List<String> mListaDetalleLugar;
    private List<String> mListaDetalle;
    private HashMap<String, List<String>> mHashMap;

    private List<Campeonatos> mListaCamps;

    //private CampeonatosExpandableListAdapter adapter;
    private CampeonatosMiniList adapter;

    public TabletCampsListFragment(){

        mCampsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos");
        consultaCamps = mCampsDB;
        consultaCamps.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getActivity(), "Ruta consulta --> " + dataSnapshot.getRef(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tablet_camps_list_fragment, container, false);

        mLista = rootView.findViewById(R.id.tablet_camps_list);
        mNombreAuto = rootView.findViewById(R.id.tablet_camps_list_nombreAuto);
        mFecha = rootView.findViewById(R.id.tablet_camps_list_fecha_spinner);
        mLugar = rootView.findViewById(R.id.tablet_camps_list_lugar_spinner);
        mTipo = rootView.findViewById(R.id.tablet_camps_list_tipo_spinner);

        mListaTitulos = new ArrayList<>();
        mListaDetalleFecha = new ArrayList<>();
        mListaDetalleLugar = new ArrayList<>();
        mListaDetalle = new ArrayList<>();
        mHashMap = new HashMap<>();

        mListaCamps = new ArrayList<>();

        // Consulta para obtrner los nombres de los campeonatos
        consultaCamps = mCampsDB;
        consultaCamps.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaTitulos.clear();
                for(DataSnapshot campSnap: dataSnapshot.getChildren()){
                    Campeonatos camp = campSnap.getValue(Campeonatos.class);
                    try {
                        mListaTitulos.add(camp.getNombre());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if(mListaTitulos.size() > 0){
                    Toast.makeText(getActivity(), "Número de resultados para el autocomplete " + mListaTitulos.size(), Toast.LENGTH_SHORT).show();
                    for(int i = 0; i < mListaTitulos.size(); i++){
                        Toast.makeText(getActivity(), mListaTitulos.get(i), Toast.LENGTH_SHORT).show();
                    }
                    // AutocompleteTextView Adapter
                    ArrayAdapter<String> autocompleteAdapter = null;
                    try {
                        autocompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mListaTitulos);
                        mNombreAuto.setAdapter(autocompleteAdapter);
                        autocompleteAdapter.notifyDataSetChanged();
                        mNombreAuto.setThreshold(1);
                    } catch (NullPointerException e) {
                        Toast.makeText(getActivity(), "Error en el autocomplete. Excepción --> "  + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Lista sin filtros, es decir, TODOS los campeonatos de la BD ordenados por fecha.
        consultaCamps = mCampsDB.orderByChild("fecha");
        consultaCamps.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaTitulos.clear();
                mListaDetalleFecha.clear();
                mListaDetalleLugar.clear();
                mListaDetalle.clear();
                mListaCamps.clear();
                if(!dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "No existen CAMPEONATOS en la BD.", Toast.LENGTH_SHORT).show();
                } else {
                    for(DataSnapshot campSnap: dataSnapshot.getChildren()){
                        Campeonatos camp = campSnap.getValue(Campeonatos.class);
                        try {
                            mListaCamps.add(camp);
                            /*mListaTitulos.add(camp.getNombre()); // Obtener el nombre
                            Toast.makeText(getActivity(),
                                    "Nombre : " + camp.getNombre() +  " ||| Fecha :" + camp.getFecha() + " ||| Lugar :" + camp.getLugar(),
                                    Toast.LENGTH_SHORT).show();
                            *//* mListaDetalleFecha.add(camp.getFecha());  // Obtener la fecha
                            mListaDetalleLugar.add(camp.getLugar());  // Obtener el lugar
                            *//*
                            mListaDetalle.clear();
                            mListaDetalle.add(camp.getFecha());
                            mListaDetalle.add(camp.getLugar());
                            mHashMap.put(camp.getNombre(), mListaDetalle);*/
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    //formatearDatosDetalle();

                    // Una vez que tenemos los datos a mostrar en el ExpandableListView deberemos asignar el adapter
                    //adapter = new CampeonatosExpandableListAdapter(getActivity(), mListaTitulos, mHashMap);
                    adapter = new CampeonatosMiniList(getActivity(), mListaCamps);
                    mLista.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void formatearDatosDetalle(){
        // Recorrer la lista de Campeonatos y extraer el nombre, la fecha y el lugar de cada uno.
        for(int i = 0; i < mListaCamps.size(); i++){
            mListaDetalle.clear();
            Campeonatos camp = mListaCamps.get(i);
            mListaTitulos.add(camp.getNombre());
            mListaDetalle.add(camp.getFecha());
            mListaDetalle.add(camp.getLugar());
            mHashMap.put(camp.getNombre(), mListaDetalle);
        }
    }

    // Modificar este fragment para que cargue los datos de los campeonatos de la BD con los filtros indicados.
    // Lista de Campeonatos --> ExpandableListView --> Necesario definir header y child (layout)
}
