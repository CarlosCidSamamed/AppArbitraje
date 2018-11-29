package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.fervenzagames.apparbitraje.Adapters.ModalidadesList;
import com.fervenzagames.apparbitraje.Add_Activities.AddModalidadActivity;
import com.fervenzagames.apparbitraje.Add_Activities.AsignarArbitroActivity;
import com.fervenzagames.apparbitraje.CampeonatosActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.User_Activities.SettingsActivity;
import com.fervenzagames.apparbitraje.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalleCampeonatoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombre;
    private TextView mLugar;
    private TextView mFecha;
    private TextView mTipo;

    private String numZonasCombate;

    private ListView mListaArbitrosView;
    private ListView mListaModalidadesView;

    private ModalidadesList mModAdapter;

    private List<Arbitros> mListaArbitros;
    private List<Modalidades> mListaModalidades;

    private Button mAddArbitroBtn;
    private Button mAddModalidadBtn;

    private DatabaseReference campDB;
    private DatabaseReference modsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_campeonato);

        mToolbar = (Toolbar) findViewById(R.id.det_camp_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Campeonato");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNombre = (TextView) findViewById(R.id.camp_detalle_nombre);
        mLugar = (TextView) findViewById(R.id.camp_detalle_lugar);
        mFecha = (TextView) findViewById(R.id.camp_detalle_fecha);
        mTipo = (TextView) findViewById(R.id.camp_detalle_tipo);

        mListaArbitrosView = (ListView) findViewById(R.id.camp_detalle_listaArbitros);
        mListaModalidadesView = (ListView) findViewById(R.id.camp_detalle_listaModalidades);

        mModAdapter = new ModalidadesList(DetalleCampeonatoActivity.this, mListaModalidades);

        mListaArbitros = new ArrayList<>();
        mListaModalidades = new ArrayList<>();

        mAddArbitroBtn = (Button) findViewById(R.id.camp_detalle_add_arb);
        mAddModalidadBtn = (Button) findViewById(R.id.camp_detalle_add_mod);

        String idCamp = getIntent().getStringExtra("idCamp");
        campDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);
        modsDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp);

        campDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String lugar = dataSnapshot.child("lugar").getValue().toString();
                String fecha = dataSnapshot.child("fecha").getValue().toString();
                String tipo = dataSnapshot.child("tipo").getValue().toString();

                // Lista de Árbitros
                if(dataSnapshot.child("listaArbitros").getValue() != null) {
                    // Lista NO vacía
                }


                // Lista de Modalidades
                if(dataSnapshot.child("listaModalidades").getValue() != null){
                    // Lista NO vacía
                }


                mNombre.setText(nombre);
                mLugar.setText(lugar);
                mFecha.setText(fecha);
                mTipo.setText(tipo);

                numZonasCombate = dataSnapshot.child("numZonasCombate").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAddModalidadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addModalidad();
            }
        });

        mAddArbitroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArbitro();
            }
        });

        mListaModalidadesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mModAdapter.notifyDataSetChanged(); // Notificar al adapter de la lista de Modalidades de que se ha podido modificar el dataset en otra actividad (Añadir Modalidad)
                // Sin añadir esta línea de código se mostraba una excepción y la app se cerraba automáticamente.

                // Al pulsar sobre una Modalidad se mostrará su detalle con sus Categorías.
                Modalidades mod = mListaModalidades.get(position);
                // Se crea un nuevo Intent
                Intent modIntent = new Intent(DetalleCampeonatoActivity.this, DetalleModalidadActivity.class);
                String idCamp = getIntent().getStringExtra("idCamp"); // Si se pulsa la flecha para volver al detalle del Campeonato desde el detalle de una Modalidad
                //modIntent.putExtra("idCamp", idCamp);                 // se necesita el idCamp, es decir, el ID del Campeonato a mostrar.
                // Pasar el Id de la Modalidad
                String idMod = mod.getId();
                //modIntent.putExtra("idMod", idMod);

                // Para poder pasar más de un elemento en el Extra de un Intent tengo que usar un Bundle, es decir, un paquete de objetos y pasarlo al Extra.
                Bundle extras = new Bundle();
                extras.putString("idCamp", idCamp);
                extras.putString("idMod", idMod);
                modIntent.putExtras(extras);

                // Y se inicia dicho Intent
                startActivity(modIntent);
            }
        });
    }

    // Selecionar el menú principal que deseamos y mostrarlo.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        campDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //mListaArbitros.clear();
                mListaModalidades.clear();
                String idCamp = getIntent().getStringExtra("idCamp");
                // Localizar las modalidades de este campeonato con modsDB
                modsDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mListaModalidades.clear();
                        for(DataSnapshot modsSnapshot : dataSnapshot.getChildren()){ // Recorremos las Modalidades de este Campeonato
                            Modalidades mod = modsSnapshot.getValue(Modalidades.class);
                            mListaModalidades.add(mod);
                        }
                        ModalidadesList adapter = new ModalidadesList(DetalleCampeonatoActivity.this, mListaModalidades);
                        mListaModalidadesView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                /*for(DataSnapshot campSnapshot: dataSnapshot.child(idCamp).getChildren()){
                    //Arbitros arb = campSnapshot.getValue(Arbitros.class);
                    //mListaArbitros.add(arb);
                    Modalidades mod = campSnapshot.getValue(Modalidades.class);
                    if(mod != null){
                        mListaModalidades.add(mod);
                    }

                }

                // Lista Arbitros

                // Lista Modalidades
                if(mListaModalidades.size() != 0){

                    ModalidadesList adapter = new ModalidadesList(DetalleCampeonatoActivity.this, mListaModalidades);
                    mListaModalidadesView.setAdapter(adapter);
                } else {
                    Toast.makeText(DetalleCampeonatoActivity.this, "La lista de Modalidades de este Campeonato está VACÍA", Toast.LENGTH_LONG).show();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Lista de Modalidades de este campeonato
        modsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListaModalidades.clear();
                for(DataSnapshot modSnapshot: dataSnapshot.getChildren()){
                    Modalidades mod = modSnapshot.getValue(Modalidades.class);
                    mListaModalidades.add(mod);
                }
                ModalidadesList adapter = new ModalidadesList(DetalleCampeonatoActivity.this, mListaModalidades);
                mListaModalidadesView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addModalidad(){

        //Obtener el nombre del campeonato seleccionado
        String nombre = mNombre.getText().toString();
        String idCamp = getIntent().getStringExtra("idCamp");

        Intent addModIntent = new Intent(DetalleCampeonatoActivity.this, AddModalidadActivity.class);
        addModIntent.putExtra("NombreCampeonato", nombre);
        addModIntent.putExtra("IdCamp", idCamp);
        startActivity(addModIntent);

    }

    private void addArbitro(){
        //Obtener el nombre del campeonato seleccionado
        String nombre = mNombre.getText().toString();
        String idCamp = getIntent().getStringExtra("idCamp");

        Intent addArbIntent = new Intent(DetalleCampeonatoActivity.this, AsignarArbitroActivity.class);
        addArbIntent.putExtra("NombreCampeonato", nombre);
        addArbIntent.putExtra("idCamp", idCamp);
        addArbIntent.putExtra("numZonasCombate", numZonasCombate);
        startActivity(addArbIntent);
    }

    /*------------------------------- MENU PRINCIPAL -----------------------------------------------------------------------------------------------------------------*/

    // ¿Qué ocurre si se pulsa algún botón en el menú principal?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.main_logout_btn:{
                // Vamos a incluir el código para cerrar la sesión.
                FirebaseAuth.getInstance().signOut(); // Cerrar sesión con Firebase Auth.
                sendToStart(); // Redirigir al inicio de la app.
                break;
            }
            case R.id.main_settings_btn:{
                Toast.makeText(DetalleCampeonatoActivity.this, "Ha pulsado el botón Opciones", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_account_settings:{
                // Toast.makeText(MainActivity.this, "Ha pulsado el botón de Cuenta de Usuario", Toast.LENGTH_SHORT).show();
                // Crear el Intent de la página de Settings
                Intent sett_intent = new Intent(DetalleCampeonatoActivity.this, SettingsActivity.class);
                // E iniciarlo
                startActivity(sett_intent);
                break;
            }
            case R.id.main_app_type:{
                Toast.makeText(DetalleCampeonatoActivity.this, "Ha pulsado el botón de Tipo de App", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_ver_campeonatos:{
                Toast.makeText(DetalleCampeonatoActivity.this, "Ver los Campeonatos de la BD", Toast.LENGTH_SHORT).show();
                Intent campIntent = new Intent(DetalleCampeonatoActivity.this, CampeonatosActivity.class);
                startActivity(campIntent);
                break;
            }
        }

        return true;
    }

    private void sendToStart() {
        /* Se crea un Intent para la página de LOGIN */
        Intent startIntent = new Intent(DetalleCampeonatoActivity.this, StartActivity.class);
        /* Se inicia ese Intent */
        startActivity(startIntent);
        /* Con esta línea evitamos que al pulsar el botón para retroceder se vuelva a esta actividad. */
        finish();
    }
}
