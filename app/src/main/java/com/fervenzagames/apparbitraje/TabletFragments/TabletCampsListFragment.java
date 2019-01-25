package com.fervenzagames.apparbitraje.TabletFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CampeonatosExpandableListAdapter;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TabletCampsListFragment extends Fragment {

    private ExpandableListView mLista;
    private AutoCompleteTextView mNombreAuto;
    private Spinner mFecha;
    private Spinner mLugar;
    private Spinner mTipo;

    private DatabaseReference mCampsDB;
    private Query consultaCamps;
    private List<String> mListaTitulos;
    private List<String> mListaDetalle;

    private CampeonatosExpandableListAdapter adapter;

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
        mListaDetalle = new ArrayList<>();

        // Lista sin filtros, es decir, TODOS los campeonatos de la BD ordenados por fecha.
        consultaCamps = mCampsDB.orderByChild("fecha");
        consultaCamps.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaTitulos.clear();
                mListaDetalle.clear();
                if(!dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "No existen CAMPEONATOS en la BD.", Toast.LENGTH_SHORT).show();
                } else {
                    for(DataSnapshot campSnap: dataSnapshot.getChildren()){
                        Campeonatos camp = campSnap.getValue(Campeonatos.class);
                        try {
                            mListaTitulos.add(camp.getNombre()); // Obtener el nombre
                            mListaDetalle.add(camp.getFecha());  // Obtener la fecha
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    // Una vez que tenemos los datos a mostrar en el ExpandableListView deberemos asignar el adapter
                    adapter = new CampeonatosExpandableListAdapter(getActivity(), mListaTitulos, mListaDetalle);
                    mLista.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    // Modificar este fragment para que cargue los datos de los campeonatos de la BD con los filtros indicados.
    // Lista de Campeonatos --> ExpandableListView --> Necesario definir header y child (layout)
}
