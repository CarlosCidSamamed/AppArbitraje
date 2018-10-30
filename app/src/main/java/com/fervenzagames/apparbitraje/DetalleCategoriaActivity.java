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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetalleCategoriaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombreCat;
    private TextView mSexo;
    private TextView mEdad;
    private TextView mPeso;
    private ListView mListaCombates;
    private Button mAddCombateBtn;

    private DatabaseReference mCampDB;
    private DatabaseReference mModDB;
    private DatabaseReference mCatDB;
    private DatabaseReference mCombatesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_categoria);

        mToolbar = (Toolbar) findViewById(R.id.cat_detalle_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Categoría");

        mNombreCat = (TextView) findViewById(R.id.cat_detalle_nombre);
        mSexo = (TextView) findViewById(R.id.cat_detalle_sexo);
        mEdad = (TextView) findViewById(R.id.cat_detalle_edad);
        mPeso = (TextView) findViewById(R.id.cat_detalle_peso);

        mListaCombates = (ListView) findViewById(R.id.cat_detalle_listaCombatesView);
        mAddCombateBtn = (Button) findViewById(R.id.cat_detalle_addCombate_btn);

        String idCamp = getIntent().getExtras().getString("idCamp");
        String idMod = getIntent().getExtras().getString("idMod");
        String idCat = getIntent().getExtras().getString("idCat");

        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idMod);
        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);

        Toast.makeText(DetalleCategoriaActivity.this, "ID MOD --> " + idMod, Toast.LENGTH_LONG).show();
        Toast.makeText(DetalleCategoriaActivity.this, "ID CATEGORIA " + idCat, Toast.LENGTH_LONG).show();

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNombreCat.setText(dataSnapshot.child("nombre").getValue().toString());
                mSexo.setText(dataSnapshot.child("sexo").getValue().toString());
                mEdad.setText(dataSnapshot.child("edad").getValue().toString());
                mPeso.setText(dataSnapshot.child("peso").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAddCombateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* En Detalle Modalidad pasamos como extras los siguientes datos:
                *       idCamp
                *       idMod
                *       idCat
                *
                *  Pasaremos esos mismos datos como extra a Añadir Combate
                */

                String idCamp = getIntent().getExtras().getString("idCamp");
                String idMod = getIntent().getExtras().getString("idMod");
                String idCat = getIntent().getExtras().getString("idCat");

                Bundle extras = new Bundle();
                extras.putString("idCamp", idCamp);
                extras.putString("idMod", idMod);
                extras.putString("idCat", idCat);

                Intent addCombateIntent = new Intent(DetalleCategoriaActivity.this, AddCombateActivity.class);
                addCombateIntent.putExtras(extras);
                startActivity(addCombateIntent);
            }
        });


    }
}
