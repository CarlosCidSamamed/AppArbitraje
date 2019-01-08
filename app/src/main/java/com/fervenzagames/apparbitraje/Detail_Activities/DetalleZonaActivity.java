package com.fervenzagames.apparbitraje.Detail_Activities;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CombatesExpandableListAdapter;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalleZonaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombreCamp;
    private TextView mNumZona;

    private TextView mNumCombates;

    private DatabaseReference mZonaDB;
    private DatabaseReference mCampDB;
    private DatabaseReference mCombatesDB;
    private DatabaseReference mModsDB;
    private DatabaseReference mCatsDB;

    private List<Combates> mListaCombates;
    private List<String> mListaIDs;
    private List<String> mListaTitulos;
    private List<String> mListaDetalles;

    private Combates mCombateSeleccionado;

    private ExpandableListView mExpandableListView;
    private CombatesExpandableListAdapter mAdapter;

    private String mIdZona;
    private String mIdCamp;

    private String mNombre;
    private String mNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_zona);

        mToolbar = findViewById(R.id.detalle_zona_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Zona de Combate");

        mNombreCamp = findViewById(R.id.detalle_zona_nombreCamp);
        mNumZona = findViewById(R.id.detalle_zona_numZona);

        mExpandableListView = findViewById(R.id.detalle_zona_listaCombates);
        mNumCombates = findViewById(R.id.detalle_zona_numCombates);

        mListaCombates = new ArrayList<>();
        mListaIDs = new ArrayList<>();
        mListaTitulos = new ArrayList<>();
        mListaDetalles = new ArrayList<>();

        mCombateSeleccionado = new Combates();

        Bundle extras = getIntent().getExtras();
        try {
            mIdZona = extras.getString("idZona");
            mIdCamp = extras.getString("idCamp");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mZonaDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate").child(mIdCamp).child(mIdZona);
        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos").child(mIdCamp);
        //Toast.makeText(this, "idCamp --> " + mIdCamp + " // idZona --> " + mIdZona, Toast.LENGTH_SHORT).show();
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates");
        mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(mIdCamp); // Modalidades de ese Campeonato
        mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias");

        mZonaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(DetalleZonaActivity.this, "No existe ninguna Zona de Combate que coincida con los datos especificados...", Toast.LENGTH_SHORT).show();
                } else {
                    ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class);
                    try {
                        mNum = String.valueOf(zona.getNumZona());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    mCampDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                Toast.makeText(DetalleZonaActivity.this, "No existe ningún Campeonato con esos datos...", Toast.LENGTH_SHORT).show();
                            } else {
                                Campeonatos camp = dataSnapshot.getValue(Campeonatos.class);
                                try {
                                    mNombre = camp.getNombre();
                                    mNombreCamp.setText(mNombre);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    String num = " Zona : " + mNum;
                    mNumZona.setText(num);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cargarDatosCombates();

    }

    // Método para cargar los datos de los combates de esta zona
    private void cargarDatosCombates(){
        // 1. Se deberá obtener la lista de datosExtraCombates de la Zona
        // 2. Crear lista con los idCombate de los combates de la Zona
        // 3. Obtener el estado de esos combates en la rama de la DB de combates. Buscaremos los combates por idCombate.
        // 4. Obtener el número de esos combates. Los datos para 3 y 4 se obtienen de los objetos de tipo Combates que devuelve getCombate
        // 5. Devolver lista con los estados y la lista con los idCombate
        mZonaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(DetalleZonaActivity.this, "No existen datos para esta Zona", Toast.LENGTH_SHORT).show();
                } else {
                    ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class);
                    List<DatosExtraZonasCombate> combates = null; // 1
                    try {
                        combates = zona.getListaDatosExtraCombates();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(combates != null){
                        Toast.makeText(DetalleZonaActivity.this, "Lista de Combates tiene " + combates.size() + " elemento(s)", Toast.LENGTH_SHORT).show();
                        String num = mNumCombates.getText() + " : ( " + combates.size() + " )";
                        mNumCombates.setText(num);
                        for(int i = 0; i < combates.size(); i++){
                            String idCombate = combates.get(i).getIdCombate();
                            mListaIDs.add(idCombate); // 2
                        } // Una vez que tenemos los IDs de los Combates a buscar...
                        for(int j = 0; j < mListaIDs.size(); j++){
                            Combates c = getCombate(mListaIDs.get(j));
                            mListaCombates.add(c);
                        } // Localizamos y obtenemos los objetos de la clase Combates.
                        Toast.makeText(DetalleZonaActivity.this, "mListaCombates.size() = " + mListaCombates.size(), Toast.LENGTH_SHORT).show();
                        // Obtenemos los datos de cada Combate para mostrarlos en el ExpandableListView
                        // NumCombate y Estado
                        for(int k = 0; k < mListaCombates.size(); k++){
                            Combates c = mListaCombates.get(k);
                            if((c != null) || !(c.getNumCombate().equals("0"))) {
                                /*String numCombate = c.getNumCombate();
                                String estado = c.getEstadoCombate().toString();
                                mListaTitulos.add(numCombate);
                                mListaDetalles.add(estado);*/
                            } else {
                                Toast.makeText(DetalleZonaActivity.this, "Error al leer los datos del Combate que está en la posición " + k + " de la lista...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(DetalleZonaActivity.this, "Lista de Combates para esta Zona --> NULL", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método para obtener un Combate a partir de su idCombate
    private Combates getCombate(final String idCombate){
        final List<Combates> listaRes = new ArrayList<>();
        final List<String> listaIDsMod = new ArrayList<>();
        final List<String> listaIDsCats = new ArrayList<>();
        // Recorreremos todos los combates para encontrar uno con el idCombate especificado.
        // Recorrer todas las modalidades y obtener una lista de IDs de dichas Modalidades...
        mModsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(DetalleZonaActivity.this, "Num de Modalidades para este Campeonato --> " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot modSnap: dataSnapshot.getChildren()){
                        Modalidades mod = modSnap.getValue(Modalidades.class);
                        listaIDsMod.add(mod.getId());
                    }
                    // Recorrer todas las categorías y obtener una lista de IDs de dichas Categorías...
                    for(int i = 0; i <listaIDsMod.size(); i++){
                        mCatsDB = mCatsDB.child(listaIDsMod.get(i)); // Para cada una de las Modalidades obtenemos sus Categorías
                        mCatsDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Toast.makeText(DetalleZonaActivity.this, "Num de Categorías para esta Modalidad --> " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                    // Recorrer todas las categorías y obtener una lista de IDs de dichas Categorías...
                                    for(DataSnapshot catSnap: dataSnapshot.getChildren()){
                                        Categorias cat = catSnap.getValue(Categorias.class);
                                        listaIDsCats.add(cat.getId());
                                    }
                                    for(int i = 0;  i < listaIDsCats.size(); i++){
                                        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(listaIDsCats.get(i));
                                        // Para cada categoría obtenemos sus combates.

                                        mCombatesDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    Toast.makeText(DetalleZonaActivity.this,
                                                            "Num de Combates para esta Categoría --> " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                                    for(DataSnapshot combSnap: dataSnapshot.getChildren()){
                                                        Combates comb = combSnap.getValue(Combates.class);
                                                        //Toast.makeText(DetalleZonaActivity.this,"ID del Combate seleccionado --> " + comb.getId(), Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(DetalleZonaActivity.this, "ID que se pasa como parámetro --> " + idCombate, Toast.LENGTH_SHORT).show();
                                                        if(comb.getId().equals(idCombate)){
                                                            // Buscar el combate por ID
                                                            // listaRes.add(comb);
                                                            mCombateSeleccionado = comb;
                                                            Toast.makeText(DetalleZonaActivity.this, "ID de mCombateSeleccionado --> " + mCombateSeleccionado.getId(), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            mCombateSeleccionado = null;
                                                        }
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
        if(mCombateSeleccionado != null){
            return mCombateSeleccionado;
        } else {
            mCombateSeleccionado = new Combates();
            mCombateSeleccionado.setEstadoCombate(Combates.EstadoCombate.Pendiente);
            mCombateSeleccionado.setNumCombate("0");
            return mCombateSeleccionado;
        }
    }

    // Método para cargar los datos de los combates en el ExpandableList
    private void cargarDatosVista(List<String> titulos, List<String> detalles, ExpandableListView vista){
        mAdapter = new CombatesExpandableListAdapter(DetalleZonaActivity.this, titulos, detalles);
        vista.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        // Modificar el número mostrado en el título del ExpandableListView
        String num = mNumCombates.getText() + " : ( " + titulos.size() + " )";
        mNumCombates.setText(num);
    }
}
