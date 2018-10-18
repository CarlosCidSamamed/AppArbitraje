package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

public class AddModalidadActivity extends AppCompatActivity {

    private TextView nombreCamp;
    private Toolbar mToolbar;

    private Spinner mNombreMod;
    private TextView mDescripcionMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modalidad);

        mToolbar = (Toolbar) findViewById(R.id.add_mod_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Modalidad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nombreCamp = (TextView) findViewById(R.id.add_mod_nombre_campeonato);

        Intent intent = getIntent();
        String nombreCampeonato = intent.getStringExtra("NombreCampeonato");

        nombreCamp.setText(nombreCampeonato);

        mNombreMod = (Spinner) findViewById(R.id.add_mod_nombre);
        mDescripcionMod = (TextView) findViewById(R.id.add_mod_desc);

        // Modificar la descripción según el valor seleccionado en el Spinner de Nombre de Modalidad
        // Vamos a configurar un listener para detectar el elemento seleccionado
        mNombreMod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Resources res = getResources();
                String[] descs = res.getStringArray(R.array.descripcionesModalidadesCombate);

                switch (position){
                    case 0: {
                        mDescripcionMod.setText(descs[0]);
                        break;
                    }
                    case 1: {
                        mDescripcionMod.setText(descs[1]);
                        break;
                    }
                    case 2: {
                        mDescripcionMod.setText(descs[2]);
                        break;
                    }
                    case 3: {
                        mDescripcionMod.setText(descs[3]);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
