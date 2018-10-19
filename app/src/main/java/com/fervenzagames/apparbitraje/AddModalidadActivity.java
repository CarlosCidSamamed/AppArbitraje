package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Categorias;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddModalidadActivity extends AppCompatActivity {

    private TextView nombreCamp;
    private Toolbar mToolbar;

    private Spinner mNombreMod;
    private TextView mDescripcionMod;

    private DatabaseReference modsDB;

    private Button mGuardarModBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modalidad);

        mToolbar = (Toolbar) findViewById(R.id.add_mod_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Modalidad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nombreCamp = (TextView) findViewById(R.id.add_mod_nombre_campeonato);

        modsDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades");
        mGuardarModBtn = (Button) findViewById(R.id.add_mod_guardar_btn);

        Intent intent = getIntent();
        String nombreCampeonato = intent.getStringExtra("NombreCampeonato");
        // String idCamp = intent.getStringExtra("IdCamp");    // El ID alfanumérico que genera Firebase RT DB al añadir un campeonato a la BD.

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

        // Boton Guardar datos setOnClickListener que invoca el método addModalidad
        mGuardarModBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addModalidad();
            }
        });

    }

    public void addModalidad(){
        // Usar el idCamp para añadir una rama correspondiente a este campeonato a la rama de Modalidades.
        // Obtener el ID del Intent que viene de la actividad anterior (AddCampeonatosActivity)
        Intent intent = getIntent();
        String idCamp = intent.getStringExtra("IdCamp");    // El ID alfanumérico que genera Firebase RT DB al añadir un campeonato a la BD.

        String nombreMod = mNombreMod.getSelectedItem().toString();
        String descMod = mDescripcionMod.getText().toString();

        // Lista vacía de Categorías
        List<Categorias> catList = null;
        // Crear objeto de tipo Modalidad
        Modalidades mod = new Modalidades(idCamp, nombreMod, descMod, catList);

        // Insertar la Modalidad en la BD.
        // Para poder añadir varias Modalidades a un Campeonato debo hacer un push para generar un ID único para cada Modalidad dentro de ese Campeonato.
        String idMod = modsDB.child(idCamp).push().getKey();

        // Comprobar si ya existe en este campeonato la Modalidad que queremos insertar.
        // Mensaje de Aviso en pantalla.
        // En caso contrario, añadir la Modalidad en la rama que le corresponde.
        modsDB.child(idCamp).child(idMod).setValue(mod);

        Toast.makeText(AddModalidadActivity.this, "Se ha añadido la Modalidad de " + nombreMod + " a la BD." , Toast.LENGTH_LONG).show();

    }
}
