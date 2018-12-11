package com.fervenzagames.apparbitraje;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CombatesList;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.Models.Categorias;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CombatesActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Spinner mCampSpinner;
    private Spinner mModSpinner;
    private Spinner mCatSpinner;
    private Spinner mEstadoSpinner;

    // private ExpandableListView mListaCombatesView;
    private ListView mListaCombatesView;
    private DatabaseReference mCombatesDB;
    private List<Combates> mListaCombates;

    private DatabaseReference mCampsDB;
    private List<String> mListaNombresCamps;

    private DatabaseReference mCampeonatoDB;

    private DatabaseReference mModsDB;
    private List<String> mListaMods;

    private DatabaseReference mCatsDB;
    private List<String> mListaCats;

    private String mIdCamp;
    private String mIdMod;
    private String mIdCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combates);

        mToolbar = findViewById(R.id.combates_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Listado de Combates");

        mCampSpinner = findViewById(R.id.combates_nombreCampSpinner);
        mModSpinner = findViewById(R.id.combates_nombreModSpinner);
        mCatSpinner = findViewById(R.id.combates_nombreCatSpinner);
        mEstadoSpinner = findViewById(R.id.combates_estadoSpinner);

        // mListaCombatesView = findViewById(R.id.combates_lista);

        mListaCombatesView =  findViewById(R.id.combates_lista);
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
        mCampsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos");
        mListaNombresCamps = new ArrayList<>();
        mListaMods = new ArrayList<>();
        mListaCats = new ArrayList<>();
        mListaCombates = new ArrayList<>();

        mIdCamp = "";
        mIdMod = "";
        mIdCat = "";

        // Cargar los combates en la ExpandableList

        // Cargar los datos en los Spinners
        // Spinner Nombres Campeonatos
        // Cargar los datos de los nombres en una lista que después le pasaremos al Spinner
        cargarDatosCampeonatos();

        mCampSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String nombreCamp = mCampSpinner.getSelectedItem().toString();
                Toast.makeText(CombatesActivity.this, "Nombre del Campeonato --> " + nombreCamp, Toast.LENGTH_SHORT).show();
                // Almacenar el ID del Campeonato seleccionado.
                // Buscar Campeonato mediante nombre.
                Query consultaIdCamp = mCampsDB;
                consultaIdCamp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot campSnapshot: dataSnapshot.getChildren()){
                            Campeonatos camp = campSnapshot.getValue(Campeonatos.class);
                            if(camp.getNombre().equals(nombreCamp)){
                                mIdCamp = camp.getIdCamp();
                                Toast.makeText(CombatesActivity.this, "ID Camp --> " + mIdCamp, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                cargarDatosModalidades(nombreCamp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mModSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String nombreMod = mModSpinner.getSelectedItem().toString();
                // Buscar Modalidad por nombre y almacenar su ID
                if(!mIdCamp.equals("")){
                    mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(mIdCamp);
                    Query consultaIdMod = mModsDB;
                    consultaIdMod.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                                Modalidades mod = modSnapshot.getValue(Modalidades.class);
                                if(mod.getNombre().equals(nombreMod)){
                                    mIdMod = mod.getId();
                                    Toast.makeText(CombatesActivity.this, "ID Mod --> " + mIdMod, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    cargarDatosCategorias(nombreMod, mIdCamp);
                } else {
                    Toast.makeText(CombatesActivity.this, "Seleccione el Campeonato antes que la Modalidad.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Una vez se han seleccionado el Campeonato, la Modalidad y la Categoría se actualizará la lista de Combates que cumplan con las condiciones
        mCatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mListaCombates.clear();
                final String nombreCat = mCatSpinner.getSelectedItem().toString();
                String estado = mEstadoSpinner.getSelectedItem().toString();
                // Buscar la Categoría mediante nombre y almacenar su ID
                if(!mIdMod.equals("")){
                    mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(mIdMod);
                    Query consultaIdCat = mCatsDB;
                    consultaIdCat.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot catSnapshot: dataSnapshot.getChildren()){
                                Categorias cat = catSnapshot.getValue(Categorias.class);
                                if(cat.getNombre().equals(nombreCat)){
                                    mIdCat = cat.getId();
                                    Toast.makeText(CombatesActivity.this, "ID Cat --> " + mIdCat, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(CombatesActivity.this, "Seleccione la Modalidad antes que la Categoría.", Toast.LENGTH_SHORT).show();
                }
                cargarDatosCombates(nombreCat, estado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void cargarDatosCampeonatos(){

        final ArrayAdapter<CharSequence> adapterCamps = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mListaNombresCamps);

        mCampsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot campSnapshot:dataSnapshot.getChildren()){
                    Campeonatos camp = campSnapshot.getValue(Campeonatos.class);

                    try {
                        mListaNombresCamps.add(camp.getNombre());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                // Toast.makeText(CombatesActivity.this, "El Spinner de Campeonatos tiene " + mListaNombresCamps.size() + " elementos.", Toast.LENGTH_SHORT).show();
                adapterCamps.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // ArrayAdapter adapterCamps = new MiSpinnerAdapter(this, mListaNombresCamps);
        // adapterCamps.setDropDownViewResource(R.layout.spinner_single_textview_dropdown_item);

        mCampSpinner.setAdapter(adapterCamps);
    }

    // El Spinner de Modalidad dependerá del valor seleccionado en el Spinner del nombre del Campeonato. Es decir, se cargarán las modalidades del Campeonato seleccionado.
    // El parámetro de este método será el nombre del Campeonato seleccionado en dicho Spinner.
    private void cargarDatosModalidades(final String nombreCamp){

        final ArrayAdapter<String> modsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mListaMods);

        // Se hará una búsqueda mediante el nombre del Campeonato para obtener su ID. Dicho ID se usará para localizar sus Modalidades.
        Query consulta = mCampsDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(CombatesActivity.this, "No existe ningún Campeonato con ese Nombre", Toast.LENGTH_SHORT).show();
                } else {
                    for(DataSnapshot campSnapshot: dataSnapshot.getChildren()){
                        Campeonatos camp = campSnapshot.getValue(Campeonatos.class);
                        try {
                            if(camp.getNombre().equals(nombreCamp)){
                                String idCamp = camp.getIdCamp();
                                // mIdCamp = idCamp;
                                // Toast.makeText(CombatesActivity.this, "ID del Campeonato --> " + idCamp, Toast.LENGTH_SHORT).show();
                                // Localizar sus Modalidades
                                mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(idCamp);
                                Query consultaMods = mModsDB;
                                consultaMods.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        mListaMods.clear(); // Limpiar la lista de Modalidades para que solo se muestren las Modalidades del Campeonato seleccionado
                                        for(DataSnapshot modSnapshot:dataSnapshot.getChildren()){
                                            Modalidades mod = modSnapshot.getValue(Modalidades.class);
                                            mListaMods.add(mod.getNombre());
                                        }
                                        modsAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mModSpinner.setAdapter(modsAdapter);
    }

    // El Spinner de Categorías depende de la Modalidad y del Campenato seleccionados. Un parámetro será el nombre de la Modalidad seleccionada en el Spinner correspondiente.
    // El otro parámetro será el ID del Campeonato seleccionado en el Spinner correspondiente.
    private void cargarDatosCategorias(final String nombreMod, String idCamp){

        final ArrayAdapter<String> catsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item ,mListaCats);

        mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(idCamp);
        Query consulta = mModsDB;
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                    Modalidades mod = modSnapshot.getValue(Modalidades.class);
                    if(mod.getNombre().equals(nombreMod)){
                        String idMod = mod.getId();
                        // mIdMod = idMod;
                        mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(idMod);
                        Query consultaCats = mCatsDB;
                        consultaCats.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mListaCats.clear();
                                for(DataSnapshot catSnapshot: dataSnapshot.getChildren()){
                                    Categorias cat = catSnapshot.getValue(Categorias.class);
                                    mListaCats.add(cat.getNombre());
                                }
                                catsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mCatSpinner.setAdapter(catsAdapter);

    }

    // Como el ID de la Categoría depende del ID de la Modalidad y este, a su vez, depende del ID del Campeonato si se facilita el ID de la Categoría podremos saber que estamos
    // trabajando con el Campeonato y la Modalidad correctos. El estado del combate servirá para que el filtro sea aún más preciso.
    // Mediante el nombre de la categoría obtendremos su ID.
    private void cargarDatosCombates(final String nombreCat, final String estado){
        // Adapter
        final CombatesList adapter = new CombatesList(this, mListaCombates);
        // Actualizar datos adapter
        // Localizar Combates que cumplan filtro
        /*// Obtener el ID de la Categoría a partir de su nombre
        mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(mIdMod);
        // Toast.makeText(this, "ID MOD --> " + mIdMod, Toast.LENGTH_SHORT).show();
        mCatsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot catSnapshot: dataSnapshot.getChildren()){
                        Categorias cat = catSnapshot.getValue(Categorias.class);
                        if(cat.getNombre().equals(nombreCat)){
                            String idCat = cat.getId();
                            // mIdCat = idCat;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        Toast.makeText(this, "Nombre CAT --> " + nombreCat, Toast.LENGTH_SHORT).show();
        // Toast.makeText(this, "ID CAT -->" + mIdCat, Toast.LENGTH_SHORT).show();

        Query consulta = mCombatesDB.child(mIdCat).orderByChild("id"); // Lista de Combates de esta Categoría, Modalidad y Campeonato.
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaCombates.clear(); // Limpiar la lista de combates a mostrar en el ListView.
                if(!dataSnapshot.exists()){
                    Toast.makeText(CombatesActivity.this, "No existen Combates en la BD que cumplan esas condiciones. Revise los datos...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CombatesActivity.this, "Se han encontrado " + dataSnapshot.getChildrenCount() + " resultados para esa consulta.", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot combateSnapshot: dataSnapshot.getChildren()){
                        Combates combate = combateSnapshot.getValue(Combates.class);
                        mListaCombates.add(combate);
                        /*try {
                            String estado = combate.estadoToString(combate.getEstadoCombate());
                            Toast.makeText(CombatesActivity.this, "Estado --> " + estado, Toast.LENGTH_SHORT).show();
                            // Vamos a filtrar por estado
                            switch (estado){
                                case "Pendiente":{
                                    mListaCombates.add(combate);
                                    break;
                                }
                                case "Finalizado":{
                                    mListaCombates.add(combate);
                                    break;
                                }
                                case "Cancelado":{
                                    mListaCombates.add(combate);
                                    break;
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }*/
                    }
                    Toast.makeText(CombatesActivity.this, "Tamaño de la lista de Combates --> " + mListaCombates.size(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Configurar adapter listView
        mListaCombatesView.setAdapter(adapter);
    }
}
