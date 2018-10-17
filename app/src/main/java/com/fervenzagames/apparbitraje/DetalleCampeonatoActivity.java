package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class DetalleCampeonatoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombre;
    private TextView mLugar;
    private TextView mFecha;
    private TextView mTipo;

    private DatabaseReference campDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_campeonato);

        mToolbar = (Toolbar) findViewById(R.id.det_camp_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Campeonato");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNombre = (TextView) findViewById(R.id.camp_detalle_nombre);
        mLugar = (TextView) findViewById(R.id.camp_detalle_lugar);
        mFecha = (TextView) findViewById(R.id.camp_detalle_fecha);
        mTipo = (TextView) findViewById(R.id.camp_detalle_tipo);

        String idCamp = getIntent().getStringExtra("idCamp");
        campDB = FirebaseDatabase.getInstance().getReference().child("Arbitraje").child("Campeonatos").child(idCamp);

        campDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String lugar = dataSnapshot.child("lugar").getValue().toString();
                String fecha = dataSnapshot.child("fecha").getValue().toString();
                String tipo = dataSnapshot.child("tipo").getValue().toString();

                //Lista de Árbitros

                mNombre.setText(nombre);
                mLugar.setText(lugar);
                mFecha.setText(fecha);
                mTipo.setText(tipo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
