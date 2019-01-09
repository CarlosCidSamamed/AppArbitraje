package com.fervenzagames.apparbitraje.Add_Activities;

import android.content.Intent;
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
import com.fervenzagames.apparbitraje.Models.DatosExtraZonasCombate;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.fervenzagames.apparbitraje.Models.ZonasCombate;
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
import java.util.Map;

public class AddZonaCombateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNumZona;
    private TextView mNombreCampeonato;
    private ExpandableListView mListaCombatesDisponiblesView;
    private ExpandableListView mListaCombatesView;
    private Button mAsignarBtn;
    private Button mRevertirBtn;
    private Button mGuardarBtn;

    private String mIdCamp;
    private DatabaseReference mCampDB;

    private String mIdZona;

    private DatabaseReference mCombatesDB;
    private DatabaseReference mModsDB;
    private DatabaseReference mCatsDB;
    private DatabaseReference mZonaCombateBD;
    private DatabaseReference mZonasDB;

    private List<Combates> mListaCombatesDisponibles;
    private List<Combates> mListaCombates;

    private List<Modalidades> mListaMods;
    private List<Categorias> mListaCats;

    private List<String> mListaTitulos; // Número de los Combates
    // private HashMap<String, List<String>> mListaDetalle; // Lista con el estado de los Combates
    private List<String> mListaDetalle;
    private List<String> mListaIDs;
    private List<String> mListaIDsCat;
    private List<String> mListaIDsMod;

    private List<String> mListaTitulosDisp;
    private List<String> mListaDetalleDisp;
    private List<String> mListaIDsDisp;
    private List<String> mListaIDsCatDisp;
    private List<String> mListaIDsModDisp;

    private CombatesExpandableListAdapter mListaCombatesAdapter;
    private CombatesExpandableListAdapter mListaCombatesDispAdapter;

    private String mIdCombateSeleccionado;
    private Combates mCombateSeleccionado;
    private ZonasCombate mZonaSeleccionada;
    private DatosExtraZonasCombate mDatosZona;

    private Campeonatos mCampeonatoActual;
    private List<ZonasCombate> mListaActualizada;

    private boolean mDatosExisten;

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
        mIdCamp = extras.getString("idCamp");
        mIdZona = "";

        mListaCombatesDisponibles = new ArrayList<>();
        mListaCombates = new ArrayList<>();
        mListaMods = new ArrayList<>();
        mListaCats = new ArrayList<>();
        mListaIDsDisp = new ArrayList<>();
        mListaIDs = new ArrayList<>();

        mIdCombateSeleccionado = "";
        mCombateSeleccionado = new Combates();

        mDatosExisten = false;

        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos").child(mIdCamp);
        // mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
        mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(mIdCamp);
        mZonasDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate").child(mIdCamp);

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

                    insertarDatosZona(mIdCamp, String.valueOf(numero));


                } catch (Exception e) {
                    e.printStackTrace();
                }

                obtenerCombatesCampeonato("");
                if(!mIdZona.equals("")){
                    obtenerCombatesCampeonato(mIdZona);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Botón Asignar
        mAsignarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIdCombateSeleccionado.equals("")){ // Si no se ha seleccionado ningún combate no se puede asignar
                    Toast.makeText(AddZonaCombateActivity.this, "No se ha seleccionado ningún Combate para ASIGNAR.", Toast.LENGTH_SHORT).show();
                } else {
                    // Localizar el combate
                    asignarCombateZona(mIdCombateSeleccionado, mCombateSeleccionado.getCategoria(), mIdZona, mCombateSeleccionado.getCampeonato());
                }
            }
        });
        // Botón Eliminar
        mRevertirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIdCombateSeleccionado.equals("")){ // Si no se ha seleccionado ningún combate no se puede eliminar
                    Toast.makeText(AddZonaCombateActivity.this, "No se ha seleccionado ningún Combate para ELIMINAR.", Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        });
        // Botón Guardar
        // Se volverá visible si la lista de Combates asignados tiene un tamaño mayor que cero.
        /*mGuardarBtn.setVisibility(View.INVISIBLE);
        if(mListaCombates != null){
            if(mListaCombates.size() > 0){
                mGuardarBtn.setVisibility(View.VISIBLE);
            }
        }*/
        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mListaCombatesDisponibles.size() > 0) && (mListaCombates.size() == 0)) {
                    // Si existen combates sin asignar y esta zona no tiene ningún combate asignado se muestra un dialog esperando confirmación.
                } else if(mCombateSeleccionado.getIdZonaCombate() == null){
                    Toast.makeText(AddZonaCombateActivity.this, "Seleccione los Combates que quiere asignar a esta Zona antes de pulsar el botón de Guardar...", Toast.LENGTH_SHORT).show();
                } else {
                    // Se guardan los datos de los combates asignados a esta zona.
                    // Recuperar el texto del TextView con el Número de la Zona
                    String num = mNumZona.getText().toString();
                    // insertarDatosZona(mIdCamp, num);
                    // Se lanza una nueva actividad para asignar los árbitros a los combates.
                    Intent arbisZonaIntent = new Intent(AddZonaCombateActivity.this, AsignarArbitrosCombatesZonaActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("idCamp", mIdCamp);
                    extras.putString("idZona", mIdZona);
                    arbisZonaIntent.putExtras(extras);
                    startActivity(arbisZonaIntent);
                }
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
                                                mListaCombatesDisponibles.clear();
                                                mListaCombates.clear();
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
                                                                // Capturar el valor del ID de la Modalidad del Combate seleccionado.
                                                                String idMod = mListaIDsModDisp.get(groupPosition);
                                                                // Abrir el Dialog con el detalle del Combate.
                                                                abrirDialog(idCombate, idCat, idMod);
                                                                // Capturar el ID del Combate seleccionado para poder cambiarlo de lista de Combates.
                                                                mIdCombateSeleccionado = idCombate;
                                                                localizarCombate(idCat, idCombate);
                                                            } else {
                                                                Toast.makeText(AddZonaCombateActivity.this, "En este momemto no existen combates disponibles...", Toast.LENGTH_SHORT).show();
                                                            }
                                                            return false;
                                                        }
                                                    });

                                                        // Método para el Click en uno de los hijos de la lista de Combates ASIGNADOS a la Zona de Combate.
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
        List<String> listaIDsMod = new ArrayList<>();

        for(int i = 0; i < lista.size(); i++){ // Recorrer la lista
            // Obtener el número del combate (Título)
            titulos.add(lista.get(i).getNumCombate());
            // Obtener el estado del combate (Detalle)
            detalle.add(lista.get(i).getEstadoCombate().toString());
            // Obtener el ID del Combate para poder pasárselo al Dialog del Detalle
            listaIDs.add(lista.get(i).getId());
            // Para poder buscar un combate determinado necesitamos el ID de la Categoría
            listaIDsCategoria.add(lista.get(i).getCategoria());
            // Para poder mostrar el nombre de la Categoría debemos acceder a ella y para eso necesitamos el ID de la Modalidad
            listaIDsMod.add(lista.get(i).getModalidad());
        }

        if(!disp){
            mListaTitulos = titulos;
            mListaDetalle = detalle;
            mListaIDs = listaIDs;
            mListaIDsCat = listaIDsCategoria;
            mListaIDsMod = listaIDsMod;
        } else {
            mListaTitulosDisp = titulos;
            mListaDetalleDisp = detalle;
            mListaIDsDisp = listaIDs;
            mListaIDsCatDisp = listaIDsCategoria;
            mListaIDsModDisp = listaIDsMod;
        }

    }

    // Se encarga de cargar los datos en el ExpandableListView correspondiente
    private void cargarCombates(ExpandableListView view, List<String> titulos, List<String> detalle, boolean disp){

        if(!disp) {
            mListaCombatesAdapter = new CombatesExpandableListAdapter(AddZonaCombateActivity.this, mListaTitulos, mListaDetalle);
            view.setAdapter(mListaCombatesAdapter);
            mListaCombatesAdapter.notifyDataSetChanged();

        } else {
            mListaCombatesDispAdapter = new CombatesExpandableListAdapter(AddZonaCombateActivity.this, mListaTitulosDisp, mListaDetalleDisp);
            view.setAdapter(mListaCombatesDispAdapter);
            mListaCombatesDispAdapter.notifyDataSetChanged();
        }

    }

    public void abrirDialog(String idCombate, String idCategoria, String idMod){
        DetalleCombateDialog dialog = new DetalleCombateDialog();
        Bundle extras = new Bundle(); // En este bundle le pasamos el ID del Combate al dialog que muestra el detalle de ese combate.
        extras.putString("idCombate", idCombate);
        extras.putString("idCategoria", idCategoria);
        extras.putString("idMod", idMod);
        dialog.setArguments(extras);
        dialog.show(getSupportFragmentManager(), "combate dialog");
    }

    public void localizarCombate(String idCat, String idCombate){
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCat).child(idCombate);
        Query consulta = mCombatesDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(AddZonaCombateActivity.this, "Error: No existe ningún Combate en la BD con ese ID", Toast.LENGTH_SHORT).show();
                } else {
                    mCombateSeleccionado = dataSnapshot.getValue(Combates.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void localizarZonaCombate(String idCamp, String idZona){
        mZonaCombateBD = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate").child(idCamp).child(idZona);
        Query consulta = mZonaCombateBD;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(AddZonaCombateActivity.this, "No existe ninguna Zona de Combate con ese ID para el Campeonato seleccionado.", Toast.LENGTH_SHORT).show();
                } else {
                    mZonaSeleccionada = dataSnapshot.getValue(ZonasCombate.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método que se encarga de modificar los datos necesarios en los objetos Combates y ZonasCombate correspondiente a los IDs indicados.
    // Es decir, una vez realizadas esas modifiaciones el combate quedará asignado a la zona y la zona contendrá las referencias necesarias al combate en cuestión.
    private void asignarCombateZona(String idCombate, String idCat, String idZona, String idCamp){
        // Localizar Combate en la BD
        if(mCombateSeleccionado.getNumCombate().equals("")) {
            localizarCombate(idCat, idCombate);
        }
        // Modificar Combates.idZonaCombate
        mCombateSeleccionado.setIdZonaCombate(idZona);
        // Toast.makeText(this, "ID Zona Combate --> " + mCombateSeleccionado.getIdZonaCombate() + " (método : asignarZonasCombate)", Toast.LENGTH_SHORT).show();
        // Convertir el objeto Combates en un Map para actualizar la BD
        Map<String, Object> combateModificado = mCombateSeleccionado.toMap();
        // Map con las rutas necesarias para las actualizaciones
        Map<String, Object> rutasUpdate = new HashMap<>();
        // Localizar ZonaCombate en la BD
        localizarZonaCombate(idCamp, idZona);
        if(mZonaSeleccionada != null) {
            // Zona de Combate
            // Modificar lista de Combates para añadir la entrada correspondiente al Combate indicado.
            // Crear nuevo objeto con los datos extras para la Zona Seleccionada
            DatosExtraZonasCombate datos = new DatosExtraZonasCombate(mCombateSeleccionado.getId(), mCombateSeleccionado.getNumCombate(), 0, null);
            //Buscar el IdCombate del objeto recién creado para evitar duplicados.
            // Si existe --> Mensaje de AVISO
            buscarDatosDuplicados(datos.getIdCombate(), mZonaSeleccionada.getListaDatosExtraCombates());
            if(mDatosExisten){
                Toast.makeText(this, "Ya existen datos para ese Combate en la Zona de Combate actual. No se han modificado los datos.", Toast.LENGTH_SHORT).show();
            } else {
                // Si no existe --> Añadirlo a la lista de DatosExtra de la Zona Seleccionada y Actualizar los Datos correspondientes a la Zona y al Combate
                mZonaSeleccionada.addToListaDatosExtraCombate(datos);
                // List<DatosExtraZonasCombate> listaDatosZonaModificada = mZonaSeleccionada.getListaDatosExtraCombates();
                // Convertir el objeto ZonasCombate en un HashMap para actualizar la BD
                Map<String, Object> zonaModificada = mZonaSeleccionada.toMap();

                // Actualizar Zona y Combate en BD.
                DatabaseReference raizBD = FirebaseDatabase.getInstance().getReference("Arbitraje");
                // Ruta Zona
                rutasUpdate.put("/ZonasCombate/" + idCamp + "/" + idZona, zonaModificada);
                // Ruta Combate
                rutasUpdate.put("/Combates/" + idCat + "/" + idCombate, combateModificado);
                // Actualización  en la BD
                raizBD.updateChildren(rutasUpdate);
                Toast.makeText(this,
                        "Se ha actualizado la Zona del Combate Seleccionado. Combate Nº " + mCombateSeleccionado.getNumCombate()
                                + " Zona Nº " + mZonaSeleccionada.getNumZona(),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se ha podido ASIGNAR ese Combate a la Zona deseada...(método : asignarCombateZona) ", Toast.LENGTH_SHORT).show();
        }
    }

    // Este método se encarga de buscar los datos del Combate especificado para evitar duplicados en la lista de DatosExtra de la Zona a la que se quiere añadir dicho combate.
    private void buscarDatosDuplicados(String idCombate, List<DatosExtraZonasCombate> lista){
        // Recorrer la lista buscando una coincidencia
        for(int i = 0; i < lista.size(); i++){
            if(lista.get(i).getIdCombate().equals(idCombate)){
                mDatosExisten = true;
                return;
            }
        }
    }

    // Método que se encarga de eliminar un combate de la lista de combates asignados a la zona indicada. Se eliminarán las conexiones entre dicha zona y dicho combate.
    private void retirarCombateZona(String idCombate, String idCat, String idZona, String idCamp){
        // Localizar Combate en la BD
        // Modificar Combates.idZonaCombate para que quede como ""
        // Localizar ZonaCombate en la BD
        // Modificar lista de Combates para eliminar la entrada correspondiente al Combate indicado.
    }

    private void insertarDatosZona(final String idCamp, String numZona){
        // Generar ID al insertar la zona en la BD.
        mZonaCombateBD = FirebaseDatabase.getInstance().getReference("Arbitraje").child("ZonasCombate");
        mIdZona = mZonaCombateBD.child(idCamp).push().getKey();
        Toast.makeText(this, "ID de la Zona generado en insertarDatosZona --> " + mIdZona, Toast.LENGTH_SHORT).show();
        // Guardar el resto de datos de la Zona
        int n = Integer.valueOf(numZona);
        final ZonasCombate zona = new ZonasCombate(mIdZona, n, idCamp, null);
        Toast.makeText(this, "Zona.idCamp --> " + zona.getIdCamp(), Toast.LENGTH_SHORT).show();
        Query consulta = mZonaCombateBD.child(idCamp).orderByChild("numZona").equalTo(zona.getNumZona());
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(AddZonaCombateActivity.this, "Ya existe una Zona de Combate con ese Número", Toast.LENGTH_SHORT).show();
                    // Si ya existe la zona deberemos eliminar el id insertado con el push() del principio de este método.
                    mZonaCombateBD.child(idCamp).child(mIdZona).removeValue();
                } else {
                    // Añadir Zona de Combate
                    mZonaCombateBD.child(idCamp).child(mIdZona).setValue(zona);
                    Toast.makeText(AddZonaCombateActivity.this, "Se ha añadido la Zona de Combate " + zona.getNumZona() + " a la BD.", Toast.LENGTH_SHORT).show();
                    mZonaSeleccionada = zona;
                    // anadirCombatesListaZona(zona.getIdZona(), zona.getIdCamp(), mListaCombates);
                    // actualizarListaZonasCampeonato();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Añadir los combates seleccionados y almacenados en la lista indicada a la zona de combate indicada (idZona e idCamp).
    private void anadirCombatesListaZona(String idZona, String idCamp, final List<Combates> lista){
        // Localizar la zona de Combate
        mZonaCombateBD = FirebaseDatabase.getInstance().getReference("Arbirtaje/ZonasCombate").child(idCamp).child(idZona);
        mZonaCombateBD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(AddZonaCombateActivity.this, "No existe la Zona de Combate indicada.", Toast.LENGTH_SHORT).show();
                } else {
                    // Obtener la Zona de Combate de la BD
                    ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class);
                    // Añadir los combates de la Lista
                    // Recorrer la lista
                    for(int i = 0; i < lista.size(); i++){
                        // Obtener el combate de la lista
                        Combates comb = lista.get(i);
                        // Crear objeto de clase DatosExtraZonasCombate
                        // Se obtienen el idCombate y numCombate del objeto extraido de la lista
                        // y la información de árbitros se deja sin cubrir hasta que se especifique más adelante.
                        DatosExtraZonasCombate datos = new DatosExtraZonasCombate(comb.getId(), comb.getNumCombate(), 0, null);
                        // Añadir esos datos a la lista de la Zona de Combate
                        try {
                            zona.addToListaDatosExtraCombate(datos);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    // Actualizar la Zona de Combate en la BD.
                    Map<String, Object> zonaActualizada = null;
                    try {
                        zonaActualizada = zona.toMap();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    // Crear ruta para actualizar
                    Map<String, Object> rutaUpdate = new HashMap<>();
                    // Indicar ruta
                    rutaUpdate.put(mIdZona, zonaActualizada);
                    // Relizar la actualización del objeto indicado en la ruta indicada
                    mZonasDB.updateChildren(rutaUpdate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Una vez que se añada la zona de combate a la BD deberemos actualizar la lista de Zonas de Combate del Campeonato correspondiente.
    private void actualizarListaZonasCampeonato(){
        // mCampDB ya apunta al Campeonato actual
        Query consultaCamp = mCampDB;
        consultaCamp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mCampeonatoActual = dataSnapshot.getValue(Campeonatos.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Query consulta = mCampDB.child("listaZonasCombate");
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ZonasCombate> lista = new ArrayList<>();
                // Localizar la lista de Zonas de Combate de este Campeonato
                if(dataSnapshot.exists()){
                    for(DataSnapshot zona: dataSnapshot.getChildren()){
                        ZonasCombate z = zona.getValue(ZonasCombate.class);
                        lista.add(z);
                    }
                } else {
                    lista = new ArrayList<>();
                }
                mListaActualizada = lista;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Una vez localizado el Campeonato a actualizar y modificada a su lista de zonas de combate
        // deberemos actualizar el Campeonato e insertar los cambios en la BD.
        if(mListaActualizada.size() > 0){ // Si hemos introducido alguna zona nueva se actualiza el Campeonato
            // Actualizar Objeto Campeonato
            mCampeonatoActual.setListaZonasCombate(mListaActualizada);
            // Mapear Campeonato
            Map<String, Object> campeonatoActualizado = mCampeonatoActual.toMap();
            // Crear ruta de actualizacion
            Map<String, Object> rutaUpdate = new HashMap<>();
            rutaUpdate.put("/Campeonatos/" + mIdCamp, campeonatoActualizado);
            // Actualizar BD en la ruta indicada
            DatabaseReference campDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
            campDB.updateChildren(rutaUpdate);
            Toast.makeText(this, "Se ha actualizado la lista de Zonas de Combate del Campeonato actual.", Toast.LENGTH_SHORT).show();
        }
    }
}
