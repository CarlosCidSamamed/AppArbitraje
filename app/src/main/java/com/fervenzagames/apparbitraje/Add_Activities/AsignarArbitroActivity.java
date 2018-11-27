package com.fervenzagames.apparbitraje.Add_Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.ArbitrosList;
import com.fervenzagames.apparbitraje.Adapters.ZonasCombateList;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AsignarArbitroActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mNombre;
    private TextView mZonaCombate;
    private ListView mZonas;
    private ListView mListaArbis;
    private List<Arbitros> mLista;
    private DatabaseReference mArbitrosDB;

    private AutoCompleteTextView mBuscador;
    private List<String> mListaDNIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignar_arbitro);

        mToolbar = findViewById(R.id.add_arb_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Asignar Árbitro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNombre = findViewById(R.id.asignar_arb_nombreCamp);

        mZonaCombate = findViewById(R.id.asignar_arb_zonaCombate);
        mZonas = findViewById(R.id.asignar_arb_zonasCombate);
        mListaArbis = findViewById(R.id.asignar_arb_listaArbis);
        mLista = new ArrayList<>();
        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros");

        mBuscador = findViewById(R.id.asignar_arb_buscador);
        mListaDNIs = new ArrayList<>();

        mNombre.setText(getIntent().getStringExtra("NombreCampeonato"));
        // mZonaCombate.setText(getIntent().getStringExtra("numZonasCombate"));

        // Generar array de zonas de combate
        int numZonas = Integer.parseInt(getIntent().getStringExtra("numZonasCombate"));
        String[] arrayZonas = new String[numZonas];
        String n = String.valueOf(arrayZonas.length);
        // mZonaCombate.setText(n);
        for(int i = 0; i < arrayZonas.length; i++){
            int x = i + 1;
            String z = String.valueOf(x);
            arrayZonas[i] = z;
        }

        // Crear el adapter para cargar los datos del array en un ListView
        ArrayAdapter adapterZonas = new ZonasCombateList(this, arrayZonas);
        adapterZonas.setDropDownViewResource(R.layout.zona_combate_single_layout);
        mZonas.setAdapter(adapterZonas);

        // Recuperar los DNIs de los Árbitros para el autocomplete del buscador
        recuperarDNIsArbis();
        // Asignar los datos de la lista de DNIs al AutoCompleteTextView mediante un ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListaDNIs);
        mBuscador.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // aplicarFiltro...
        // Prueba Filtro Nivel 2
        /*final Query consulta = mArbitrosDB
                .orderByChild("nivel")
                .equalTo("Nivel 2");*/

        final Query consulta = mArbitrosDB
                .orderByChild("DNI");

        // Cargar la lista de árbitros sin filtrar
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLista.clear();

                if(!dataSnapshot.exists()){
                    Toast.makeText(AsignarArbitroActivity.this, "No existen Árbitros que cumplan esas condiciones. Revise el filtro utilizado.", Toast.LENGTH_SHORT).show();
                } else {
                    for(DataSnapshot arbitroSnapshot: dataSnapshot.getChildren()){
                        Arbitros arbi = arbitroSnapshot.getValue(Arbitros.class);
                        mLista.add(arbi);
                    }


                    ArbitrosList adapter = new ArbitrosList(AsignarArbitroActivity.this, mLista);
                    adapter.setDropDownViewResource(R.layout.arbitro_single_layout);
                    mListaArbis.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // En este método se especifican los filtros a usar para devolver la lista de Árbitros disponibles para asignar al campeonato actual.
    // El campo idCamp de los Árbitros almacena el valor del campeonato actual al que están asignados, es decir:
    //      idCamp == 0 --> Disponible porque no se ha asignado a este campeonato
    //      idCamp != 0 --> Si idCamp coincide con el id del campeonato actual quiere decir que ya se le ha asignado, es decir, no está disponible.
    //                  --> Si idCamp no coincide con el id del campeonato actual quiere decir que se le ha asignado a otro campeonato. En este caso deberemos recuperar
    //                      la info de ambos campeoantos para ver si coinciden en fecha y/o lugar. No existe el don de la Ubicuidad(;D).
    // El DNI se usará para localizar a un árbitro en particular. Para usar el autocomplete debo recuperar los dni de todos los árbitros en la BD y pasarle dicho array a un
    // ArrayAdapter
    public void aplicarFiltro(String nivel, String cargo, String DNI){



    }
    // El DNI se usará para localizar a un árbitro en particular. Para usar el autocomplete debo recuperar los dni de todos los árbitros en la BD y pasarle dicho array a un
    // ArrayAdapter
    public void recuperarDNIsArbis(){

        Query consulta = mArbitrosDB
                .orderByChild("DNI");

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaDNIs.clear();
                if(!dataSnapshot.exists()){
                    Toast.makeText(AsignarArbitroActivity.this, "No existe ningún Árbitro en la BD de Firebase.", Toast.LENGTH_SHORT).show();
                } else {
                    long num = dataSnapshot.getChildrenCount();

                    // Toast.makeText(getApplicationContext(), "La consulta de los DNIs de los Árbitros devuelve " + num + " resultados.", Toast.LENGTH_SHORT).show();

                    for(DataSnapshot arbiSnapshot: dataSnapshot.getChildren()){
                        Arbitros arbi = arbiSnapshot.getValue(Arbitros.class);
                        String dni = arbi.getDni();
                        mListaDNIs.add(dni);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
