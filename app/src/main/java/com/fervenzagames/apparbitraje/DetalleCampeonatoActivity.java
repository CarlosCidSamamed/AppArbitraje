package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DetalleCampeonatoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombre;
    private TextView mLugar;
    private TextView mFecha;
    private TextView mTipo;

    private ListView mListaArbitrosView;
    private ListView mListaModalidadesView;

    private List<Arbitros> mListaArbitros;
    private List<Modalidades> mListaModalidades;

    private Button mAddArbitroBtn;
    private Button mAddModalidadBtn;

    private DatabaseReference campDB;

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

        mListaArbitros = new ArrayList<>();
        mListaModalidades = new ArrayList<>();

        mAddArbitroBtn = (Button) findViewById(R.id.camp_detalle_add_arb);
        mAddModalidadBtn = (Button) findViewById(R.id.camp_detalle_add_mod);

        String idCamp = getIntent().getStringExtra("idCamp");
        campDB = FirebaseDatabase.getInstance().getReference().child("Arbitraje").child("Campeonatos").child(idCamp);

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
                //addArbitro();
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
                for(DataSnapshot campSnapshot: dataSnapshot.child(idCamp).getChildren()){
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
                    mListaArbitrosView.setAdapter(adapter);
                } else {
                    Toast.makeText(DetalleCampeonatoActivity.this, "La lista de Modalidades de este Campeonato está VACÍA", Toast.LENGTH_LONG).show();
                }
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
}
