package com.fervenzagames.apparbitraje;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

import static android.view.View.VISIBLE;

public class CombatesActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Spinner mCampSpinner;
    private Spinner mModSpinner;
    private Spinner mCatSpinner;
    private Spinner mEstadoSpinner;

    private TextView mModTitulo;
    private TextView mCatTitulo;
    private TextView mResultados;

    private Button mFiltroModBtn;
    private Button mFiltroCatBtn;
    private Button mFiltroEstadoBtn;
    private Button mAplicarFiltroBtn;

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

    private String mNombreCamp;
    private String mNombreMod;
    private String mNombreCat;
    private String mEstado;
    private String mIdCamp;
    private String mIdMod;
    private String mIdCat;

    private CombatesList mAdapter;

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

        mModTitulo = findViewById(R.id.combates_nombreMod);
        mCatTitulo = findViewById(R.id.combates_nombreCat);
        mResultados = findViewById(R.id.combates_resultados);

        mFiltroModBtn = findViewById(R.id.combates_add_flitroMod_btn);
        mFiltroCatBtn = findViewById(R.id.combates_add_filtroCat_btn);
        mFiltroEstadoBtn = findViewById(R.id.combates_add_filtroEstado_btn);
        mAplicarFiltroBtn = findViewById(R.id.combates_aplicar_filtro_btn);

        // mListaCombatesView = findViewById(R.id.combates_lista);

        mListaCombatesView =  findViewById(R.id.combates_lista);
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
        mCampsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos");
        mListaNombresCamps = new ArrayList<>();
        mListaMods = new ArrayList<>();
        mListaCats = new ArrayList<>();
        mListaCombates = new ArrayList<>();

        mNombreCamp = "";
        mNombreMod = "";
        mNombreCat = "";
        mEstado = "";
        mIdCamp = "";
        mIdMod = "";
        mIdCat = "";

        // Cargar los combates en la ExpandableList

        // Cargar los datos en los Spinners
        // Spinner Nombres Campeonatos
        // Cargar los datos de los nombres en una lista que después le pasaremos al Spinner

        cargarDatosCampeonatos();
        //region Spinners

        mCampSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNombreCamp = mCampSpinner.getSelectedItem().toString();
                Toast.makeText(CombatesActivity.this, "Nombre del Campeonato --> " + mNombreCamp, Toast.LENGTH_SHORT).show();
                // Almacenar el ID del Campeonato seleccionado.
                // Buscar Campeonato mediante nombre.
                Query consultaIdCamp = mCampsDB;
                consultaIdCamp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot campSnapshot: dataSnapshot.getChildren()){
                            Campeonatos camp = campSnapshot.getValue(Campeonatos.class);
                            if(camp.getNombre().equals(mNombreCamp)){
                                mIdCamp = camp.getIdCamp();
                                Toast.makeText(CombatesActivity.this, "ID Camp --> " + mIdCamp, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // cargarDatosModalidades(mNombreCamp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mModSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNombreMod = mModSpinner.getSelectedItem().toString();
                // Buscar Modalidad por nombre y almacenar su ID
                if(!mIdCamp.equals("")){
                    mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(mIdCamp);
                    Query consultaIdMod = mModsDB;
                    consultaIdMod.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                                Modalidades mod = modSnapshot.getValue(Modalidades.class);
                                if(mod.getNombre().equals(mNombreMod)){
                                    mIdMod = mod.getId();
                                    Toast.makeText(CombatesActivity.this, "ID Mod --> " + mIdMod, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // cargarDatosCategorias(nombreMod, mIdCamp);
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
                mNombreCat = mCatSpinner.getSelectedItem().toString();
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
                                if(cat.getNombre().equals(mNombreCat)){
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
                // cargarDatosCombates(nombreCat, estado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEstadoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEstado = mEstadoSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //endregion

        //region Botones
        mFiltroModBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModSpinner.setVisibility(VISIBLE);
                if(!mNombreCamp.equals("")){
                    cargarDatosModalidades(mNombreCamp);
                }
            }
        });

        mFiltroCatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCatSpinner.setVisibility(VISIBLE);
                if(!mIdMod.equals("")){
                    cargarDatosCategorias(mNombreMod, mIdCamp);
                }
            }
        });

        mFiltroEstadoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEstadoSpinner.setVisibility(VISIBLE);
            }
        });

        mAplicarFiltroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vaciar ListView.
                List<Combates> listaVacia = new ArrayList<>();
                CombatesList adapter = new CombatesList(CombatesActivity.this, listaVacia);
                mListaCombatesView.setAdapter(adapter);
                // Resultados = 0
                mResultados.setText("Resultados");
                // Aplicar filtro para mostrar los resultados correctos.
                aplicarFiltro(mNombreCamp, mNombreMod, mNombreCat, mEstado);
            }
        });
        //endregion



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
        Toast.makeText(this, "URL de la Consulta de Combates --> " + consulta.getRef().toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CombatesActivity.this, "ID del Combate : " + combate.getId(), Toast.LENGTH_SHORT).show();
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

    // Se obtienen los valores de los Spinners si están activos, es decir, si son visibles. A partir de esos valores de los Spinners se buscan los ID de las
    // entidades correspondientes y se realiza la búsqueda correspondiente en los combates.
    public void aplicarFiltro(String nombreCamp, String nombreMod, String nombreCat, String estado){

        Toast.makeText(this, "Nombre Camp --> " + mNombreCamp, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Nombre Mod  --> " + mNombreMod, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Nombre Cat  --> " + mNombreCat, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Estado      --> " + mEstado, Toast.LENGTH_SHORT).show();

        // El ID del Campeonato a partir del nombre que se obtiene del Spinner se ha almacenado en la variable mIdCamp
        final List<Combates> listaCombates = new ArrayList<>();
        final List<Modalidades> listaModalidades = new ArrayList<>();
        final List<Categorias> listaCategorias = new ArrayList<>();
        //region 1. Solo se pasa el nombre del Campeonato
        if((mNombreMod.equals("") && (mNombreCat.equals("")))){
            // Buscar los Combates cuyo campo campenato coincida con el ID del Campeonato deseado.
            // Como los Campeonatos dependen de una Categoría, si no se facilita la categoría deseada deberemos obtener una lista de categorías para ese campenato.
            // Para obtener las categorías deberemos obtener antes las modalidades.

            // Lista de Modalidades de este Campeonato
            mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(mIdCamp);
            Toast.makeText(this, "URL de la Consulta de Modalidades --> " + mModsDB.getRef().toString(), Toast.LENGTH_SHORT).show();
            Query consultaMods = mModsDB;
            consultaMods.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Toast.makeText(CombatesActivity.this, "No existen Modalidades en la Bd para ese Campeonato.", Toast.LENGTH_SHORT).show();
                    } else {
                        for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                            Modalidades mod = modSnapshot.getValue(Modalidades.class);
                            listaModalidades.add(mod);
                        }
                        Toast.makeText(getApplicationContext(), "La consulta de Modalidades devuelve " + listaModalidades.size() + " elementos.", Toast.LENGTH_SHORT).show();
                        // Mostrar en el título del Spinner de Modalidades el número de resultados que ha devuelto la consulta.
                        String m = "Modalidad ( " + listaModalidades.size() + " )";
                        mModTitulo.setText(m);
                        mModTitulo.setTypeface(null, Typeface.BOLD);
                        buscarCategorias(listaModalidades, listaCategorias);
                        // buscarCombates(listaCategorias, listaCombates);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //endregion
        } else if(mNombreCat.equals("")){        //region 2. Se pasa el nombre del Campeonato y el nombre de la Modalidad
            // Obtener el ID de la Modalidad a partir del nombre
            mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(mIdCamp);
            Query consultaIdMod = mModsDB;
            consultaIdMod.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Toast.makeText(CombatesActivity.this, "No existe ninguna Modalidad en la BD para ese Campeonato.", Toast.LENGTH_SHORT).show();
                    } else {
                        for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                            Modalidades mod = modSnapshot.getValue(Modalidades.class);
                            if(mod.getNombre().equals(mNombreMod)){
                                mIdMod = mod.getId();
                            }
                        }
                        // Consulta de Categorías para obtener la lista correspondiente.
                        mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(mIdMod);
                        Query consultaMods = mCatsDB;
                        consultaMods.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    Toast.makeText(CombatesActivity.this, "No existen Categorías para la Modalidad cuyo ID es " + mIdMod, Toast.LENGTH_SHORT).show();
                                } else {
                                    for(DataSnapshot catSnapshot: dataSnapshot.getChildren()){
                                        Categorias cat = catSnapshot.getValue(Categorias.class);
                                        listaCategorias.add(cat);
                                    }
                                    Toast.makeText(CombatesActivity.this, "La consulta de Categorías devuelve " + listaCategorias.size() + " elementos." , Toast.LENGTH_SHORT).show();
                                    // Mostrar en el título del Spinner de Categorías el número de resultados que ha devuelto la consulta.
                                    String c = "Categorías ( " + listaCategorias.size() + " )";
                                    mCatTitulo.setTypeface(null, Typeface.BOLD);
                                    mCatTitulo.setText(c);
                                    buscarCombates(listaCategorias, listaCombates);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //endregion
        } else { //region 3. Se pasan los nombres del Campeonato, Modalidad y Categoría.
            buscarCombatesIdCat(mIdCat, listaCombates);
            //endregion
        }



        // 4. Se pasan los nombres del Campeonato, Modalidad y Categoría además del Estado del Combate.
    }

    public void buscarCategorias(List<Modalidades> listaModalidades, final List<Categorias> listaCategorias){
        if(listaModalidades.size() > 0) { // Si no hay modalidades no se recorre la lista
            for (int i = 0; i < listaModalidades.size(); i++) {
                final String idMod = listaModalidades.get(i).getId();
                // Obtener la referencia a la BD
                mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(idMod);
                // Obtener datos de Categorías
                Query consultaCats = mCatsDB;
                consultaCats.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(CombatesActivity.this, "No existen Categorías en la BD para la Modalidad cuyo ID es " + idMod, Toast.LENGTH_SHORT).show();
                        } else {
                            for (DataSnapshot catSnapshot : dataSnapshot.getChildren()) {
                                Categorias cat = catSnapshot.getValue(Categorias.class);
                                // Añadir las Categorías a la lista
                                listaCategorias.add(cat);
                            }
                            Toast.makeText(getApplicationContext(), "La consulta de Categorías devuelve " + listaCategorias.size() + " elementos.", Toast.LENGTH_SHORT).show();
                            buscarCombates(listaCategorias, mListaCombates);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void buscarCombates(final List<Categorias> listaCategorias, final List<Combates> listaCombates){
        if(listaCategorias.size() > 0){
            for(int i = 0; i < listaCategorias.size(); i++){
                final String idCat = listaCategorias.get(i).getId();
                Query consultaCombates = mCombatesDB.child(idCat);
                consultaCombates.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Toast.makeText(CombatesActivity.this,
                                    "No existen Combates en la BD para la Categoría cuyo ID es " + idCat,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            listaCombates.clear();
                            for(DataSnapshot combSnapshot: dataSnapshot.getChildren()){
                                Combates comb = combSnapshot.getValue(Combates.class);
                                listaCombates.add(comb);
                                Toast.makeText(getApplicationContext(),
                                        "La consulta de Combates devuelve " + listaCombates.size() + " elementos.",
                                        Toast.LENGTH_SHORT).show();
                                // Mostrar en el título del ListView de Combates el número de resultados que ha devuelto la consulta.
                                String res = "Resultados ( " + listaCombates.size() + " )";
                                mResultados.setText(res);
                                cargarCombates(listaCombates);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void buscarCombatesIdCat(String idCat, final List<Combates> listaCombates){
        Query consultaCombates = mCombatesDB.child(mIdCat);
        consultaCombates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(CombatesActivity.this, "No existen Combates que cumplan esos requisitos.", Toast.LENGTH_SHORT).show();
                } else {
                    listaCombates.clear();
                    for(DataSnapshot combSnapshot: dataSnapshot.getChildren()){
                        Combates comb = combSnapshot.getValue(Combates.class);
                        listaCombates.add(comb);
                        cargarCombates(listaCombates);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void cargarCombates(List<Combates> listaCombates){
        // Crear Adapter
        mAdapter = new CombatesList(this, listaCombates);
        // Asignar Adapter a ListView
        mListaCombatesView.setAdapter(mAdapter);
    }
}
