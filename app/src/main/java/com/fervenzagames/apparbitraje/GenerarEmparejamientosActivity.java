package com.fervenzagames.apparbitraje;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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

public class GenerarEmparejamientosActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombreCamp;
    private TextView mNombreMod;
    private TextView mNombreCat;

    private DatabaseReference mCampDB;
    private DatabaseReference mModDB;
    private DatabaseReference mCatDB;
    private DatabaseReference mCompDB;

    private List<Competidores> mListaComp;
    private ListView mListaCompView;
    private TextView mNumCompetidores;

    private Spinner mTipoSpinner;

    private Button mGenerarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_emparejamientos);

        mToolbar = (Toolbar) findViewById(R.id.emparejamientos_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Generar Emparejamientos");

        Intent intent = getIntent();
        final Bundle extr = intent.getExtras();
        String idCamp = extr.getString("idCamp");
        String idMod = extr.getString("idMod");
        String idCat = extr.getString("idCat");

        final String sexo = extr.getString("sexo");
        final String peso = extr.getString("peso");
        final String edad = extr.getString("edad");

        mNombreCamp = (TextView) findViewById(R.id.generar_emp_nombreCamp);
        mNombreMod  = (TextView) findViewById(R.id.generar_emp_nombreMod);
        mNombreCat = (TextView) findViewById(R.id.generar_emp_nombreCat);


        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp).child(idMod);
        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);
        mCompDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores");

        mListaComp = new ArrayList<>();
        mListaCompView = (ListView) findViewById(R.id.generar_emp_listaCompetidores);
        mNumCompetidores = (TextView) findViewById(R.id.generar_emp_numCompetidores);

        mTipoSpinner =(Spinner) findViewById(R.id.generar_emp_tipoCompeticionSpinner);

        mGenerarBtn = (Button) findViewById(R.id.generar_emp_btn);

        //region TextViews

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

         //region Mostrar Lista Competidores

        Query consulta = mCompDB.orderByChild("id");

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaComp.clear();
                for(DataSnapshot comp: dataSnapshot.getChildren()){
                    Competidores competidor = comp.getValue(Competidores.class);

                    final String sexo = extr.getString("sexo");
                    final String peso = extr.getString("peso");
                    final String edad = extr.getString("edad");

                    if((competidor.getSexo().equals(sexo)) && (competidor.getCatPeso().equals(peso)) && (competidor.getCatEdad().equals(edad))){
                        // Si cumple las tres condiciones de sexo, edad y peso se añade a la lista para poder mostrarlo.
                        mListaComp.add(competidor);
                    }
                    // mListaComp.add(competidor);
                    CompetidoresList adapter = new CompetidoresList(GenerarEmparejamientosActivity.this, mListaComp);
                    adapter.notifyDataSetChanged();
                    mListaCompView.setAdapter(adapter);
                }
                if(mListaComp.size() == 0){
                    /*Toast.makeText(GenerarEmparejamientosActivity.this,
                            "No existe ningún competidor en la BD que cumpla esos requisitos. Sexo: " + sexo
                                    + ", CatEdad: " + edad + ", CatPeso: " + peso,
                            Toast.LENGTH_SHORT).show();*/

                    mGenerarBtn.setVisibility(View.INVISIBLE);
                }
                String numComp = String.valueOf(mListaComp.size());
                mNumCompetidores.setText(numComp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //endregion

        mGenerarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dependiendo del número de competidores de la categoría y del tipo de competición seleccionado se cargará el layout correspondiente
                // con LayoutInflater
                String tipo = mTipoSpinner.getSelectedItem().toString();
                // Si no se ha seleccionado ningún tipo de competición...
                if(tipo.equals("Tipo de Competición")){
                    Toast.makeText(GenerarEmparejamientosActivity.this, "Seleccione el tipo de competición...", Toast.LENGTH_SHORT).show();
                }
                String numComp = mNumCompetidores.getText().toString();
                int num = Integer.parseInt(numComp);

                // En el Bundle de extras se incluyen el ID del layout a cargar. Además del idCamp, idMod e idCat.
                final Bundle extras = extr;

                switch (tipo){
                    case "Eliminatoria":{
                        // Una vez que se ha determinado el tipo de competición se evaluará el número de Competidores de la Categoría.
                        switch(num){
                            case 0:{
                                Toast.makeText(GenerarEmparejamientosActivity.this,
                                        "No existe ningún competidor en esta categoría por lo que no se pueden generar los emparejamientos...",
                                        Toast.LENGTH_SHORT).show();
                                break; 
                            }
                            case 1:{
                                Toast.makeText(GenerarEmparejamientosActivity.this,
                                        "Solamente existe 1 competidor en esta categoría por lo que no se pueden generar los emparejamientos...",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 2:{ // Prueba
                                Intent empIntent = new Intent(GenerarEmparejamientosActivity.this, MostrarEmparejamientosActivity.class);
                                Bundle ext = extras;
                                String nombreLayout = "";
                                extr.putInt("layout", R.layout.emparejamientos_2_competidores_layout);
                                empIntent.putExtras(extras);
                                startActivity(empIntent);
                                break;
                            }
                            case 3:{
                                Intent empIntent = new Intent(GenerarEmparejamientosActivity.this, MostrarEmparejamientosActivity.class);
                                Bundle ext = extras;
                                extr.putInt("layout", R.layout.emparejamientos_3_competidores_layout);
                                empIntent.putExtras(ext);
                                startActivity(empIntent);
                                break;
                            }
                            case 4:{
                                break;
                            }
                            case 5:{
                                break;
                            }
                            case 6:{
                                break;
                            }
                            case 7:{
                                break;
                            }
                            case 8:{
                                Intent empIntent = new Intent(GenerarEmparejamientosActivity.this, MostrarEmparejamientosActivity.class);
                                Bundle ext = extras;
                                String nombreLayout = "";
                                extr.putInt("layout", R.layout.emparejamientos_8_competidores_layout);
                                empIntent.putExtras(extras);
                                startActivity(empIntent);
                                break;
                            }
                        }
                        break;
                    }
                    case "Round-Robin":{
                        break;
                    }
                }
            }
        });


    }
}
