package com.fervenzagames.apparbitraje.Add_Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CompetidoresList;
import com.fervenzagames.apparbitraje.Dialogs.AddCompetidorDialog;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    private DatabaseReference mCompRojoDB;
    private DatabaseReference mCompAzulDB;
    private DatabaseReference mListaCompetidoresDB;
    private List<Competidores> mListaComp;
    private ListView mListaCompView;
    private ListView mListViewPrueba;


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

        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Combates").child(idCat); // Los combates dependen de la categoría a la que pertenecen.

        mListaCompetidoresDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores"); // Referencia a la lista completa de Competidores de la BD.
        mListaComp = new ArrayList<>();
        mListaCompView = (ListView) findViewById(R.id.add_comp_dialog_listView);
        mListViewPrueba = (ListView) findViewById(R.id.add_comb_pruebaListView);

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
                filtrarCompetidores();
                //idRojo = abrirDialogo();
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

        // filtrarCompetidores();

        res = dialogo.idCompetidor;

        return res;
    }

    @Override
    public String getIdCompetidor(Competidores comp) {
        return comp.getId();
    }
    //endregion

    //region Filtrar Competidores
    // Método que devuelve una lista con los competidores de una modalidad y una categoría determinadas (para un Campeonato determinado).
    public void filtrarCompetidores(){

        // Toast.makeText(this, "Filtrando Competidores", Toast.LENGTH_SHORT).show();
        
        List<Competidores> lista = new ArrayList<>();
        // Los valores de los IDs para el Campeonato, Modalidad y Categoría actuales se han recuperado en el método onCreate de esta clase. idCamp, idMod e idCat.
        // Categoría -> mCatDB --> edadCat, pesoCat y sexoCat
        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    //final String catEdad = dataSnapshot.child(idMod).child(idCat).child("catEdad").getValue().toString();
                    //final String catPeso = dataSnapshot.child(idMod).child(idCat).child("catPeso").getValue().toString();
                    final String edad = dataSnapshot.child("edad").getValue().toString();
                    final String peso = dataSnapshot.child("peso").getValue().toString();
                    final String sexo = dataSnapshot.child("sexo").getValue().toString();

                    /*Toast.makeText(AddCombateActivity.this,
                            "Datos para la CONSULTA: Sexo --> " + sexo + " Edad --> " + edad + " Peso --> " + peso,
                            Toast.LENGTH_SHORT).show();*/

                    // Realizar la consulta en la lista de Competidores.
                    Query consulta = mListaCompetidoresDB
                            .orderByChild("id");

                    consulta.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            /*if(dataSnapshot.exists()){
                                Toast.makeText(AddCombateActivity.this, "La consulta devuelve algún resultado." + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                            }*/
                            mListaComp.clear();
                            for(DataSnapshot competidorSnapshot: dataSnapshot.getChildren()){ // Recorrer los competidores de la lista obtenida en la consulta.
                                Competidores comp = competidorSnapshot.getValue(Competidores.class);
                               /* Toast.makeText(AddCombateActivity.this, "DNI del Competidor -> " + comp.getDni(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(AddCombateActivity.this, "SEXO --> " + comp.getSexo(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(AddCombateActivity.this, "CAT EDAD --> " + comp.getCatEdad(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(AddCombateActivity.this, "CAT PESO --> " + comp.getCatPeso(), Toast.LENGTH_SHORT).show();*/
                                
                                /*if(comp.getSexo().equals(sexo))
                                    Toast.makeText(AddCombateActivity.this, "Sexo Coincide", Toast.LENGTH_SHORT).show();*/
                                
                                // Comprobación por sexo
                                if((comp.getSexo().equals(sexo)) &&
                                // Comprobaciones por edad y catEdad
                                        (comp.getCatEdad().equals(edad)) &&
                                // Comprobaciones por peso y catPeso
                                        (comp.getCatPeso().equals(peso))){
                                    // Si se cumplen los requisitos se añade al competidor a la lista de resultados.
                                    mListaComp.add(comp);
                                }
                                // Asignar el adapter para mostrar la lista en un Listview
                                CompetidoresList adapter = new CompetidoresList(AddCombateActivity.this, mListaComp);
                                // Si supiera el nombre del ListView lo pondría de esta manera
                                // nombreLisView.setAdapter(adapter);
                                // Como el ListView se crea en el Dialog debo obtener el nombre del ListView en AddCompetidorDialog.
                                // mListaCompView.setAdapter(adapter);
                                mListViewPrueba.setAdapter(adapter);
                                // Toast.makeText(AddCombateActivity.this, "Se ha asignado el ADAPTER a la ListView del Dialog", Toast.LENGTH_SHORT).show();
                            }
                            if (mListaComp.size() == 0){
                                Toast.makeText(AddCombateActivity.this,
                                        "No existe ningún competidor en la BD que cumpla esos requisitos. Sexo: " + sexo
                                        + ", CatEdad: " + edad + ", CatPeso: " + peso,
                                        Toast.LENGTH_SHORT).show();
                            }
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

        // Recuperar la lista de competidores de una categoría de peso


        // return lista;
    }
    //enregion

    //region Cargar Datos Competidores

    //endregion
}
