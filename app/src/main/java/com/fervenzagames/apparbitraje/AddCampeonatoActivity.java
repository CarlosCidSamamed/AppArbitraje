package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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
                addModalidades();
            }
        });

    }

    private void addCampeonato(){

        String nombre = mNombre.getEditText().getText().toString();
        String fecha = mFecha.getEditText().getText().toString();
        String lugar = mLugar.getEditText().getText().toString();
        String tipo = mTipo.getSelectedItem().toString();

        if(!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(fecha) && !TextUtils.isEmpty(lugar)){

            final String id = campsDB.push().getKey();
            List<Arbitros> listaArbitros = null;

            final Campeonatos camp = new Campeonatos(id, nombre, fecha, lugar, tipo, listaArbitros);

            // Comprobar si existe este campeonato en la BD
            // Creamos la consulta que nos devuelve el primer campeonato que encuentre con ese nombre.
            Query consulta = campsDB
                    .orderByChild("nombre")
                    .equalTo(nombre)
                    .limitToFirst(1);

            consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Toast.makeText(AddCampeonatoActivity.this,
                                "Ya existe un Campeonato en la BD con esos datos. Compruebe el formulario...",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Si no existe ningún campeonato con ese nombre se almacenan sus datos en la BD

                        campsDB.child(id).setValue(camp);

                        Toast.makeText(AddCampeonatoActivity.this, "Campeonato añadido a la BD", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            Toast.makeText(AddCampeonatoActivity.this, "Debe rellenar el formulario en su totalidad antes de pulsar el botón de Guardar", Toast.LENGTH_LONG).show();
        }
    }

    public void addModalidades() {

        String nombre = mNombre.getEditText().getText().toString();
        String fecha = mFecha.getEditText().getText().toString();
        String lugar = mLugar.getEditText().getText().toString();
        String tipo = mTipo.getSelectedItem().toString();

        if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(fecha) && !TextUtils.isEmpty(lugar)) {

            Intent addModIntent = new Intent(this, AddModalidadActivity.class);
            addModIntent.putExtra("NombreCampeonato", nombre);
            startActivity(addModIntent);

        } else {
            Toast.makeText(AddCampeonatoActivity.this, "Debe rellenar el formulario en su totalidad antes de pulsar el botón de Añadir Modalidades", Toast.LENGTH_LONG).show();
        }
    }

    // Para añadir un campeonato a la BD deberé asegurarme de que existe otro campeonato con el mismo nombre en la BD. Así evitaremos duplicados.
    private boolean comprobarDuplicados(Campeonatos camp)
    {
        // Obtenemos el nombre del campeonato que queremos guardar en la BD
        String nombre = mNombre.getEditText().getText().toString(); // Nombre a buscar en los campeonatos de la BD.

        // Creamos la consulta que nos devuelve el primer campeonato que encuentre con ese nombre.
        Query consulta = campsDB
                .orderByChild("nombre")
                .equalTo(nombre)
                .limitToFirst(1);

        // Comprobamos si la consulta ha encontrado algún campeonato con ese nombre o no.
        boolean existe;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean e;
                if(dataSnapshot.exists()){ // Existe un campeonato en la BD con ese nombre
                    e = true;
                } else {
                    e = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return false;
    }
}
