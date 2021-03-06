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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CategoriasList;
import com.fervenzagames.apparbitraje.Add_Activities.AddCategoriaActivity;
import com.fervenzagames.apparbitraje.Models.Categorias;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalleModalidadActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mNombreCamp;
    private String idCamp;
    private String idMod;
    private DatabaseReference modDB;
    private DatabaseReference campDB;
    private TextView modNombre;
    private TextView modDesc;
    private ListView mListaCatView;
    private Button mAddCatBtn;

    private String nombreCamp;

    private DatabaseReference catsDB; // Referencia a las CATEGORÍAS de esta Modalidad
    private List<Categorias> mCatList;
    private CategoriasList mCatListAdapter;

    private Spinner mOrdenarSpinner;

    private Query catsOrdenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_modalidad);

        nombreCamp = "";

        mNombreCamp = (TextView) findViewById(R.id.mod_detalle_nombreCamp);

        mToolbar = (Toolbar) findViewById(R.id.mod_detalle_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Modalidad");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Deshabilitar la flecha atrás en la barra de título.

        modNombre = (TextView) findViewById(R.id.mod_detalle_nombre);
        modDesc = (TextView) findViewById(R.id.mod_detalle_desc);
        mListaCatView = (ListView) findViewById(R.id.mod_detalle_listCat);
        mAddCatBtn = (Button) findViewById(R.id.mod_detalle_addCat_btn);

        mOrdenarSpinner = (Spinner) findViewById(R.id.mod_detalle_ordenarSpinner);
/*
        idCamp = getIntent().getStringExtra("idCamp");
        idMod = getIntent().getStringExtra("idMod");*/

        // Vamos a recuperar idCamp e idMod del Bundle del Extra del Intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idCamp = extras.getString("idCamp");
        idMod = extras.getString("idMod");
/*
        Toast.makeText(DetalleModalidadActivity.this, "PUT EXTRA idCamp --> " + idCamp, Toast.LENGTH_LONG).show();
        Toast.makeText(DetalleModalidadActivity.this, "PUT EXTRA idMod --> " + idMod, Toast.LENGTH_LONG).show();*/

        modDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp).child(idMod); // Referencia a esta Modalidad.
        campDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);

        catsDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod);
        mListaCatView = (ListView) findViewById(R.id.mod_detalle_listCat);
        mCatList = new ArrayList<>();
        mCatListAdapter = new CategoriasList(DetalleModalidadActivity.this, mCatList);

        catsOrdenadas = catsDB
            .orderByChild("nombre");

        campDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreCamp = dataSnapshot.child("nombre").getValue().toString();
                mNombreCamp.setText(nombreCamp);
                // mToolbar.setTitle("Detalle Modalidad ( " + nombreCamp + " )");
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

        // Actualizar Lista Categorías y gestionar el click sobre una categoría
        mListaCatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCatListAdapter.notifyDataSetChanged(); // Notificar actualización de Datos

                // Se captura el elemento seleccionado
                Categorias cat = mCatList.get(position);
                // Se crea un nuevo Intent
                Intent detalleCartIntent = new Intent(DetalleModalidadActivity.this, DetalleCategoriaActivity.class);
                String idMod = getIntent().getExtras().getString("idMod");
                String idCat = cat.getId();
                Bundle extras = new Bundle();
                extras.putString("idCamp",idCamp);
                extras.putString("idMod", idMod);
                extras.putString("idCat", idCat);
                detalleCartIntent.putExtras(extras);
                startActivity(detalleCartIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        modDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCatList.clear();
                String idMod = getIntent().getExtras().getString("idMod");
                // Localizar las Categorías de esta Modalidad
                catsOrdenadas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mCatList.clear();
                        for(DataSnapshot catsSnapshot : dataSnapshot.getChildren()){ // Recorremos las Categorías de esta Modalidad
                            Categorias cat = catsSnapshot.getValue(Categorias.class);
                            mCatList.add(cat);
                        }
                        CategoriasList adapter = new CategoriasList(DetalleModalidadActivity.this, mCatList);
                        mListaCatView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Lista de Categorías de esta Modalidad

        // Dependiendo del valor seleccionado en el Spinner se ordenarán las categorías según el campo indicado.
        mOrdenarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catsOrdenadas = actualizarListaCategorias(mOrdenarSpinner.getSelectedItem().toString().toLowerCase());
                /*Toast.makeText(DetalleModalidadActivity.this,
                        "Spinner Filtro --> " + mOrdenarSpinner.getSelectedItem().toString().toLowerCase(),
                        Toast.LENGTH_SHORT).show();*/
                catsOrdenadas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mCatList.clear();
                        for(DataSnapshot catsSnapshot : dataSnapshot.getChildren()){ // Recorremos las Categorías de esta Modalidad
                            Categorias cat = catsSnapshot.getValue(Categorias.class);
                            mCatList.add(cat);
                        }
                        CategoriasList adapter = new CategoriasList(DetalleModalidadActivity.this, mCatList);
                        mListaCatView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*catsOrdenadas = actualizarListaCategorias(mOrdenarSpinner.getSelectedItem().toString());
        mCatListAdapter.notifyDataSetChanged();*/

        catsOrdenadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCatList.clear();

                //mCatListAdapter.notifyDataSetChanged();

                for(DataSnapshot catsSnapshot : dataSnapshot.getChildren()){ // Recorremos las Categorías de esta Modalidad
                    Categorias cat = catsSnapshot.getValue(Categorias.class);
                    mCatList.add(cat);
                }
                CategoriasList adapter = new CategoriasList(DetalleModalidadActivity.this, mCatList);
                mListaCatView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Query actualizarListaCategorias(String filtro){

        Query catsActualizadas = catsDB
                .orderByChild(filtro);

        mCatListAdapter.notifyDataSetChanged();

/*        catsActualizadas.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { // Se usa cuando se trabaja con datos ORDENADOS
                // Extraer los resultados de la consulta y pasarlos a un List
                List<Categorias> lista = new ArrayList<>();
                for(DataSnapshot catSnapshot:dataSnapshot.getChildren()){
                    Categorias cat = catSnapshot.getValue(Categorias.class);
                    lista.add(cat);
                }
                mCatListAdapter.actualizarCategorias(lista);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        return catsActualizadas;
    }
}
