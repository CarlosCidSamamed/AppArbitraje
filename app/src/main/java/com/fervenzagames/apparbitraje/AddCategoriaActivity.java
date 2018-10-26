package com.fervenzagames.apparbitraje;

import android.content.Intent;
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
    private DatabaseReference mCampDB;

    private String idMod; // ID de la Modalidad a la que pertenece esta Categoría
    private String nombreMod; // Nombre de la Modalidad a la que pertenece esta Categoría

    private String idCamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categoria);

        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idMod = extras.getString("idMod");
        idCamp = extras.getString("idCamp");
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idCamp).child(idMod);
        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);

        nombreMod = extras.getString("nombreMod");

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
        Toast.makeText(AddCategoriaActivity.this, "El Nombre de la Modalidad es " + nombreMod, Toast.LENGTH_LONG).show(); // OK
        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoria();
            }
        });

    }

    public void addCategoria(){

        String nombreCat = nombrarCategoria();
        // Comprobar el nombre generado para la categoría con un Toast.
        Toast.makeText(AddCategoriaActivity.this, "El nombre de la Categoría es " + nombreCat, Toast.LENGTH_LONG).show();

    }

    public String nombrarCategoria(){

        // Para añadir una categoría se ha de generar el nombre a partir de los valores de los spinners de Sexo, Edad y Peso.
        // Además será necesario el nombre de la Modalidad a la que pertenece la Categoría
        /* Ejemplo: SD -85 M ABS -->
            Modalidad : Sanda
            Peso : Menos de 85 kg
            Sexo : Masculino
            Edad : Absoluto
         */
        String sexo = mSexoSpinner.getSelectedItem().toString();
        String edad = mEdadSpinner.getSelectedItem().toString();
        String peso = mPesoSpinner.getSelectedItem().toString();

        String nombreCat = "";

        // Para el nombre de la Categoría deberemos obtener el nombre de la Modalidad. Ese dato se pasa desde DetalleModalidad como extra dentro de un Bundle.
        // En esta clase se almacena en nombreMod.

        switch(nombreMod){
            case "Sanda SD":
            {
                nombreCat = nombreCat + "SD";
                break;
            }
            case "Qingda QD":
            {
                nombreCat = nombreCat + "QD";
                break;
            }
            case "Shuai Jiao SJ":
            {
                nombreCat = nombreCat + "SJ";
                break;
            }
            case "Kungfu Combat KC":{
                nombreCat = nombreCat + "KC";
                break;
            }
        }


        // Para el peso deberemos evaluar la cadena que devuelve el spinner.
        switch (peso){
            case "Menos de 48 kg":
            {
                nombreCat = nombreCat + " -48 ";
                break;
            }
            case "Menos de 52 kg":
            {
                nombreCat = nombreCat + " -52 ";
                break;
            }
            case "Menos de 56 kg":
            {
                nombreCat = nombreCat + " -56 ";
                break;
            }
            case "Menos de 60 kg":
            {
                nombreCat = nombreCat + " -60 ";
                break;
            }
            case "Menos de 65 kg":
            {
                nombreCat = nombreCat + " -65 ";
                break;
            }
            case "Menos de 70 kg":
            {
                nombreCat = nombreCat + " -70 ";
                break;
            }
            case "Menos de 75 kg":
            {
                nombreCat = nombreCat + " -75 ";
                break;
            }
            case "Menos de 80 kg":
            {
                nombreCat = nombreCat + " -80 ";
                break;
            }
            case "Menos de 85 kg":
            {
                nombreCat = nombreCat + " -85 ";
                break;
            }
            case "Menos de 90 kg":
            {
                nombreCat = nombreCat + " -90 ";
                break;
            }
            case "90 kg o más":
            {
                nombreCat = nombreCat + " +90 ";
                break;
            }
        }

        // Sexo
        switch (sexo){
            case "Femenino":
            {
                nombreCat = nombreCat + " F ";
                break;
            }
            case "Masculino":
            {
                nombreCat = nombreCat + " M ";
                break;
            }
            case "Mixto":
            {
                nombreCat = nombreCat + " X ";
                break;
            }
        }

        // Edad
        switch (edad){
            case "PreInfantil":
            {
                nombreCat = nombreCat + " PRE-INF ";
                break;
            }
            case "Infantil":
            {
                nombreCat = nombreCat + " INF ";
                break;
            }
            case "Cadete":
            {
                nombreCat = nombreCat + " CAD ";
                break;
            }
            case "Junior":
            {
                nombreCat = nombreCat + " JUN ";
                break;
            }
            case "Absoluto":
            {
                nombreCat = nombreCat + " ABS ";
                break;
            }
            case "Sénior A (+40)":
            {
                nombreCat = nombreCat + " SEN_A ";
                break;
            }
            case "Sénior B (+50)":
            {
                nombreCat = nombreCat + " SEN_B ";
                break;
            }
            case "Sénior C (+60)":
            {
                nombreCat = nombreCat + " SEN_C ";
                break;
            }
            case "Sénior D (+70)":
            {
                nombreCat = nombreCat + " SEN_D ";
                break;
            }
        }

        return nombreCat;
    }
}
