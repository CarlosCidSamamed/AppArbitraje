package com.fervenzagames.apparbitraje.Add_Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Dialogs.AddCompetidorDialog;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCombateActivity extends AppCompatActivity implements AddCompetidorDialog.AddCompetidorDialogListener {

    private Toolbar mToolbar;

    private TextView mNombreCamp;
    private TextView mNombreMod;
    private TextView mNombreCat;

    private Spinner mEstadoSpinner;

    // Competidor Rojo
    private CircleImageView mFotoRojo;
    private TextView mNombreRojo;
    private Button mRojoBtn;

    // Competidor Azul
    private CircleImageView mFotoAzul;
    private TextView mNombreAzul;
    private Button mAzulBtn;

    private Button mGuardarBtn;

    // Referencias a la BD
    private DatabaseReference mCampDB;
    private DatabaseReference mModDB;
    private DatabaseReference mCatDB;
    private DatabaseReference mCombateDB;


    private String idCamp;
    private String idMod;
    private String idCat;

    // Los identificadores que devuelve el dialogo en el que se añade un competidor. Este ID se obtiene de la DB y sirve para
    // poder localizar su nombre y apellidos en la DB y la imagen en el Storage de Firebase.
    private String idRojo;
    private String idAzul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_combate);

        mToolbar = (Toolbar) findViewById(R.id.add_comb_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Combate");

        mNombreCamp = (TextView) findViewById(R.id.add_comb_nombreCampeonato);
        mNombreMod = (TextView) findViewById(R.id.add_comb_modNombre);
        mNombreCat = (TextView) findViewById(R.id.add_comb_catNombre);

        mEstadoSpinner = (Spinner) findViewById(R.id.add_comb_estadoSpinner);

        mFotoRojo = (CircleImageView) findViewById(R.id.add_comb_Rojo);
        mNombreRojo = (TextView) findViewById(R.id.add_comb_Rojo_nombre);
        mRojoBtn = (Button) findViewById(R.id.add_comb_Rojo_btn);

        mFotoAzul = (CircleImageView) findViewById(R.id.add_comb_Azul);
        mNombreAzul = (TextView) findViewById(R.id.add_comb_Azul_nombre);
        mAzulBtn = (Button) findViewById(R.id.add_comb_Azul_btn);

        mGuardarBtn = (Button) findViewById(R.id.add_comb_guardar_btn);

        Intent intent = getIntent();
        idCamp = intent.getExtras().getString("idCamp");
        idMod = intent.getExtras().getString("idMod");
        idCat = intent.getExtras().getString("idCat");

        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp).child(idMod);
        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);

        idRojo = "";
        idAzul = "";

        //region Leer Datos DB para TextViews
        mCampDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNombreCamp.setText(dataSnapshot.child("nombre").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mModDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNombreMod.setText(dataSnapshot.child("nombre").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNombreCat.setText(dataSnapshot.child("nombre").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion

        //region Botones Añadir Competidores
        mRojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idRojo = abrirDialogo();
            }
        });

        mAzulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idAzul = abrirDialogo();
            }
        });
        //endregion
    }

    //region Método Abrir Diálogo Añadir Competidor
    public String abrirDialogo(){
        String res = "";

        // Mostramos el dialogo...
        AddCompetidorDialog dialogo = new AddCompetidorDialog();
        dialogo.show(getSupportFragmentManager(), "AddCompetidorDialog");
        res = dialogo.idCompetidor;

        return res;
    }

    @Override
    public String getIdCompetidor(Competidores comp) {
        return comp.getId();
    }
    //endregion
}
