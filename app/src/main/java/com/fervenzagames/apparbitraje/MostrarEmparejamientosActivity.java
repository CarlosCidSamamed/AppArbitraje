package com.fervenzagames.apparbitraje;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CompetidoresList;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MostrarEmparejamientosActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mNombreCat;

    private TextView mPruebaArray;

    private DatabaseReference mCatDB;
    private DatabaseReference mCompDB;

    private Integer[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_mostrar_emparejamientos);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String idCamp = extras.getString("idCamp");
        final String idMod = extras.getString("idMod");
        final String idCat = extras.getString("idCat");
        int idLayout = extras.getInt("layout");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(idLayout, null);

        setContentView(layout);

        mToolbar = (Toolbar) findViewById(R.id.mostrar_emp_app_bar);
        setSupportActionBar(mToolbar);

        switch(idLayout){
            case R.layout.emparejamientos_2_competidores_layout:{
                mNombreCat = (TextView) findViewById(R.id.dos_comp_nombreCat);
                mPruebaArray = (TextView) findViewById(R.id.pruebaArray);
                array = sorteoCompetidores(2);
                break;
            }
            case R.layout.emparejamientos_8_competidores_layout:{
                mNombreCat = (TextView) findViewById(R.id.ocho_comp_nombreCat);
                break;
            }
        }

        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);
        mCompDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores");

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String titulo = dataSnapshot.child("nombre").getValue().toString();
                mNombreCat.setText(titulo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //region Generar Números Aleatorios
    public Integer[] sorteoCompetidores(int numCompetidores){

        Random r = new Random();
        Integer[] array = new Integer[numCompetidores];

        for(int i = 0; i < numCompetidores; i++){
            int num = r.nextInt(numCompetidores);
            array[i] = num;
        }
        // Corrección de duplicados para 2 competidores. El generador de números no funciona bien cuando el número máximo es 2.
        if(numCompetidores == 2){
            if(array[0] == array[1]){
                if(array[0] == 1) {
                    array[1] = 0;
                } else if(array[0] == 0){
                    array[0] = 0;
                }
            }
        }


        // Eliminar duplicados
        for(int i = 0; i < numCompetidores; i++){
            for(int j = 0; j < numCompetidores; j ++){
                if(i != j){
                    if(array[i] == array[j]){
                        array[i] = r.nextInt(numCompetidores);
                    }
                }
            }
        }

        // Mostrar los elementos del array
        String res = "";
        for(int i = 0; i < numCompetidores; i++){
            res += i + " --> " + array[i] + "\n";
        }
        mPruebaArray.setText(res);

        return array;
    }
    //endregion

    //region Asignar Competidores
    // Lee la lista de competidores de la BD y le asigna a cada competidor el número que se ha almacenado en el array de Integer
    // que se pasa como parámetro.
    public void asignarCompetidores(Integer[] array){

        final List<Competidores> listaComp = new ArrayList<>();

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final String sexo = dataSnapshot.child("nombre").getValue().toString();
                    final String peso = dataSnapshot.child("peso").getValue().toString();
                    final String edad = dataSnapshot.child("edad").getValue().toString();

                    Query consulta = mCompDB;   // Lista de los competidores

                    consulta.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaComp.clear();
                            for(DataSnapshot compSnapshot: dataSnapshot.getChildren()){
                                Competidores comp = compSnapshot.getValue(Competidores.class);
                                if((comp.getSexo().equals(sexo)) &&
                                        (comp.getCatEdad().equals(edad)) &&
                                        (comp.getCatPeso().equals(peso))){
                                    listaComp.add(comp);
                                }
                            }
                            CompetidoresList adapter = new CompetidoresList(MostrarEmparejamientosActivity.this, listaComp);
                            adapter.notifyDataSetChanged();
                            if(listaComp.size() == 0){
                                Toast.makeText(MostrarEmparejamientosActivity.this,
                                        "No existe ningún competidor en la BD que cumpla esos requisitos. Sexo: " + sexo
                                                + ", CatEdad: " + edad + ", CatPeso: " + peso,
                                        Toast.LENGTH_SHORT).show();
                            }

                            // Una vez que tenemos el array de números aleatorios y la lista de Competidores debemos asignar su número a cada competidor.
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //endregion
}
