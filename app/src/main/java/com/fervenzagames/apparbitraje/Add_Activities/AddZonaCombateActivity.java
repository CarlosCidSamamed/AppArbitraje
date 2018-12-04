package com.fervenzagames.apparbitraje.Add_Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddZonaCombateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNumZona;
    private TextView mNombreCampeonato;
    private ExpandableListView mListaCombatesDisponibles;
    private ExpandableListView mListaCombates;
    private Button mAsignarBtn;
    private Button mRevertirBtn;
    private Button mGuardarBtn;

    private String idCamp;
    private DatabaseReference mCampDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_zona_combate);

        mToolbar = findViewById(R.id.add_zona_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Asignar Combates a la Zona");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNumZona = findViewById(R.id.add_zona_numero);
        mNombreCampeonato = findViewById(R.id.add_zona_nombreCamp);
        mListaCombatesDisponibles = findViewById(R.id.add_zona_listaCombates);
        mListaCombates = findViewById(R.id.add_zona_listaCombates2);
        mAsignarBtn = findViewById(R.id.add_zona_addBtn);
        mRevertirBtn = findViewById(R.id.add_zona_removeBtn);
        mGuardarBtn = findViewById(R.id.add_zona_guardarBtn);

        Bundle extras = getIntent().getExtras();
        idCamp = extras.getString("idCamp");

        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);

        mCampDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Campeonatos camp = dataSnapshot.getValue(Campeonatos.class);

                try {
                    String nombre = camp.getNombre();
                    mNombreCampeonato.setText(nombre);

                    int numero = 0;
                    if(camp.getListaZonasCombate() != null){
                        numero = camp.getListaZonasCombate().size() + 1;
                    } else {
                        numero = 1;
                    }

                    String n = "Zona de Combate : " + String.valueOf(numero);
                    mNumZona.setTextColor(Color.BLUE);
                    mNumZona.setText(n);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
