package com.fervenzagames.apparbitraje;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoriaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Spinner mSexoSpinner;
    private Spinner mEdadSpinner;
    private TextView mDescEdad;
    private Spinner mPesoSpinner;
    private Button mGuardarBtn;

    private DatabaseReference mCatDB; // Referencia a las Categorías
    private DatabaseReference mModDB; // Referencia a la Modalidad a la que pertenece esta Categoría

    private String idMod; // ID de la Modalidad a la que pertenece esta Categoría*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categoria);

        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias");
        idMod = getIntent().getStringExtra("idMod");
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idMod);

        mToolbar = (Toolbar) findViewById(R.id.add_cat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Categoría");

        mSexoSpinner = (Spinner) findViewById(R.id.sexo_spinner);
        mEdadSpinner = (Spinner) findViewById(R.id.edad_spinner);
        mDescEdad = (TextView) findViewById(R.id.desc_edad_text);
        mPesoSpinner = (Spinner) findViewById(R.id.peso_spinner);
        mGuardarBtn = (Button) findViewById(R.id.add_cat_guardar_btn);

        // Modficar la descripción de la categoría de Edad según el valor del spinner correspondiente.
        mEdadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Resources res = getResources();
                String[] descs = res.getStringArray(R.array.descripcionCatEdad);

                mDescEdad.setText(descs[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Guardar los datos en la BD.
        // Mostrar el ID de la Modalidad con un Toast.
        Toast.makeText(AddCategoriaActivity.this, "El ID de la Modalidad es " + idMod, Toast.LENGTH_SHORT).show(); // OK
        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoria();
            }
        });

    }

    public void addCategoria(){

    }
}
