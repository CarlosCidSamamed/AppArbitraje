package com.fervenzagames.apparbitraje;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.MiSpinnerAdapter;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CombatesActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Spinner mCampSpinner;
    private Spinner mModSpinner;
    private Spinner mCatSpinner;
    private Spinner mEstadoSpinner;

    private ExpandableListView mLista;
    private DatabaseReference mCombatesDB;

    private DatabaseReference mCampsDB;
    private List<String> mListaNombresCamps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combates);

        mToolbar = findViewById(R.id.combates_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Listado de Combates");

        mCampSpinner = findViewById(R.id.combates_nombreCampSpinner);
        mModSpinner = findViewById(R.id.combates_nombreModSpinner);
        mCatSpinner = findViewById(R.id.combates_nombreCatSpinner);
        mEstadoSpinner = findViewById(R.id.combates_estadoSpinner);

        mLista = findViewById(R.id.combates_lista);
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
        mCampsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos");
        mListaNombresCamps = new ArrayList<>();

        // Cargar los combates en la ExpandableList

        // Cargar los datos en los Spinners
        // Spinner Nombres Campeonatos
        // Cargar los datos de los nombres en una lista que después le pasaremos al Spinner
        cargarDatosCampeonatos();

    }

    private void cargarDatosCampeonatos(){

        final ArrayAdapter<CharSequence> adapterCamps = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mListaNombresCamps);

        mCampsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot campSnapshot:dataSnapshot.getChildren()){
                    Campeonatos camp = campSnapshot.getValue(Campeonatos.class);
                    mListaNombresCamps.add(camp.getNombre());
                }
                Toast.makeText(CombatesActivity.this, "El Spinner de Campeonatos tiene " + mListaNombresCamps.size() + " elementos.", Toast.LENGTH_SHORT).show();
                adapterCamps.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // ArrayAdapter adapterCamps = new MiSpinnerAdapter(this, mListaNombresCamps);
        // adapterCamps.setDropDownViewResource(R.layout.spinner_single_textview_dropdown_item);

        mCampSpinner.setAdapter(adapterCamps);
    }
}
