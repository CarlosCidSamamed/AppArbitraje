package com.fervenzagames.apparbitraje.Add_Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

    private Button mAplicarFiltroBtn;
    private Spinner mNivelSpinner;
    private Spinner mCargoSpinner;

    private String mIdZonaCombate; // Para almacenar el id de la Zona de Combate que se selecciona en la ListView correspondiente.
    private String mIdArbi;        // Para almacenar el id del Árbitro que se selecciona en la ListView correspondiente.

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

        mAplicarFiltroBtn = findViewById(R.id.asignar_arb_aplicarFiltro_btn);
        mNivelSpinner = findViewById(R.id.asignar_arb_filtroNivelSpinner);
        mCargoSpinner = findViewById(R.id.asignar_arb_filtroCargoSpinner);

        mIdZonaCombate = "";
        mIdArbi = "";

        mNombre.setText(getIntent().getStringExtra("NombreCampeonato"));
        // mZonaCombate.setText(getIntent().getStringExtra("numZonasCombate"));

        // Generar array de zonas de combate
        int numZonas = Integer.parseInt(getIntent().getStringExtra("numZonasCombate"));
        final String[] arrayZonas = new String[numZonas];
        String n = String.valueOf(arrayZonas.length);
        // mZonaCombate.setText(n);
        for(int i = 0; i < arrayZonas.length; i++){
            int x = i + 1;
            String z = String.valueOf(x);
            arrayZonas[i] = z;
        }

        // Crear el adapter para cargar los datos del array en un ListView
        final ArrayAdapter adapterZonas = new ZonasCombateList(this, arrayZonas);
        adapterZonas.setDropDownViewResource(R.layout.zona_combate_single_layout);
        mZonas.setAdapter(adapterZonas);

        // Recuperar los DNIs de los Árbitros para el autocomplete del buscador
        recuperarDNIsArbis();
        // Asignar los datos de la lista de DNIs al AutoCompleteTextView mediante un ArrayAdapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListaDNIs);
        mBuscador.setAdapter(adapter);
        mBuscador.setThreshold(1); // El autocomplete funciona al escribir el primer carácter correcto.

        // ¿Qué ocurre al seleccionar un DNI en el buscador con autocomplete?
        mBuscador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dni = mListaDNIs.get(position); // Recuperar el DNI seleccionado
                Query consulta = mArbitrosDB.orderByChild("dni").equalTo(dni); // Crear la consulta
                realizarConsultaArbis(consulta); // Realizar la consulta
            }
        });

        // Cargar los datos correspondientes a los cargos según el nivel
        mNivelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:{
                        ArrayAdapter<CharSequence> adapterUno = ArrayAdapter.createFromResource(AsignarArbitroActivity.this, R.array.tareasNivelUno, android.R.layout.simple_spinner_item);
                        adapterUno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCargoSpinner.setAdapter(adapterUno);
                        break;
                    }
                    case 1:{
                        ArrayAdapter<CharSequence> adapterDos = ArrayAdapter.createFromResource(AsignarArbitroActivity.this, R.array.tareasNivelDos, android.R.layout.simple_spinner_item);
                        adapterDos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCargoSpinner.setAdapter(adapterDos);
                        break;
                    }
                    case 2:{
                        ArrayAdapter<CharSequence> adapterTres = ArrayAdapter.createFromResource(AsignarArbitroActivity.this, R.array.tareasNivelTres, android.R.layout.simple_spinner_item);
                        adapterTres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCargoSpinner.setAdapter(adapterTres);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Aplicar el filtro al pulsar el botón
        mAplicarFiltroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos del spinner de Nivel
                String nivel = mNivelSpinner.getSelectedItem().toString();

                // Obtener los datos del spinner de Cargo
                String cargo = mCargoSpinner.getSelectedItem().toString();

                // aplicarFiltro
                Query consulta = aplicarFiltro(nivel, cargo, "");

                // realizarConsulta
                realizarConsultaArbis(consulta);
            }
        });


        // Almacenar el ID de la Zona de Combate al seleccionarlo en su ListView.
        mZonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIdZonaCombate = ((ZonasCombateList) adapterZonas).getItem(position);
                String nuevaZona = "Zona de Combate -- "  + mIdZonaCombate;
                mZonaCombate.setText(nuevaZona);
            }
        });
        // Almacenar el ID del Árbitro al seleccionarlo en su ListView.
        mListaArbis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Arbitros arbi = mLista.get(position);
                mIdArbi = arbi.getDni();

                // Comprobar que se haya seleccionado la zona de Combate antes de seleccionar al Árbitro.
                if(TextUtils.isEmpty(mIdZonaCombate)){
                    Toast.makeText(AsignarArbitroActivity.this, "Seleccione una Zona de Combate antes de seleccionar al Árbitro deseado.", Toast.LENGTH_SHORT).show();
                } else {
                    guardarDatos(mIdZonaCombate, mIdArbi);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query cons =  aplicarFiltro(mNivelSpinner.getSelectedItem().toString(), mCargoSpinner.getSelectedItem().toString(), "");

        realizarConsultaArbis(cons);

    }


    // En este método se especifican los filtros a usar para devolver la lista de Árbitros disponibles para asignar al campeonato actual.
    // El campo idCamp de los Árbitros almacena el valor del campeonato actual al que están asignados, es decir:
    //      idCamp == 0 --> Disponible porque no se ha asignado a este campeonato
    //      idCamp != 0 --> Si idCamp coincide con el id del campeonato actual quiere decir que ya se le ha asignado, es decir, no está disponible.
    //                  --> Si idCamp no coincide con el id del campeonato actual quiere decir que se le ha asignado a otro campeonato. En este caso deberemos recuperar
    //                      la info de ambos campeoantos para ver si coinciden en fecha y/o lugar. No existe el don de la Ubicuidad(;D).
    // El DNI se usará para localizar a un árbitro en particular. Para usar el autocomplete debo recuperar los dni de todos los árbitros en la BD y pasarle dicho array a un
    // ArrayAdapter
    // El método devuelve la Query con los parámetros correctos.
    public Query aplicarFiltro(String nivel, String cargo, String dni){

        Query consulta = mArbitrosDB
                .orderByChild("dni");

        if(TextUtils.isEmpty(dni)){ // No se ha especificado un filtro por DNI pero sí por nivel y cargo
            String nivel_cargo = nivel + "_" + cargo;
            Query consulta2 = mArbitrosDB.orderByChild("nivel_cargo").equalTo(nivel_cargo);
            consulta = consulta2;
        }

        return consulta;

    }
    // El DNI se usará para localizar a un árbitro en particular. Para usar el autocomplete debo recuperar los dni de todos los árbitros en la BD y pasarle dicho array a un
    // ArrayAdapter
    public void recuperarDNIsArbis(){

        Query consulta = mArbitrosDB
                .orderByChild("dni");

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

    // Método para realizar la consulta a la BD con los filtros o con el buscador de DNIs
    public void realizarConsultaArbis(Query query){
        final Query consulta = query;

        // Cargar la lista de árbitros sin filtrar
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLista.clear();

                if(!dataSnapshot.exists()){
                    Toast.makeText(AsignarArbitroActivity.this, "No existen Árbitros que cumplan esas condiciones. Revise el filtro utilizado.", Toast.LENGTH_SHORT).show();
                    mListaArbis.setAdapter(null); // Limpiar ListView si no se encuentran árbitros que cumplan las condiciones.
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

    // Método para guardar los datos del Árbitro seleccionado, es decir, asignar a ese Árbitro al Campeonato y a la Zona de Combate seleccionados.
    public void guardarDatos(String idZona, String idArbi){
        Toast.makeText(this, "Guardando datos para Asignar Árbitro... ID Zona --> " + mIdZonaCombate + ", ID Arbi --> " + mIdArbi, Toast.LENGTH_SHORT).show();
        // Recuperar datos Árbitro

        // Actualizar :
        // 1. Lista de zonas de combate
        // 2. Id de la zona de combate actual
        // 3. IdCamp

        // Localizar el Campeonato deseado

        // Actualizar Lista Árbitros

        // Mostrar mensaje de Éxito / Error
    }
}
