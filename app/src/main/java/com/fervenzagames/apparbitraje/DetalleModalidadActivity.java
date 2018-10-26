package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.Models.Categorias;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DetalleModalidadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private String idCamp;
    private String idMod;
    private DatabaseReference modDB;
    private DatabaseReference campDB;
    private TextView modNombre;
    private TextView modDesc;
    private ListView mListaCatView;
    private Button mAddCatBtn;

    private String nombreCamp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_modalidad);

        nombreCamp = "";

        mToolbar = (Toolbar) findViewById(R.id.mod_detalle_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Modalidad ( " + nombreCamp + " )");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Deshabilitar la flecha atrás en la barra de título.

        modNombre = (TextView) findViewById(R.id.mod_detalle_nombre);
        modDesc = (TextView) findViewById(R.id.mod_detalle_desc);
        mListaCatView = (ListView) findViewById(R.id.mod_detalle_listCat);
        mAddCatBtn = (Button) findViewById(R.id.mod_detalle_addCat_btn);
/*
        idCamp = getIntent().getStringExtra("idCamp");
        idMod = getIntent().getStringExtra("idMod");*/

        // Vamos a recuperar idCamp e idMod del Bundle del Extra del Intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idCamp = extras.getString("idCamp");
        idMod = extras.getString("idMod");

        Toast.makeText(DetalleModalidadActivity.this, "PUT EXTRA idCamp --> " + idCamp, Toast.LENGTH_LONG).show();
        Toast.makeText(DetalleModalidadActivity.this, "PUT EXTRA idMod --> " + idMod, Toast.LENGTH_LONG).show();

        modDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp).child(idMod); // Referencia a esta Modalidad.
        campDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);

        campDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreCamp = dataSnapshot.child("nombre").getValue().toString();
                mToolbar.setTitle("Detalle Modalidad ( " + nombreCamp + " )");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        modDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String desc = dataSnapshot.child("descripcion").getValue().toString();

                modNombre.setText(nombre);
                modDesc.setText(desc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Añadir Categorías
        mAddCatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Para poder guardar la categoría en la modalidad que le corresponde deberemos pasar al intent el id de la Modalidad.
                // Vamos a recuperar idCamp e idMod del Bundle del Extra del Intent
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                idCamp = extras.getString("idCamp");
                idMod = extras.getString("idMod");
                Intent addCatIntent = new Intent(DetalleModalidadActivity.this, AddCategoriaActivity.class);
                Bundle extras2 = new Bundle();
                extras2.putString("idCamp", idCamp);
                extras2.putString("idMod", idMod);
                extras2.putString("nombreMod", modNombre.getText().toString());
                addCatIntent.putExtras(extras2);
                startActivity(addCatIntent);
            }
        });

    }
}
