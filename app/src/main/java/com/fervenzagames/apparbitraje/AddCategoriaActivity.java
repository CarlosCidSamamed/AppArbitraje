package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
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
import com.fervenzagames.apparbitraje.Models.Combates;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

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

        final String nombreCat = nombrarCategoria();
        // Comprobar el nombre generado para la categoría con un Toast.
        Toast.makeText(AddCategoriaActivity.this, "El nombre de la Categoría es " + nombreCat, Toast.LENGTH_LONG).show();

        // Usar el id de la Modalidad para crear una rama en Categorías que tenga el mismo id que la modalidad a la que pertenece la Categoría que vamos a crear.
        // El idMod se obtiene de los extras de DetalleModalidad
        Intent intent = getIntent();
        final String idMod = intent.getExtras().getString("idMod");

        // El nombre de la cateogoría se genera con el método nombrarCategoria(). Se almacena en la variable nombreCat.
        // El resto de los valores de sexo, edad y peso se obtienen de los spinners de esta actividad.
        String sexo = mSexoSpinner.getSelectedItem().toString();
        String edad = mEdadSpinner.getSelectedItem().toString();
        String peso = mPesoSpinner.getSelectedItem().toString();

        // Lista de Combates vacía
        List<Combates> listaCombates = null;

        // Insertar la Categoría en la BD para obtener su ID único. Dicho ID lo almacenamos en idCat.
        final String idCat = mCatDB.child(idMod).push().getKey();

        // Creamos el objeto de tio Categorias
        final Categorias cat = new Categorias(idCat, nombreCat, edad, sexo, peso, listaCombates);

        // Comprobación de duplicados ¿Existe ya esta categoría para esta modalidad?
        // Consulta
        Query consulta = mCatDB.child(idMod);
        Query consulta2 = consulta
                .orderByChild("nombre")
                .equalTo(nombreCat)
                .limitToFirst(1);

        consulta2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){ // Comprobar si existe esta Categoría en esta Modalidad
                    // Mensaje de aviso por pantalla
                    Toast.makeText(AddCategoriaActivity.this, "Ya existe esa Categoría para esta Modalidad en la BD. Compruebe el formulario...", Toast.LENGTH_LONG).show();
                } else { // Si no existe esa Categoría en la BD se almacena donde le corresponde.
                    mCatDB.child(idMod).child(idCat).setValue(cat);
                    Toast.makeText(AddCategoriaActivity.this, "Se ha añadido la Categoría " + nombreCat + " a la Modalidad " + nombreMod + "(BD)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public String nombrarCategoria(){

        // Para añadir una categoría se ha de generar el nombre a partir de los valores de los spinners de Sexo, Edad y Peso.
        // Además será necesario el nombre de la Modalidad a la que pertenece la Categoría
        /* Ejemplo: SD -85 M ABS -->
            Modalidad : Sanda
            Peso : Menos de 85 kg
            Edad : Absoluto
            Sexo : Masculino
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

        // Edad
        switch (edad){
            case "01_PreInfantil":
            {
                nombreCat = nombreCat + "01_PRE-INF";
                break;
            }
            case "02_Infantil":
            {
                nombreCat = nombreCat + "02_INF";
                break;
            }
            case "03_Cadete":
            {
                nombreCat = nombreCat + "03_CAD";
                break;
            }
            case "04_Junior":
            {
                nombreCat = nombreCat + "04_JUN";
                break;
            }
            case "05_Absoluto":
            {
                nombreCat = nombreCat + "05_ABS";
                break;
            }
            case "06_Sénior A (+40)":
            {
                nombreCat = nombreCat + "06_SEN_A";
                break;
            }
            case "07_Sénior B (+50)":
            {
                nombreCat = nombreCat + "07_SEN_B";
                break;
            }
            case "08_Sénior C (+60)":
            {
                nombreCat = nombreCat + "08_SEN_C";
                break;
            }
            case "09_Sénior D (+70)":
            {
                nombreCat = nombreCat + "09_SEN_D";
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

        return nombreCat;
    }
}
