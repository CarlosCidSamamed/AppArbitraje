package com.fervenzagames.apparbitraje.Add_Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CompetidoresList;
import com.fervenzagames.apparbitraje.Dialogs.AddCompetidorDialog;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.R;
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
    private Button mRojoBorrarBtn;

    // Competidor Azul
    private CircleImageView mFotoAzul;
    private TextView mNombreAzul;
    private Button mAzulBtn;
    private Button mAzulBorrarBtn;

    //Número del Combate dentro de la categoría actual
    private TextInputLayout mNumCombateInput;

    private Button mGuardarBtn;

    // Referencias a la BD
    private DatabaseReference mCampDB;
    private DatabaseReference mModDB;
    private DatabaseReference mCatDB;
    private DatabaseReference mCombateDB;
    private DatabaseReference mCompRojoDB;
    private DatabaseReference mCompAzulDB;
    private DatabaseReference mCombatesCatDB;

    // Listas y ListViews para mostrar dichas Listas
    private DatabaseReference mListaCompetidoresDB;
    private List<Competidores> mListaComp;
    private ListView mListaCompView;
    private ListView mRojoListView;
    private ListView mAzulListView;



    private String idCamp;
    private String idMod;
    private String idCat;

    // Los identificadores que devuelve el dialogo en el que se añade un competidor. Este ID se obtiene de la DB y sirve para
    // poder localizar su nombre y apellidos en la DB y la imagen en el Storage de Firebase.
    private String idRojo;
    private String idAzul;
    private int lado;

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
        mRojoBorrarBtn = (Button) findViewById(R.id.add_comb_Rojo_Borrar_btn);
        mRojoBorrarBtn.setVisibility(View.INVISIBLE);

        mFotoAzul = (CircleImageView) findViewById(R.id.add_comb_Azul);
        mNombreAzul = (TextView) findViewById(R.id.add_comb_Azul_nombre);
        mAzulBtn = (Button) findViewById(R.id.add_comb_Azul_btn);
        mAzulBorrarBtn = (Button) findViewById(R.id.add_comb_Azul_Borrar_btn);
        mAzulBorrarBtn.setVisibility(View.INVISIBLE);

        mNumCombateInput = (TextInputLayout) findViewById(R.id.add_comb_numCombateInput);

        mGuardarBtn = (Button) findViewById(R.id.add_comb_guardar_btn);

        Intent intent = getIntent();
        idCamp = intent.getExtras().getString("idCamp");
        idMod = intent.getExtras().getString("idMod");
        idCat = intent.getExtras().getString("idCat");

        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp).child(idMod);
        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);
        mCombatesCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Combates").child(idCat); // Lista de Combates de la Categoría indicada.

        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Combates");

        mListaCompetidoresDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores"); // Referencia a la lista completa de Competidores de la BD.
        mListaComp = new ArrayList<>();
        mListaCompView = (ListView) findViewById(R.id.add_comp_dialog_listView);
        mRojoListView = (ListView) findViewById(R.id.add_comb_RojoListView);
        mAzulListView = (ListView) findViewById(R.id.add_comb_AzulListView);



        idRojo = "";
        idAzul = "";
        lado = 0;

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
                lado = 0;
                filtrarCompetidores(lado);
                //idRojo = abrirDialogo();

                if(mRojoListView != null) {
                    cargarDatosCompetidor(lado);
                    mRojoBorrarBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        mAzulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //idAzul = abrirDialogo();
                lado = 1;
                filtrarCompetidores(lado);

                if(mAzulListView != null) {
                    cargarDatosCompetidor(lado);
                    mAzulBorrarBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        //endregion
        //region Eliminar Competidores
        mRojoBorrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lado = 0;
                eliminarDatosCompetidores(lado);
                mRojoListView.setVisibility(View.INVISIBLE);
                //filtrarCompetidores(lado);
            }
        });

        mAzulBorrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lado = 1;
                eliminarDatosCompetidores(lado);
                mAzulListView.setVisibility(View.INVISIBLE);
                //filtrarCompetidores(lado);
            }
        });
        //endregion

        //region Guardar Datos
        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCombate();
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
    public void filtrarCompetidores(final int lado){

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
                                    // Ahora debemos comprobar que el competidor no se haya añadido a la otra ranura.
                                    // Es decir, no queremos que se pueda poner el mismo competidor en ambos lados, Rojo y Azul.
                                    if((lado == 0) && (idAzul != comp.getId())){
                                        // Se añade el competidor si el Id no es mismo que el del contrario.
                                        mListaComp.add(comp);
                                    } else if((lado == 1) && (idRojo != comp.getId())){
                                        mListaComp.add(comp);
                                    } // En caso contrario no se añade ese competidor a la lista de posibles candidatos.

                                }
                                // Asignar el adapter para mostrar la lista en un Listview
                                CompetidoresList adapter = new CompetidoresList(AddCombateActivity.this, mListaComp);
                                // Si supiera el nombre del ListView lo pondría de esta manera
                                // nombreLisView.setAdapter(adapter);
                                // Como el ListView se crea en el Dialog debo obtener el nombre del ListView en AddCompetidorDialog.
                                // mListaCompView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                if(lado == 0){
                                    // ROJO
                                    mRojoListView.setVisibility(View.VISIBLE);
                                    mRojoListView.setAdapter(adapter);
                                    idRojo = comp.getId();
                                } else if(lado == 1){
                                    mAzulListView.setVisibility(View.VISIBLE);
                                    mAzulListView.setAdapter(adapter);
                                    idAzul = comp.getId();
                                }
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
    public void cargarDatosCompetidor(int lado){
        if(lado == 0){
            mRojoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Obtener el competidor que se ha pulsado
                    Competidores comp = mListaComp.get(position);
                    Picasso.get().load(comp.getFoto()).into(mFotoRojo);
                    mNombreRojo.setText(comp.getNombre() + " " + comp.getApellido1() + " " + comp.getApellido2());
                    idRojo = comp.getId();
                    //Toast.makeText(AddCombateActivity.this, "Cargar Datos Competidor --> IdRojo: " + idRojo , Toast.LENGTH_SHORT).show();
                }
            });
        } else if(lado == 1){
            mAzulListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Obtener el competidor que se ha pulsado
                    Competidores comp = mListaComp.get(position);
                    Picasso.get().load(comp.getFoto()).into(mFotoAzul);
                    mNombreAzul.setText(comp.getNombre() + " " + comp.getApellido1() + " " + comp.getApellido2());
                    idAzul = comp.getId();
                    //Toast.makeText(AddCombateActivity.this, "Cargar Datos Competidor --> IdAzul: " + idAzul , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //endregion

    //region Eliminar Datos Competidores
    public void eliminarDatosCompetidores(int lado){
        if(lado == 0){
            Resources res = getResources();
            String str = res.getString(R.string.nombre_y_apellidos);
            mNombreRojo.setText(str);
            Picasso.get().load(R.drawable.default_avatar).into(mFotoRojo);
            idRojo = "";
            //Toast.makeText(this, "Eliminar Datos Competidor --> IdRojo: " + idRojo , Toast.LENGTH_SHORT).show();
        } else if(lado == 1){
            Resources res = getResources();
            String str = res.getString(R.string.nombre_y_apellidos);
            mNombreAzul.setText(str);
            Picasso.get().load(R.drawable.default_avatar).into(mFotoAzul);
            idAzul = "";
            //Toast.makeText(this, "Eliminar Datos Competidor --> IdAzul: " + idAzul , Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region Guardar Combate

    public void guardarCombate(){

        boolean datosOK = false;
        // Comprobar que se han añadido los competidores Rojo y Azul
        if(!TextUtils.isEmpty(idRojo) && (!TextUtils.isEmpty(idAzul))){
            datosOK = true;
        } else {
            Toast.makeText(this, "Introduzca a ambos competidores antes de pulsar el botón de Guardar Datos.", Toast.LENGTH_SHORT).show();
        }
        // Y el número de Combate se ha añadido y no existe en la BD para esta categoría.
        /*try {
            final String numCombate = mNumCombateInput.getEditText().getText().toString();
            if(!TextUtils.isEmpty(numCombate)){
                // Realizar una consulta a la BD para comprobar que no existe ningún combate con este número.
                Query consulta = mCombatesCatDB
                        .orderByChild("idCombate"); // Lista de los combates de esta categoría (ordenados por el idCombate).

                consulta.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String numero = "";
                        for(DataSnapshot combateSnapshot: dataSnapshot.getChildren()){ // Para cada uno de los combates
                            if(dataSnapshot.exists()){
                                try {
                                    numero = dataSnapshot.child("numCombate").getValue().toString();
                                    if(numero.equals(numCombate)){ // Ya existe un combate con ese número en la BD;
                                        Toast.makeText(AddCombateActivity.this,
                                                "Ya existe un combate con ese número para esta categoría en la BD. Revise el valor de Número de Combate...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                numero = "1"; // Si no existe ningún combate en la lista...
                            }

                        }

                        // Si no existe un combate con ese número en la BD se crea un objeto de tipo Combates
                        String idCombate = mCombatesCatDB.child(idCat).push().getKey();
                        // if(numero.equals("")) numero = "1";
                        Integer num = Integer.parseInt(numero);
                        int n = num;
                        Combates comb = new Combates(idCombate, n,
                               "ganador", "motivo","enlaceVideo" ,
                                idRojo, idAzul, null,
                                idMod, idCat, idCamp,
                                Combates.EstadoCombate.Pendiente);
                        // Y se añade a la BD.
                        mCombatesCatDB.child(idCat).child(idCombate).setValue(comb);
                        Toast.makeText(AddCombateActivity.this, "Se ha añadido el Combate a la BD.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "Introduzca el Número del Combate antes de pulsar el botón de Guardar Datos.", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/

    }

    //endregion
}
