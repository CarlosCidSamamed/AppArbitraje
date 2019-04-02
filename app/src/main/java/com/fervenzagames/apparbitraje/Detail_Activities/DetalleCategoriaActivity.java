package com.fervenzagames.apparbitraje.Detail_Activities;

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

import com.fervenzagames.apparbitraje.Adapters.CombatesList;
import com.fervenzagames.apparbitraje.Add_Activities.AddCombateActivity;
import com.fervenzagames.apparbitraje.GenerarEmparejamientosActivity;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalleCategoriaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombreCat;
    private TextView mSexo;
    private TextView mEdad;
    private TextView mPeso;
    private ListView mListaCombatesView;
    private List<Combates> mLista;
    private Button mAddCombateBtn;

    private Button mGenerarEmpBtn;

    private DatabaseReference mCampDB;
    private DatabaseReference mModDB;
    private DatabaseReference mCatDB;
    private DatabaseReference mCombatesDB;

    private String mIdCamp;
    private String mIdMod;
    private String mIdCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_categoria);

        mToolbar =  findViewById(R.id.cat_detalle_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Categoría");

        mNombreCat = findViewById(R.id.cat_detalle_nombre);
        mSexo = findViewById(R.id.cat_detalle_sexo);
        mEdad = findViewById(R.id.cat_detalle_edad);
        mPeso = findViewById(R.id.cat_detalle_peso);

        mListaCombatesView = findViewById(R.id.cat_detalle_listaCombatesView);
        mLista = new ArrayList<>();
        mAddCombateBtn = findViewById(R.id.cat_detalle_addCombate_btn);

        mGenerarEmpBtn = findViewById(R.id.cat_detalle_generarEmp_btn);

        try {
            mIdCamp = getIntent().getExtras().getString("idCamp");
            mIdMod = getIntent().getExtras().getString("idMod");
            mIdCat = getIntent().getExtras().getString("idCat");

            mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(mIdCamp);
            mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(mIdMod);
            mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(mIdMod).child(mIdCat);
            mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
            cargarCombatesCategoria(mIdCat);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

/*        Toast.makeText(DetalleCategoriaActivity.this, "ID MOD --> " + idMod, Toast.LENGTH_LONG).show();
        Toast.makeText(DetalleCategoriaActivity.this, "ID CATEGORIA " + idCat, Toast.LENGTH_LONG).show();*/

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

                Bundle extras = new Bundle();
                extras.putString("idCamp", mIdCamp);
                extras.putString("idMod", mIdMod);
                extras.putString("idCat", mIdCat);

                Intent addCombateIntent = new Intent(DetalleCategoriaActivity.this, AddCombateActivity.class);
                addCombateIntent.putExtras(extras);
                startActivity(addCombateIntent);
            }
        });

        mGenerarEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generarIntent = new Intent(DetalleCategoriaActivity.this, GenerarEmparejamientosActivity.class);
                Bundle extras = new Bundle();
                extras.putString("idCamp", mIdCamp);
                extras.putString("idMod", mIdMod);
                extras.putString("idCat", mIdCat);

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

                extras.putString("sexo", mSexo.getText().toString());
                extras.putString("edad", mEdad.getText().toString());
                extras.putString("peso", mPeso.getText().toString());

                generarIntent.putExtras(extras);
                startActivity(generarIntent);
            }
        });


    }

    private void cargarCombatesCategoria(String idCat){
        Query consulta = mCombatesDB.child(idCat);
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(DetalleCategoriaActivity.this, "(DetalleCategoria) Error al localizar los combates de la Categoría", Toast.LENGTH_SHORT).show();
                } else {
                    mLista.clear();
                    for(DataSnapshot combateSnapshot: dataSnapshot.getChildren()){
                        Combates combate = combateSnapshot.getValue(Combates.class);
                        mLista.add(combate);
                    }
                    CombatesList adapter = new CombatesList(DetalleCategoriaActivity.this, mLista);
                    mListaCombatesView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
