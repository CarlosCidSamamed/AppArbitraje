package com.fervenzagames.apparbitraje.Add_Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

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
import java.util.List;

public class AsignarArbitrosCombatesZonaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mGuardarBtn;

    private ExpandableListView mListaCombatesView;

    private List<Combates> mListaCombates;

    private DatabaseReference mCampDB;
    private DatabaseReference mModsDB;
    private DatabaseReference mCatsDB;
    private DatabaseReference mCombatesDB;
    private DatabaseReference mArbisDB;

    private String mIdCamp;
    private String mIdZona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignar_arbitros_combates_zona);

        mToolbar = findViewById(R.id.asignar_arb_combate_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Asignar Árbitros a los Combates");

        // Mientras no se seleccionen un combate de la zona y un árbitro del campeonato no se mostrará el botón para guardar los datos.
        mGuardarBtn = findViewById(R.id.asignar_arb_combate_guardarBtn);
        mGuardarBtn.setVisibility(View.INVISIBLE);

        mListaCombatesView = findViewById(R.id.asignar_arb_combate_combatesAsignados);

        Bundle extras = getIntent().getExtras();
        mIdCamp = extras.getString("idCamp");
        mIdZona = extras.getString("idZona");

        Toast.makeText(this, "ID CAMP --> "+ mIdCamp, Toast.LENGTH_SHORT).show();
        // cargarCombatesZona(mIdCamp, mIdZona);



    }
    // Método para cargar desde la BD los Combates que se han asignado a la Zona de Combate del Campeonato especificado.
    private void cargarCombatesZona(final String idCamp, final String idZona){
        // mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos").child(idCamp); // El Campeonato buscado.
        mModsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(idCamp); // Lista de Modalidades de ese Campeonato.
        final List<Modalidades> listaMods = new ArrayList<>();
        final List<Categorias> listaCats = new ArrayList<>();
        final List<Combates> listaCombates = new ArrayList<>();
        // Al no especificar la Categoría del combate deberemos recorrer todas las Modalidades y Categorías del Campeonato para localizar los Combates
        // cuyo ID de Zona de Combate coincida con el que estamos buscando.
        Query consultaMods = mModsDB;
        consultaMods.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMods.clear();
                if(!dataSnapshot.exists()){
                    Toast.makeText(AsignarArbitrosCombatesZonaActivity.this, "No existen Modalidades en la BD para ese Campeonato", Toast.LENGTH_SHORT).show();
                } else {
                    // Para cada modalidad de ese Campeonato deberemos obtener su ID para buscar sus categorías.
                    for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                        Modalidades mod = modSnapshot.getValue(Modalidades.class);
                        listaMods.add(mod);
                    }
                    // Recorremos la lista de Modalidades y obtenemos las categorías de cada una de las Modalidades.
                    for(int i = 0; i < listaMods.size(); i++){
                        Modalidades mod = listaMods.get(i);
                        String idMod = mod.getId();
                        final String nombreMod = mod.getNombre();
                        mCatsDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Categorias").child(idMod);
                        mCatsDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listaCats.clear();
                                if(!dataSnapshot.exists()){
                                    Toast.makeText(AsignarArbitrosCombatesZonaActivity.this, "No existen Categorías para la Modalidad " + nombreMod, Toast.LENGTH_SHORT).show();
                                } else { // A su vez, para cada categoría deberemos obtener su ID para buscar sus combates.
                                    for(DataSnapshot catSnapshot: dataSnapshot.getChildren()){
                                        Categorias cat = catSnapshot.getValue(Categorias.class);
                                        listaCats.add(cat);
                                    }
                                    // Recorremos la lista de Categorías y obtenemos los Combates de cada una de ellas.
                                    for(int i = 0; i < listaCats.size(); i++){
                                        Categorias cat = listaCats.get(i);
                                        String idCat = cat.getId();
                                        final String nombreCat = cat.getNombre();
                                        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCat); // Combates de esta categoría.
                                        mCombatesDB.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                listaCombates.clear();
                                                if(!dataSnapshot.exists()){
                                                    Toast.makeText(AsignarArbitrosCombatesZonaActivity.this, "No existen Combates en la BD para la Categoría " + nombreCat, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    for(DataSnapshot combSnapshot: dataSnapshot.getChildren()){
                                                        Combates comb = combSnapshot.getValue(Combates.class);
                                                        // De todos los combates nos quedaremos con los combates cuyo ID de Zona de Combate coincida con el buscado.
                                                        // Esos combate serán los que mostraremos.
                                                        if(idZona == null) {
                                                            Toast.makeText(AsignarArbitrosCombatesZonaActivity.this, "No se ha indicado el ID de la Zona...", Toast.LENGTH_SHORT).show();
                                                        } else if(comb.getIdZonaCombate().equals(idZona)){
                                                            listaCombates.add(comb);
                                                        }
                                                    }
                                                    mListaCombates = listaCombates; // Lista de Combates a mostrar en el ExpandableListView de Combates de la Zona.
                                                    if(mListaCombates.size() == 0){
                                                        Toast.makeText(AsignarArbitrosCombatesZonaActivity.this, "La lista de Combates de esta Zona está VACÍA", Toast.LENGTH_SHORT).show();
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

    }

    // Método para cargar desde la BD los Árbitros que se han asignado al Campeonato especificado.
    private void cargarArbitrosCampeonato(String idCamp){

    }


}
