package com.fervenzagames.apparbitraje;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class AddCampeonatoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mNombre;
    private TextInputLayout mFecha;
    private TextInputLayout mLugar;
    private Spinner mTipo;
    private Button mAddCategoriasBtn;
    private Button mGuardarBtn;

    private DatabaseReference campsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campeonato);

        campsDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos");

        mToolbar = (Toolbar) findViewById(R.id.add_camp_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Campeonato");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNombre = (TextInputLayout) findViewById(R.id.add_camp_nombre);
        mFecha = (TextInputLayout) findViewById(R.id.add_camp_fecha);
        mLugar = (TextInputLayout) findViewById(R.id.add_camp_lugar);
        mTipo = (Spinner) findViewById(R.id.add_camp_tipo);

        mAddCategoriasBtn = (Button) findViewById(R.id.add_camp_modalidades_btn);
        mGuardarBtn = (Button) findViewById(R.id.add_camp_guardar_btn);

        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCampeonato();
            }
        });

        mAddCategoriasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addCampeonato();
            }
        });

    }

    private void addCampeonato(){
        String nombre = mNombre.getEditText().getText().toString();
        String fecha = mFecha.getEditText().getText().toString();
        String lugar = mLugar.getEditText().getText().toString();
        String tipo = mTipo.getSelectedItem().toString();

        if(!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(fecha) && !TextUtils.isEmpty(lugar)){

            String id = campsDB.push().getKey();
            List<Arbitros> listaArbitros = null;

            Campeonatos camp = new Campeonatos(id, nombre, fecha, lugar, tipo, listaArbitros);

            campsDB.child(id).setValue(camp);

            Toast.makeText(this, "Campeonato añadido a la BD", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(AddCampeonatoActivity.this, "Debe rellenar el formulario en su totalidad antes de pulsar el botón de Guardar", Toast.LENGTH_LONG).show();
        }
    }
}
