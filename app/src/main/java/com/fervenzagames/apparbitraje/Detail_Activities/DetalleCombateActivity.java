package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.AsaltosList;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.common.util.Base64Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleCombateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombreCamp;
    private TextView mMod;
    private TextView mNumero;
    private TextView mCat;
    private TextView mEnlace;
    private TextView mZona;
    private TextView mEstado;
    private TextView mGanador;

    private CircleImageView mFotoRojo;
    private TextView mNombreRojo;
    private CircleImageView mFotoAzul;
    private TextView mNombreAzul;

    private ListView mListaAsaltosView;
    private List<Asaltos> mListaAsaltos;

    private DatabaseReference mCombateDB;
    private Bundle extras;
    private String idCombate;

    private DatabaseReference mCampDB;
    private String idCamp;
    private String nombreCamp;
    private DatabaseReference mModDB;
    private String idMod;
    private String nombreMod;
    private DatabaseReference mCatDB;
    private String idCat;
    private String nombreCat;
    private DatabaseReference mZonaDB;
    private String idZona;
    private String numZona;
    private DatabaseReference mRojoDB;
    private DatabaseReference mAzulDB;
    private DatabaseReference mAsaltosDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_combate);

        mToolbar = findViewById(R.id.comb_detalle_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Combate");

        mNombreCamp = findViewById(R.id.comb_detalle_nombreCampeonato);
        mMod = findViewById(R.id.comb_detalle_modNombre);
        mNumero = findViewById(R.id.comb_detalle_numCombate);
        mCat = findViewById(R.id.comb_detalle_catNombre);
        mEnlace = findViewById(R.id.comb_detalle_enlaceVideo);
        mZona = findViewById(R.id.comb_detalle_zonaCombate);
        mEstado = findViewById(R.id.comb_detalle_estado);
        mGanador = findViewById(R.id.comb_detalle_ganador);

        mFotoRojo = findViewById(R.id.comb_detalle_Rojo);
        mNombreRojo = findViewById(R.id.comb_detalle_Rojo_nombre);

        mFotoAzul = findViewById(R.id.comb_detalle_Azul);
        mNombreAzul = findViewById(R.id.comb_detalle_Azul_nombre);

        mListaAsaltosView = findViewById(R.id.comb_detalle_listaAsaltos);
        mListaAsaltos = new ArrayList<>();

        extras = getIntent().getExtras();
        try {
            idCombate = extras.getString("idCombate");
            idCamp = extras.getString("idCamp");
            idMod = extras.getString("idMod");
            idCat = extras.getString("idCat");
            idZona = extras.getString("idZona");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Combates").child(idCat).child(idCombate);    // Datos de este Combate
        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos").child(idCamp);                // Campeonato de este Combate
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(idCamp).child(idMod);    // Modalidad de este Combate
        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(idMod).child(idCat);      // Categoría de este Combate
        mZonaDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate").child(idCamp).child(idZona); // Zona a la que está asignado este Combate
        mAsaltosDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(idCombate);              // Lista de Asaltos de este Combate
        mRojoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");
        mAzulDB = mRojoDB;  // Hasta que no recuperemos los datos del Combate no podemos especificar el ID de los Competidores.

        // Obtener nombre del Campeonato
        mCampDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreCamp = dataSnapshot.child("nombre").getValue().toString();
                mNombreCamp.setText(nombreCamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Obtener el nombre de la Modalidad
        mModDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreMod = dataSnapshot.child("nombre").getValue().toString();
                mMod.setText(nombreMod);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Obtener el nombre de la Categoría
        mCatDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreCat = dataSnapshot.child("nombre").getValue().toString();
                mCat.setText(nombreCat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Obtener el número de la Zona de Combate
        mZonaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 numZona = dataSnapshot.child("numZona").getValue().toString();
                 numZona = "Zona de Combate : " + numZona;
                 mZona.setText(numZona);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAsaltosDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaAsaltos.clear();
                for(DataSnapshot asaltoSnapshot:dataSnapshot.getChildren()){
                    Asaltos asalto = asaltoSnapshot.getValue(Asaltos.class);
                    mListaAsaltos.add(asalto);
                }
                Toast.makeText(DetalleCombateActivity.this, "Nº de Asaltos de este Combate : " + mListaAsaltos.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mCombateDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        // Estado
                        String estado = dataSnapshot.child("estadoCombate").getValue().toString();
                        mEstado.setText(estado);
                        // Ganador
                        String ganador = dataSnapshot.child("ganador").getValue().toString();
                        mGanador.setText(ganador);
                        // Datos Rojo
                        String idRojo = dataSnapshot.child("idRojo").getValue().toString();
                        Query consultaRojo = mRojoDB.child(idRojo);
                        consultaRojo.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String nombreRojo = dataSnapshot.child("nombre").getValue().toString();
                                nombreRojo = nombreRojo + " " + dataSnapshot.child("apellido1").getValue().toString();
                                nombreRojo = nombreRojo + " " + dataSnapshot.child("apellido2").getValue().toString();
                                String fotoRojo = dataSnapshot.child("foto").getValue().toString();
                                mNombreRojo.setText(nombreRojo);
                                Picasso.get().load(fotoRojo).into(mFotoRojo);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        // Datos Azul
                        String idAzul = dataSnapshot.child("idAzul").getValue().toString();
                        Query consultaAzul = mAzulDB.child(idAzul);
                        consultaAzul.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String nombreAzul = dataSnapshot.child("nombre").getValue().toString();
                                nombreAzul = nombreAzul + " " + dataSnapshot.child("apellido1").getValue().toString();
                                nombreAzul = nombreAzul + " " + dataSnapshot.child("apellido2").getValue().toString();
                                String fotoAzul = dataSnapshot.child("foto").getValue().toString();
                                mNombreAzul.setText(nombreAzul);
                                Picasso.get().load(fotoAzul).into(mFotoAzul);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        // Lista Asaltos
                        // Crear Adapter para los Asaltos y asignárselo a la lista de Asaltos.
                        AsaltosList adapter = new AsaltosList(DetalleCombateActivity.this, mListaAsaltos);
                        adapter.setDropDownViewResource(R.layout.asalto_single_layout);
                        mListaAsaltosView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DetalleCombateActivity.this, "No existe ningún campeonato con ese ID.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mListaAsaltosView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Asaltos asalto = mListaAsaltos.get(position);
                Bundle extras = new Bundle();
                extras.putString("idCombate", idCombate);
                extras.putString("idAsalto", asalto.getId());
                Intent detalleAsaltoIntent = new Intent(DetalleCombateActivity.this, DetalleAsaltoActivity.class);
                detalleAsaltoIntent.putExtras(extras);
                startActivity(detalleAsaltoIntent);
            }
        });

    }
}
