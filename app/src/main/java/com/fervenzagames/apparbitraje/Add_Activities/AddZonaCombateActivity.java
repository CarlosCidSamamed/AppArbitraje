package com.fervenzagames.apparbitraje.Add_Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CombatesExpandableListAdapter;
import com.fervenzagames.apparbitraje.Dialogs.DetalleCombateDialog;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.Models.Categorias;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddZonaCombateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNumZona;
    private TextView mNombreCampeonato;
    private ExpandableListView mListaCombatesDisponiblesView;
    private ExpandableListView mListaCombatesView;
    private Button mAsignarBtn;
    private Button mRevertirBtn;
    private Button mGuardarBtn;

    private String idCamp;
    private DatabaseReference mCampDB;

    private DatabaseReference mCombatesDB;
    private DatabaseReference mModsDB;
    private DatabaseReference mCatsDB;

    private List<Combates> mListaCombatesDisponibles;
    private List<Combates> mListaCombates;

    private List<Modalidades> mListaMods;
    private List<Categorias> mListaCats;

    private List<String> mListaTitulos; // Nombre de los Combates
    // private HashMap<String, List<String>> mListaDetalle; // Lista con el estado de los Combates
    private List<String> mListaDetalle;
    private List<String> mListaIDs;
    private List<String> mListaIDsCat;

    private List<String> mListaTitulosDisp;
    private List<String> mListaDetalleDisp;
    private List<String> mListaIDsDisp;
    private List<String> mListaIDsCatDisp;

    private CombatesExpandableListAdapter mListaCombatesAdapter;
    private CombatesExpandableListAdapter mListaCombatesDispAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_zona_combate);

        mToolbar = findViewById(R.id.add_zona_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Asignar Combates a la Zona");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNumZona = findViewById(R.id.add_zona_numero);
        mNombreCampeonato = findViewById(R.id.add_zona_nombreCamp);
        mListaCombatesDisponiblesView = findViewById(R.id.add_zona_listaCombates);
        mListaCombatesView = findViewById(R.id.add_zona_listaCombates2);
        mAsignarBtn = findViewById(R.id.add_zona_addBtn);
        mRevertirBtn = findViewById(R.id.add_zona_removeBtn);
        mGuardarBtn = findViewById(R.id.add_zona_guardarBtn);

        Bundle extras = getIntent().getExtras();
        idCamp = extras.getString("idCamp");

        mListaCombatesDisponibles = new ArrayList<>();
        mListaCombates = new ArrayList<>();
        mListaMods = new ArrayList<>();
        mListaCats = new ArrayList<>();
        mListaIDsDisp = new ArrayList<>();
        mListaIDs = new ArrayList<>();

        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos").child(idCamp);
        // mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
        mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(idCamp);

        mCampDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Campeonatos camp = dataSnapshot.getValue(Campeonatos.class);

                try {
                    String nombre = camp.getNombre();
                    mNombreCampeonato.setText(nombre);

                    int numero = 0;
                    if(camp.getListaZonasCombate() != null){
                        numero = camp.getListaZonasCombate().size() + 1;
                    } else {
                        numero = 1;
                    }

                    String n = "Zona de Combate : " + String.valueOf(numero);
                    mNumZona.setTextColor(Color.BLUE);
                    mNumZona.setText(n);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                obtenerCombatesCampeonato("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Dado el idCamp (de los extras) se obtienen sus modalidades y categorías. Se recorren las Categorías y se obtienen los combates de ese Campeonato.
    // Si idZona == null --> Lista Combates Disponibles
    // Si idZona != null --> Lista Combates de esa Zona
    private void obtenerCombatesCampeonato(final String idZona){

        // Obtener las Modalidades
        Query consultaMods = mModsDB;

        consultaMods.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mListaMods.clear();
                    for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                        Modalidades mod = modSnapshot.getValue(Modalidades.class);
                        mListaMods.add(mod);
                    }
                    // Obtener las Categorías
                    // Recorrer la lista de Modalidades
                    mListaCats.clear();
                    for(int i = 0; i < mListaMods.size(); i++){
                        String idMod = mListaMods.get(i).getId();
                        mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(idMod);
                        mCatsDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot catSnapshot: dataSnapshot.getChildren()){
                                        Categorias cat = catSnapshot.getValue(Categorias.class);
                                        mListaCats.add(cat);
                                    }
                                    // Obtener los Combates
                                    // Recorrer la lista de Categorías
                                    mListaCombates.clear();
                                    for(int i = 0; i < mListaCats.size(); i++){
                                        String idCat = mListaCats.get(i).getId();
                                        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCat);
                                        mCombatesDB.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        for(DataSnapshot combSnapshot: dataSnapshot.getChildren()){
                                                            Combates comb = combSnapshot.getValue(Combates.class);
                                                            // Comprobar idZona
                                                            if(idZona.equals("")){ // Combates Disponibles
                                                                mListaCombatesDisponibles.add(comb);
                                                            } else if(idZona.equals(comb.getIdZonaCombate())){ // Combates de la Zona deseada
                                                                mListaCombates.add(comb);
                                                            }
                                                        }
                                                        if(mListaCombatesDisponibles.size() == 0){
                                                            Toast.makeText(AddZonaCombateActivity.this, "Lista de Combates Disponibles VACÍA.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(AddZonaCombateActivity.this, "Lista de Combates Disponibles --> " + mListaCombatesDisponibles.size() + "elementos.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        if(mListaCombates.size() == 0){
                                                            Toast.makeText(AddZonaCombateActivity.this, "Lista de Combates de la Zona VACÍA.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(AddZonaCombateActivity.this, "Lista de Combates ZONA --> " + mListaCombatesDisponibles.size() + "elementos.", Toast.LENGTH_SHORT).show();
                                                        }

                                                        // Procesar datos para mostrarlos en los ExpandableListViews
                                                        procesarDatosLista(mListaCombatesDisponibles, true);
                                                        procesarDatosLista(mListaCombates, false);
                                                        // Cargar datos en los ExpandableListViews
                                                        cargarCombates(mListaCombatesDisponiblesView, mListaTitulosDisp, mListaDetalleDisp,true);
                                                        cargarCombates(mListaCombatesView, mListaTitulos, mListaDetalle, false);

                                                        // Método para el Click en uno de los hijos de la lista de Combates Disponibles para Asignar a una Zona de Combate.
                                                        mListaCombatesDisponiblesView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                                            @Override
                                                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                                                mListaCombatesDisponibles.clear();
                                                                if(mListaTitulosDisp.size() > 0){
                                                                    String numCombate = mListaTitulosDisp.get(groupPosition);
                                                                    Toast.makeText(AddZonaCombateActivity.this, "NumCombate " + numCombate, Toast.LENGTH_SHORT).show();
                                                                    // Capturar el valor del ID del Combate seleccionado
                                                                    String idCombate = mListaIDsDisp.get(groupPosition);
                                                                    // Capturar el valor del ID de la Categoría del Combate seleccionado.
                                                                    String idCat = mListaIDsCatDisp.get(groupPosition);
                                                                    // Abrir el Dialog con el detalle del Combate.
                                                                    abrirDialog(idCombate, idCat);
                                                                } else {
                                                                    Toast.makeText(AddZonaCombateActivity.this, "En este momemto no existen combates disponibles...", Toast.LENGTH_SHORT).show();
                                                                }
                                                                return false;
                                                            }
                                                        });
                                                    } else {
                                                        // Advertencia
                                                    }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } else {
                                    // Advertencia
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    // Advertencia
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Método para procesar los datos de las List<Combates> y obtener la lista de headers y details para los ExpandableListViews.
    // Disp es un boolean que sirve para indicar si vamos a procesar la lista de Combates disponnibles o no.
    private void procesarDatosLista(List<Combates> lista, boolean disp){

        List<String> titulos = new ArrayList<>();
        List<String> detalle = new ArrayList<>();
        List<String> listaIDs = new ArrayList<>();
        List<String> listaIDsCategoria = new ArrayList<>();

        for(int i = 0; i < lista.size(); i++){ // Recorrer la lista
            // Obtener el número del combate (Título)
            titulos.add(lista.get(i).getNumCombate());
            // Obtener el estado del combate (Detalle)
            detalle.add(lista.get(i).getEstadoCombate().toString());
            // Obtener el ID del Combate para poder pasárselo al Dialog del Detalle
            listaIDs.add(lista.get(i).getId());
            // Para poder buscar un combate determinado necesstamos el ID de la Categoría
            listaIDsCategoria.add(lista.get(i).getCategoria());
        }

        if(disp == false){
            mListaTitulos = titulos;
            mListaDetalle = detalle;
            mListaIDs = listaIDs;
            mListaIDsCat = listaIDsCategoria;

        } else {
            mListaTitulosDisp = titulos;
            mListaDetalleDisp = detalle;
            mListaIDsDisp = listaIDs;
            mListaIDsCatDisp = listaIDsCategoria;
        }

    }

    // Se encarga de cargar los datos en el ExpandableListView correspondiente
    private void cargarCombates(ExpandableListView view, List<String> titulos, List<String> detalle, boolean disp){

        if(disp == false) {
            mListaCombatesAdapter = new CombatesExpandableListAdapter(AddZonaCombateActivity.this, mListaTitulos, mListaDetalle);
            view.setAdapter(mListaCombatesAdapter);
            mListaCombatesAdapter.notifyDataSetChanged();

        } else {
            mListaCombatesDispAdapter = new CombatesExpandableListAdapter(AddZonaCombateActivity.this, mListaTitulosDisp, mListaDetalleDisp);
            view.setAdapter(mListaCombatesDispAdapter);
            mListaCombatesDispAdapter.notifyDataSetChanged();
        }

    }

    public void abrirDialog(String idCombate, String idCategoria){
        DetalleCombateDialog dialog = new DetalleCombateDialog();
        Bundle extras = new Bundle(); // En este bundle le pasamos el ID del Combate al dialog que muestra el detalle de ese combate.
        extras.putString("idCombate", idCombate);
        extras.putString("idCategoria", idCategoria);
        dialog.setArguments(extras);
        dialog.show(getSupportFragmentManager(), "combate dialog");
    }

}
