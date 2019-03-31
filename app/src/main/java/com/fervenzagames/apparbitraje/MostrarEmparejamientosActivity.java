package com.fervenzagames.apparbitraje;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CompetidoresList;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.MesaArbitrajeActivity;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.Categorias;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.Models.Emparejamientos;
import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.fervenzagames.apparbitraje.Models.Emparejamientos.EsFinal.NO;
import static com.fervenzagames.apparbitraje.Models.Emparejamientos.EsFinal.SI;
import static com.fervenzagames.apparbitraje.Models.Emparejamientos.EsFinal.TERCEROS;

public class MostrarEmparejamientosActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mNombreCat;

    private TextView mPruebaArray;

    private Button mSorteoBoton;

    private DatabaseReference mCampeonatoDB;
    private DatabaseReference mModDB;
    private DatabaseReference mCatDB;
    private DatabaseReference mCompDB;
    private DatabaseReference mCombatesDB;
    private DatabaseReference mAsaltosDB;
    private DatabaseReference mEmparejamientosDB;


    private Integer[] array;
    private List<Competidores> mListaComp;
    private List<Emparejamientos> mListaEmpa;


    // CUADRO SUPERIOR
    private List<Emparejamientos> cuadroSuperior = new ArrayList<>();
    // CUADRO INFERIOR
    private List<Emparejamientos> cuadroInferior = new ArrayList<>();

    private int idLayout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_mostrar_emparejamientos);

        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        final String idCamp = extras.getString("idCamp");
        final String idMod = extras.getString("idMod");
        final String idCat = extras.getString("idCat");
        idLayout = extras.getInt("layout");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(idLayout, null);

        setContentView(layout);

        mToolbar = (Toolbar) findViewById(R.id.mostrar_emp_app_bar);
        setSupportActionBar(mToolbar);

        switch(idLayout){
            case R.layout.emparejamientos_2_competidores_layout:{
                mNombreCat = (TextView) findViewById(R.id.dos_comp_nombreCat);
                mPruebaArray = (TextView) findViewById(R.id.pruebaArray);
                array = sorteoCompetidores(2);
                // asignarCompetidores(array);

                mSorteoBoton = (Button) findViewById(R.id.dos_comp_asignar_puestos_btn);

                //simularCuadro();
                break;
            }
            case R.layout.emparejamientos_3_competidores_layout:{
                mNombreCat = (TextView) findViewById((R.id.tres_comp_nombreCat));
                mPruebaArray = (TextView) findViewById(R.id.tres_comp_pruebaArray);
                array = sorteoCompetidores(3);
                //asignarCompetidores(array);
                //simularCuadro();

                mSorteoBoton = (Button) findViewById(R.id.tres_comp_asignar_puestos_btn);

                break;
            }
            case R.layout.emparejamientos_8_competidores_layout:{
                mNombreCat = (TextView) findViewById(R.id.ocho_comp_nombreCat);
                break;
            }
        }

        mCampeonatoDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos").child(idCamp);        // Campeonato Actual.
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Modalidades").child(idMod);                // Modalidad Actual.
        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);    // Categoría Actual.
        mCompDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores");                           // Lista de Competidores en la BD.
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Combates");                           // Lista de Combates en la BD.
        mAsaltosDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Asaltos");                             // Lista de Asaltos en la BD.
        mEmparejamientosDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Emparejamientos");             // Rama con la información de los emparejamientos en la BD.

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String titulo = dataSnapshot.child("nombre").getValue().toString();
                mNombreCat.setText(titulo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Al pulsar el botón sorteo se celebra el sorteo de posiciones y se asignan los competidores a sus puestos en los emparejamientos.
        mSorteoBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarCompetidores(array);
            }
        });
    }

    //region Generar Números Aleatorios
    public Integer[] sorteoCompetidores(int numCompetidores){

        Random r = new Random();
        Integer[] array = new Integer[numCompetidores];

        /*for(int i = 0; i < numCompetidores; i++){
            int num = r.nextInt(numCompetidores-1);
            array[i] = num;
        }
        // Corrección de duplicados para 2 competidores. El generador de números no funciona bien cuando el número máximo es 2.
        if(numCompetidores == 2){
            if(array[0] == array[1]){
                if(array[0] == 1) {
                    array[1] = 0;
                } else if(array[0] == 0){
                    array[0] = 1;
                }
            }
        }


        // Eliminar duplicados
        for(int i = 0; i < numCompetidores; i++){
            for(int j = 0; j < numCompetidores; j ++){
                if(i != j){
                    if(array[i] == array[j]){
                        array[i] = r.nextInt(numCompetidores-1);
                    }
                }
            }
        }*/

        for(int i = 0; i < array.length; i++){ // Rellenar arrays sin duplicados
            array[i] = i;
        }

        Collections.shuffle(Arrays.asList(array)); // "Barajar" el array

        // Mostrar los elementos del array
        String res = "";
        for(int i = 0; i < numCompetidores; i++){
            res += i + " --> " + array[i] + "\n";
        }
        mPruebaArray.setText(res);

        return array;
    }
    //endregion

    //region Asignar Competidores
    // Lee la lista de competidores de la BD y le asigna a cada competidor el número que se ha almacenado en el array de Integer
    // que se pasa como parámetro.
    public void asignarCompetidores(final Integer[] array){

        final List<Competidores> listaComp = new ArrayList<>();

        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        String idMod = extras.getString("idMod");
        String idCat = extras.getString("idCat");

        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final String sexo = dataSnapshot.child("sexo").getValue().toString();
                    final String peso = dataSnapshot.child("peso").getValue().toString();
                    final String edad = dataSnapshot.child("edad").getValue().toString();

                    Query consulta = mCompDB;   // Lista de los competidores

                    consulta.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaComp.clear();
                            for(DataSnapshot compSnapshot: dataSnapshot.getChildren()){
                                Competidores comp = compSnapshot.getValue(Competidores.class);
                                if((comp.getSexo().equals(sexo)) &&
                                        (comp.getCatEdad().equals(edad)) &&
                                        (comp.getCatPeso().equals(peso))){
                                    listaComp.add(comp);
                                }
                            }
                            CompetidoresList adapter = new CompetidoresList(MostrarEmparejamientosActivity.this, listaComp);
                            adapter.notifyDataSetChanged();
                            if(listaComp.size() == 0){
                                Toast.makeText(MostrarEmparejamientosActivity.this,
                                        "No existe ningún competidor en la BD que cumpla esos requisitos. Sexo: " + sexo
                                                + ", CatEdad: " + edad + ", CatPeso: " + peso,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Una vez que tenemos el array de números aleatorios y la lista de Competidores debemos asignar su número a cada competidor.
                            // Recorrer el array para comprobar el número aleatorio que se ha almacenado en cada una de sus posiciones.
                            // Con ese número se decidirá la ranura en la que se cargarán los datos del competidor de la lista.
                            switch (idLayout){
                                case R.layout.emparejamientos_2_competidores_layout:{
                                    CircleImageView fotoUno = (CircleImageView) findViewById(R.id.dos_comp_foto_1);
                                    TextView nombreUno = (TextView) findViewById(R.id.dos_comp_nombre_1);
                                    TextView paisUno = (TextView) findViewById(R.id.dos_comp_pais_1);

                                    CircleImageView fotoDos = (CircleImageView) findViewById(R.id.dos_comp_foto_2);
                                    TextView nombreDos = (TextView) findViewById(R.id.dos_comp_nombre_2);
                                    TextView paisDos = (TextView) findViewById(R.id.dos_comp_pais_2);

                                    // Recorrer el array
                                    for(int i = 0; i < array.length; i++){
                                        if(array[i] == 0){ // Cargar datos en Competidor 1
                                            String nombreCompleto = listaComp.get(i).getNombre() + " " + listaComp.get(i).getApellido1() + " " + listaComp.get(i).getApellido2();
                                            nombreUno.setText(nombreCompleto);
                                            paisUno.setText(listaComp.get(i).getPais());
                                            Picasso.get().load(listaComp.get(i).getFoto()).into(fotoUno);
                                        } if(array[i] == 1){ // Cargar datos en Competidor 2
                                            String nombreCompleto = listaComp.get(i).getNombre() + " " + listaComp.get(i).getApellido1() + " " + listaComp.get(i).getApellido2();
                                            nombreDos.setText(nombreCompleto);
                                            paisDos.setText(listaComp.get(i).getPais());
                                            Picasso.get().load(listaComp.get(i).getFoto()).into(fotoDos);
                                        }
                                    }

                                    // Una vez que se han asignado los datos a los competidores correspondientes
                                    // en las ranuras de los emparejamientos se guardan los combates resultantes en la BD. (Y sus Asaltos correspondientes)

                                    int tamano = listaComp.size();
                                    String[][] arrayBidim = new String[tamano][2];
                                    arrayBidim = addCombatesEmparejamientos(array, listaComp);
                                    final String idRojo = arrayBidim[0][1];
                                    final String idAzul = arrayBidim[1][1];

                                    // Click sobre el botón Empezar Combate
                                    Button comenzarBtn = (Button) findViewById(R.id.dos_comp_emmpezar_combate_btn);
                                    comenzarBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Recopilar datos a pasar al intent. (Bundle)
                                            // Crear intent para arbitraje (Mesa)
                                            Intent arbitrarIntent = new Intent(MostrarEmparejamientosActivity.this, MesaArbitrajeActivity.class);
                                            // Pasarle datos del Bundle al intent.
                                            Intent intent = getIntent();
                                            Bundle ext = intent.getExtras();
                                            ext.putString("idRojo", idRojo);
                                            ext.putString("idAzul", idAzul);
                                            arbitrarIntent.putExtras(ext);
                                            // Lanzar intent arbitraje (Mesa)
                                            startActivity(arbitrarIntent);
                                        }
                                    });
                                    break;
                                }
                                case R.layout.emparejamientos_3_competidores_layout:{
                                    // Cargar datos de los 3 competidores
                                    CircleImageView fotoUno = (CircleImageView) findViewById(R.id.tres_comp_foto_1);
                                    TextView nombreUno = (TextView) findViewById(R.id.tres_comp_nombre_1);
                                    TextView paisUno = (TextView) findViewById(R.id.tres_comp_pais_1);

                                    CircleImageView fotoDos = (CircleImageView) findViewById(R.id.tres_comp_foto_2);
                                    TextView nombreDos = (TextView) findViewById(R.id.tres_comp_nombre_2);
                                    TextView paisDos = (TextView) findViewById(R.id.tres_comp_pais_2);

                                    CircleImageView fotoTres = (CircleImageView) findViewById(R.id.tres_comp_foto_3);
                                    TextView nombreTres = (TextView) findViewById(R.id.tres_comp_nombre_3);
                                    TextView paisTres = (TextView) findViewById(R.id.tres_comp_pais_3);

                                    // Recorrer el array
                                    for(int i = 0; i < array.length; i++){
                                        String nombreCompleto = listaComp.get(i).getNombre() + " " + listaComp.get(i).getApellido1() + " " + listaComp.get(i).getApellido2();
                                        switch(array[i]){
                                            case 0:{
                                                nombreUno.setText(nombreCompleto);
                                                paisUno.setText(listaComp.get(i).getPais());
                                                Picasso.get().load(listaComp.get(i).getFoto()).into(fotoUno);
                                                break;
                                            }
                                            case 1:{
                                                nombreDos.setText(nombreCompleto);
                                                paisDos.setText(listaComp.get(i).getPais());
                                                Picasso.get().load(listaComp.get(i).getFoto()).into(fotoDos);
                                                break;
                                            }
                                            case 2:{
                                                nombreTres.setText(nombreCompleto);
                                                paisTres.setText(listaComp.get(i).getPais());
                                                Picasso.get().load(listaComp.get(i).getFoto()).into(fotoTres);
                                                break;
                                            }
                                        }
                                    }

                                    // Una vez que se han asignado los datos a los competidores correspondientes
                                    // en las ranuras de los emparejamientos se guardan los combates resultantes en la BD. (Y sus Asaltos correspondientes)

                                    int tamano = listaComp.size();
                                    String[][] arrayBidim = new String[tamano][2];
                                    arrayBidim = addCombatesEmparejamientos(array, listaComp);
                                    final String idRojo = arrayBidim[0][1];
                                    final String idAzul = arrayBidim[2][1];

                                    // Comportamiento Botones Empezar Combate
                                    Button empezarUnoBtn = (Button) findViewById(R.id.tres_comp_comenzar_1_btn);
                                    empezarUnoBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent empezarUnoIntent = new Intent(MostrarEmparejamientosActivity.this, MesaArbitrajeActivity.class);
                                            Intent intent = getIntent();
                                            Bundle ext = intent.getExtras();
                                            // Toast.makeText(MostrarEmparejamientosActivity.this, "MostrarEmparejamientos --- DNI Rojo --> " + listaComp.get(0).getDni(), Toast.LENGTH_SHORT).show();
                                            ext.putString("idRojo", idRojo);
                                            ext.putString("idAzul", idAzul);
                                            empezarUnoIntent.putExtras(ext);
                                            startActivity(empezarUnoIntent);
                                        }
                                    });
                                    break;
                                }
                                case R.layout.emparejamientos_8_competidores_layout:{
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //endregion

    //region Añadir Combates de los Emparejamientos
    // A este método se le pasan como parámetros el array del sorteo y la lista de competidores para saber en qué posición del cuadro se encuentra cada uno de los competidores.
    public String[][] addCombatesEmparejamientos(Integer[] array, List<Competidores> listaComp){
        // Se recorre el array y la lista y se generan un array bidimensional con el índice del array y el id del Competidor que le corresponde en la lista.
        // Ejemplo --> arrayBidimensional[0] = array[0], listaComp.get(0)
        // Para poder mantener el String del idComp el array será de String y se convertirá en String el índice del array de Integer.
        int tamano = listaComp.size();
        String[][] arrayBidim = new String[tamano][2];

        for(int i = 0; i < tamano; i++){
            arrayBidim[i][0] = array[i].toString();
            arrayBidim[i][1] = listaComp.get(array[i]).getDni(); // El DNI del competidor cuyo indice es igual al valor almacenado en el array que obtenemos en el sorteo.
        }

        // Bucle Añadir Combates a la BD (desde i = 0 hasta listaComp.size --> tamano)

        // Buscar en la BD los datos sobre los competidores

        // Añadir combate a la BD y sus Asaltos
        // addCombate(parámetros);

        // Fin bucle añadir combates

        // Bucle Añadir los Combates a los Emparejamientos
        // Los datos que definen el árbol de emparejamientos serán distintos según el número de combates, es decir, el número de competidores define el número de combates
        // y este, a su vez, define la estructura del árbol de emparejamientos. Es decir, no será igual el cuadro para 4 competidores que para 8 competidores.
        // Dependiendo del número de competidores habrá un número de emparejamientos tanto en el cuadro superior como en el inferior.
        // Ejemplo: 8 competidores --> 7 combates en el cuadro superior y 4 combates en el inferior --> Total 11 combates.

        //region Decidir número de Emparejamientos del cuadro Superior e Inferior.
        int numEmparejamientosSup = 0;  // Número de Emparejamientos del cuadro superior.
        int numEmparejamientosInf = 0;  // Número de Emparejamientos del cuadro inferior.
        switch(tamano){ // Dependiendo del número de competidores, el valor de numEmparejamientos será distinto, tanto en el cuadro superior como en el inferior.
            case 2:{ // Final Directa
                numEmparejamientosSup = 1;
                break;
            }
            case 3:{ // 1 semi y final
                numEmparejamientosSup = 2;
                break;
            }
            case 4:{ // Semis y Final
                numEmparejamientosSup = 3;
                break;
            }
            case 5:{
                numEmparejamientosSup = 4;
                numEmparejamientosInf = 1;
                break;
            }
            case 6:{
                numEmparejamientosSup = 5;
                numEmparejamientosInf = 2;
                break;
            }
            case 7:{
                numEmparejamientosSup = 6;
                numEmparejamientosInf = 3;
                break;
            }
            case 8:{
                numEmparejamientosSup = 7;
                numEmparejamientosInf = 4;
                break;
            }
            case 9:{
                numEmparejamientosSup = 8;
                numEmparejamientosInf = 5;
                break;
            }
            case 10:{
                numEmparejamientosSup = 9;
                numEmparejamientosInf = 6;
            }
        }
        //endregion

        // Una vez decidido el número de Emparejamientos del cuadro superior e inferior deberemos especificar la información que definirá la estructura de los emparejamientos.


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String numCombate = "";

        switch (numEmparejamientosSup){
            case 1: { // Final Directa, 2 Competidores

                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[0][1], arrayBidim[1][1], "", "", SI, cuadroSuperior);
                Toast.makeText(this, "Número de Combate " + numCombate, Toast.LENGTH_SHORT).show();

                break;
            }
            case 2: { // Uno se clasifica directo a la final y los otros dos hacen semifinal, 3 Competidores
                // Semi
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[0][1], arrayBidim[2][1], "002-R", "", NO, cuadroSuperior);

                // Toast.makeText(this, "Tamaño de CuadroSuperior ----> " + cuadroSuperior.size(), Toast.LENGTH_SHORT).show();

                if(cuadroSuperior.size() > 0){
                    // Final
                    numCombate = "002";
                    addCombate(numCombate, extras, arrayBidim[1][1], cuadroSuperior.get(0).getIdGanador(), "", "", SI, cuadroSuperior);
                }

                break;
            }
            case 3: { // Semis y final, 4 Competidores

                // Cuadro Superior
                // Semis
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[0][1], arrayBidim[3][1],
                        "003-R", "004-A", NO, cuadroSuperior);

                numCombate = "002";
                addCombate(numCombate, extras, arrayBidim[2][1], arrayBidim[1][1],
                        "003-A", "004-R", NO, cuadroSuperior);
                // Final
                numCombate = "003";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdGanador(), cuadroSuperior.get(1).getIdGanador(),
                        "", "", SI, cuadroSuperior);
                // Cuadro Inferior
                // Repesca 1
                numCombate = "004";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdPerdedor(), cuadroSuperior.get(1).getIdPerdedor(),
                        "", "", TERCEROS, cuadroInferior);

                break;

            }
            case 4: { // 1 combate de cuartos, dos semis y final, 5 Competidores
                // Cuartos
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[0][1], arrayBidim[4][1], // 1 vs 5
                        "002-R", "006-R", NO, cuadroSuperior);

                // Semi 1
                numCombate = "002";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdGanador(), arrayBidim[2][1], // ganador001 vs 3
                        "004-R", "005-A", NO, cuadroSuperior);

                // Semi 2
                numCombate = "003";
                addCombate(numCombate, extras, arrayBidim[3][1], arrayBidim[1][1], // 4 vs 2
                        "004-A", "005-A", NO, cuadroSuperior);

                // Final
                numCombate = "004";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdGanador(), cuadroSuperior.get(2).getIdGanador(), // ganador002 vs ganador003
                        "", "", SI, cuadroSuperior);

                // Cuadro Inferior
                // Repesca 1
                numCombate = "005";
                addCombate(numCombate, extras, cuadroSuperior.get(2).getIdPerdedor(), cuadroSuperior.get(0).getIdPerdedor(), // perdedor003 vs perdedor001
                        "", "", TERCEROS, cuadroInferior);

                // Repesca 2 -- El perdedor del emp002 es tercero --> Hace cuartos y semis en el cuadro superior
                numCombate = "006";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdPerdedor(), "",
                        "", "", TERCEROS, cuadroInferior);

                break;
            }
            case 5: { // 2 cuartos, 2 semis y final , 6 Competidores
                // Cuartos 1
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[0][1], arrayBidim[5][1], // 1 vs 6
                        "003-R", "006-A", NO, cuadroSuperior);

                // Cuartos 2
                numCombate = "002";
                addCombate(numCombate, extras, arrayBidim[4][1], arrayBidim[1][1],// 5 vs 2
                        "004-A", "007-R", NO, cuadroSuperior);

                // Semis 1
                numCombate ="003";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdGanador(), arrayBidim[2][1], // ganador001 vs 3
                        "005-R", "007-A", NO, cuadroSuperior);

                // Semis 2
                numCombate = "004";
                addCombate(numCombate, extras, arrayBidim[3][1], cuadroSuperior.get(1).getIdGanador(), // 4 vs ganador002
                        "005-A", "006-R", NO, cuadroSuperior);

                // Final
                numCombate = "005";
                addCombate(numCombate, extras, cuadroSuperior.get(2).getIdGanador(), cuadroSuperior.get(3).getIdGanador(), // ganador003 vs ganador004
                        "", "", SI, cuadroSuperior);

                // Cuadro Inferior
                // 1
                numCombate = "006";
                addCombate(numCombate, extras, cuadroSuperior.get(3).getIdPerdedor(), cuadroSuperior.get(0).getIdPerdedor(), // perdedor004 vs perdedor001
                        "", "", TERCEROS, cuadroInferior);

                // 2
                numCombate = "007";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdPerdedor(), cuadroSuperior.get(2).getIdPerdedor(), // perdedor002 vs perdedor003
                        "", "", TERCEROS, cuadroInferior);
                break;
            }
            case 6: { // 3 cuartos, 2 semis y 1 final 7 Competidores
                // Cuartos
                // 1
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "004-A", "007-R", NO, cuadroSuperior);

                // 2
                numCombate = "002";
                addCombate(numCombate, extras, arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "005-R", "008-A", NO, cuadroSuperior);

                // 3
                numCombate = "003";
                addCombate(numCombate, extras, arrayBidim[6][1], arrayBidim[1][1], // 7 vs 2
                        "005-A", "008-R", NO, cuadroSuperior);

                // Semis
                // 1
                numCombate = "004";
                addCombate(numCombate, extras, arrayBidim[0][1], cuadroSuperior.get(0).getIdGanador(), // 1 vs ganador001
                        "006-R", "009-A", NO, cuadroSuperior);

                // 2
                numCombate = "005";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdGanador(), cuadroSuperior.get(2).getIdGanador(), // ganador002 vs ganador003
                        "006-A", "007-A", NO, cuadroSuperior);

                // Final
                numCombate = "006";
                addCombate(numCombate, extras, cuadroSuperior.get(3).getIdGanador(), cuadroSuperior.get(4).getIdGanador(), // ganador004 vs ganador005
                        "", "", SI, cuadroSuperior);

                // Cuadro Inferior
                // 1
                numCombate = "007";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdPerdedor(), cuadroSuperior.get(4).getIdPerdedor(), // perdedor001 vs perdedor005
                        "", "", TERCEROS, cuadroInferior);

                // 2
                numCombate = "008";
                addCombate(numCombate, extras, cuadroSuperior.get(2).getIdPerdedor(), cuadroSuperior.get(1).getIdPerdedor(), // perdedor003 vs perdedor002
                        "009-R", "", NO, cuadroInferior);

                // 3
                numCombate = "009";
                addCombate(numCombate, extras, cuadroInferior.get(1).getIdGanador(), cuadroSuperior.get(3).getIdPerdedor(), // ganador008 vs perdedor004
                        "", "", TERCEROS, cuadroInferior);

                break;
            }
            case 7: { // 4 cuartos, 2 semis y final 8 Competidores
                // Cuartos
                // 1
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[0][1], arrayBidim[7][1], // 1 vs 8
                        "005-R", "008-A", NO, cuadroSuperior);

                // 2
                numCombate = "002";
                addCombate(numCombate, extras, arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "005-A", "008-R", NO, cuadroSuperior);

                // 3
                numCombate = "003";
                addCombate(numCombate, extras, arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "006-R", "009-A", NO, cuadroSuperior);

                // 4
                numCombate = "004";
                addCombate(numCombate, extras, arrayBidim[6][1], arrayBidim[1][1], // 7 vs 2
                        "006-A", "009-R", NO, cuadroSuperior);

                // Semis
                // 1
                numCombate = "005";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdGanador(), cuadroSuperior.get(1).getIdGanador(), // ganador001 vs ganador002
                        "007-R", "011-R", NO, cuadroSuperior);

                // 2
                numCombate = "006";
                addCombate(numCombate, extras, cuadroSuperior.get(2).getIdGanador(), cuadroSuperior.get(3).getIdGanador(), // ganador003 vs ganador004
                        "007-A", "010-A", NO, cuadroSuperior);

                // Final
                numCombate = "007";
                addCombate(numCombate, extras, cuadroSuperior.get(4).getIdGanador(), cuadroSuperior.get(5).getIdGanador(), // ganador005 vs ganador006
                        "", "", SI, cuadroSuperior);

                // Cuadro Inferior
                // 1
                numCombate = "008";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdPerdedor(), cuadroSuperior.get(0).getIdPerdedor(), // perdedor002 vs perdedor001
                        "010-R", "", NO, cuadroInferior);

                // 2
                numCombate = "009";
                addCombate(numCombate, extras, cuadroSuperior.get(3).getIdPerdedor(), cuadroSuperior.get(2).getIdPerdedor(), // perdedor004 vs perdedor003
                        "011-A", "", NO, cuadroInferior);

                // 3
                numCombate = "010";
                addCombate(numCombate, extras, cuadroInferior.get(0).getIdGanador(), cuadroSuperior.get(5).getIdPerdedor(), // ganador008 vs perdedor006
                        "","", TERCEROS, cuadroInferior);

                // 4
                numCombate = "011";
                addCombate(numCombate, extras, cuadroSuperior.get(4).getIdPerdedor(), cuadroInferior.get(1).getIdGanador(), // perdedor005 vs ganador009
                        "", "", TERCEROS, cuadroInferior);

                break;
            }
            case 8: { // 1 octavo, 4 cuartos, 2 semis y final 9 Competidores
                // Octavos
                // 1
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[8][1], arrayBidim[7][1], // 9 vs 8
                        "002-A", "009-R", NO, cuadroSuperior);

                // Cuartos
                // 1
                numCombate = "002";
                addCombate(numCombate, extras, arrayBidim[0][1], cuadroSuperior.get(0).getIdGanador(), // 1 vs ganador001
                        "006-R", "011-A", NO, cuadroSuperior);

                // 2
                numCombate = "003";
                addCombate(numCombate, extras, arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "006-A", "009-A", NO, cuadroSuperior);

                // 3
                numCombate = "004";
                addCombate(numCombate, extras, arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "007-R", "010-A", NO, cuadroSuperior);

                // 4
                numCombate = "005";
                addCombate(numCombate, extras, arrayBidim[6][1], arrayBidim[1][1], // 7 vs 2
                        "007-A", "010-R", NO, cuadroSuperior);

                // Semis
                // 1
                numCombate = "006";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdGanador(), cuadroSuperior.get(2).getIdGanador(), // ganador002 vs ganador003
                        "008-R", "012-A", NO, cuadroSuperior);

                // 2
                numCombate = "007";
                addCombate(numCombate, extras, cuadroSuperior.get(3).getIdGanador(), cuadroSuperior.get(4).getIdGanador(), // ganador004 vs ganador005
                        "008-A", "013-A", NO, cuadroSuperior);

                // Final
                numCombate = "008";
                addCombate(numCombate, extras, cuadroSuperior.get(5).getIdGanador(), cuadroSuperior.get(6).getIdGanador(), // ganador006 vs ganador007
                        "", "", SI, cuadroSuperior);

                // Cuadro Inferior
                // 1
                numCombate = "009";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdPerdedor(), cuadroSuperior.get(2).getIdPerdedor(), // perdedor001 vs perdedor003
                        "011-R","", NO, cuadroInferior);

                // 2
                numCombate = "010";
                addCombate(numCombate, extras, cuadroSuperior.get(4).getIdPerdedor(), cuadroSuperior.get(3).getIdPerdedor(), // perdedor005 vs perdedor004
                        "012-R","", NO, cuadroInferior);

                // 3
                numCombate = "011";
                addCombate(numCombate, extras, cuadroInferior.get(0).getIdGanador(), cuadroSuperior.get(1).getIdPerdedor(), // ganador009 vs perdedor002
                        "013-R", "", NO, cuadroInferior);

                // 4
                numCombate = "012";
                addCombate(numCombate, extras, cuadroInferior.get(1).getIdGanador(), cuadroSuperior.get(5).getIdPerdedor(), // ganador010 vs perdedor006
                        "", "", TERCEROS, cuadroInferior);

                // 5
                numCombate = "013";
                addCombate(numCombate, extras, cuadroInferior.get(2).getIdPerdedor(), cuadroSuperior.get(6).getIdPerdedor(), // ganador011 vs perdedor006
                        "", "", TERCEROS, cuadroInferior);

                break;
            }
            case 9: { // 2 octavos, 4 cuartos, 2 semis y 1 final 10 Competidores
                // Octavos
                // 1
                numCombate = "001";
                addCombate(numCombate, extras, arrayBidim[8][1], arrayBidim[7][1], // 9 vs 8
                        "003-A", "010-R", NO, cuadroSuperior);

                // 2
                numCombate = "002";
                addCombate(numCombate, extras, arrayBidim[6][1], arrayBidim[9][1], // 7 vs 10
                        "006-R", "011-R", NO, cuadroSuperior);

                // Cuartos
                // 1
                numCombate = "003";
                addCombate(numCombate, extras, arrayBidim[0][1], cuadroSuperior.get(0).getIdGanador(), // 1 vs ganador001
                        "007-R", "012-A", NO, cuadroSuperior);

                // 2
                numCombate = "004";
                addCombate(numCombate, extras, arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "007-A", "010-A", NO, cuadroSuperior);

                // 3
                numCombate = "005";
                addCombate(numCombate, extras, arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "008-R", "011-A", NO, cuadroSuperior);

                // 4
                numCombate = "006";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdGanador(), arrayBidim[5][1], // ganador001 vs 6
                        "008-A", "013-R", NO, cuadroSuperior);

                // Semis
                // 1
                numCombate = "007";
                addCombate(numCombate, extras, cuadroSuperior.get(2).getIdGanador(), cuadroSuperior.get(3).getIdGanador(), // ganador003 vs ganador004
                        "009-R", "015-A", NO, cuadroSuperior);

                // 2
                numCombate = "008";
                addCombate(numCombate, extras,  cuadroSuperior.get(4).getIdGanador(), cuadroSuperior.get(5).getIdGanador(), // ganador005 vs ganador006
                        "009-A", "014-A", NO, cuadroSuperior);

                // Final
                numCombate = "009";
                addCombate(numCombate, extras, cuadroSuperior.get(6).getIdGanador(), cuadroSuperior.get(7).getIdGanador(), // ganador007 vs ganador008
                        "", "", SI, cuadroSuperior);

                // Cuadro inferior
                // 1
                numCombate = "010";
                addCombate(numCombate, extras, cuadroSuperior.get(0).getIdPerdedor(), cuadroSuperior.get(3).getIdPerdedor(), // perdedor001 vs perdedor004
                        "012-R", "", NO, cuadroInferior);

                // 2
                numCombate = "011";
                addCombate(numCombate, extras, cuadroSuperior.get(1).getIdPerdedor(), cuadroSuperior.get(4).getIdPerdedor(), // perdedor002 vs perdedor005
                        "013-A", "", NO, cuadroInferior);

                // 3
                numCombate = "012";
                addCombate(numCombate, extras, cuadroInferior.get(0).getIdGanador(), cuadroSuperior.get(2).getIdPerdedor(), // ganador010 vs perdedor003
                        "014-R", "", NO, cuadroInferior);

                // 4
                numCombate = "013";
                addCombate(numCombate, extras, cuadroSuperior.get(5).getIdPerdedor(), cuadroInferior.get(1).getIdGanador(), // perdedor006 vs ganador011
                        "015-R", "", NO, cuadroInferior);

                // 5
                numCombate = "014";
                addCombate(numCombate, extras, cuadroInferior.get(2).getIdGanador(), cuadroSuperior.get(7).getIdPerdedor(), // ganador012 vs perdedor008
                        "", "", TERCEROS, cuadroInferior);

                // 6
                numCombate = "015";
                addCombate(numCombate, extras, cuadroInferior.get(3).getIdGanador(), cuadroSuperior.get(6).getIdPerdedor(), // gaandor013 vs perdedor007
                        "", "", TERCEROS, cuadroInferior);

                break;
            }
            // El resto de casos, es decir, cuando haya más de 10 competidores en una categoría.
        }

        return arrayBidim; // Devolvemos el array Bidimensional que alamcena la correspondencia entre competidores y su posición en los emparejamientos tras el sorteo.
    }


    /*public void addCombate(String idCamp, String idMod, final String idCat, String idRojo, String idAzul, final int numCombate){
        // La raíz para insertar datos será mCombatesDB
        // Crear un objeto de tipo Combates cuyo idCombate será el generado por la BD al insertarlo bajo el idCat de la Categoría a la que pertenece el combate.
        final String idCombate = mCombatesDB.child(idCat).push().getKey();
        final Combates combate = new Combates(idCombate, numCombate,
                "", "", "", "",
                idRojo, idAzul, null,
                idCamp, idMod, idCat, Combates.EstadoCombate.Pendiente);

        // Añadir el combate a la rama de Combates. La lista de Combates depende de la categoría, es decir, raíz de la BD de Combates para esta categoría es mCatDB.
        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Comprobar duplicados
                Query consulta = mCombatesDB
                        .orderByChild("idCombate");

                consulta.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot combateSnapShot: dataSnapshot.getChildren()){
                            Combates comb = dataSnapshot.getValue(Combates.class);
                            if(comb.getNumCombate() == numCombate){ // Existe algún combate con ese Número
                                Toast.makeText(MostrarEmparejamientosActivity.this, "Ya existe un Combate con ese Número en esta Categoría en la BD. Compruebe los datos...", Toast.LENGTH_SHORT).show();
                                return;
                            } else { // Si no existe ningún combate con ese Número se añade
                                mCombatesDB.child(idCat).child(idCombate).setValue(combate);
                                // Añadir los Asaltos de este Combate

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
    //endregion

    //region Simular Emparejamientos y actualizar UI
    // En este método vamos a simular un cuadro de emparejamientos siguiendo los siguientes pasos:
    // 1º - Sorteo de posiciones
    // 2º - Cargar datos de competidores
    // 3º - Simular Resultados
    // 4º - Actualizar UI y mostrar podio.
    public void simularCuadro(){

        final List<Competidores> listaComp = new ArrayList<>();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String idMod = extras.getString("idMod");
        String idCat = extras.getString("idCat");

        mCatDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Categorias").child(idMod).child(idCat);

        mCatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Query consulta = mCompDB
                        .orderByChild("dni"); // Lista de competidores ordenada por DNI.
                Categorias cat = dataSnapshot.getValue(Categorias.class);
                final String peso = cat.getPeso();
                final String edad = cat.getEdad();
                final String sexo = cat.getSexo();

                consulta.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaComp.clear();
                        for(DataSnapshot competidorSnapshot: dataSnapshot.getChildren()){
                            Competidores comp = competidorSnapshot.getValue(Competidores.class);
                            if(comp.getSexo().equals(sexo) && (comp.getCatEdad().equals(edad)) && (comp.getCatPeso().equals(peso))){
                                listaComp.add(comp);
                            }
                        }
                        final Integer[] array = sorteoCompetidores(listaComp.size());
                        asignarCompetidores(array);
                        switch (listaComp.size()){ // Dependiendo del número de competidores que haya en la categoría se deberá cargar un layout
                            case 2:{ // Dos competidores --> se habrá cargado el layout para final directa.

                                Button ganaRojo = (Button) findViewById(R.id.dos_comp_gana_Rojo_btn);
                                Button ganaAzul = (Button) findViewById(R.id.dos_comp_gana_Azul_btn);

                                final CircleImageView fotoCampeon = (CircleImageView) findViewById(R.id.dos_comp_foto_ganador);

                                ganaRojo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pintarLinea(R.id.dos_comp_linea_horizontal_Rojo, getResources().getColor(R.color.colorRojo));
                                        Picasso.get().load(listaComp.get(array[0]).getFoto()).into(fotoCampeon);
                                    }
                                });

                                ganaAzul.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pintarLinea(R.id.dos_comp_linea_horizontal_Azul, getResources().getColor(R.color.colorAccent2));
                                        Picasso.get().load(listaComp.get(array[1]).getFoto()).into(fotoCampeon);
                                    }
                                });

                                break;
                            }
                            case 3:{ // Tres competidores --> 1 pasa a la final y una semifinal

                                // Se carga el layout para 3 competidores

                                Button gana1Btn = (Button) findViewById(R.id.tres_comp_gana_1_btn);
                                Button gana2Btn = (Button) findViewById(R.id.tres_comp_gana_2_btn);
                                Button gana3Btn = (Button) findViewById(R.id.tres_comp_gana_3_btn);

                                final CircleImageView fotoCampeon = (CircleImageView) findViewById(R.id.tres_comp_foto_ganador);

                                // Boton Gana 1
                                gana1Btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pintarLinea(R.id.tres_comp_linea_horizontal_1, getResources().getColor(R.color.colorRojo));
                                        pintarLinea(R.id.tres_comp_linea_vertical_1, getResources().getColor(R.color.colorRojo));
                                        pintarLinea(R.id.tres_comp_linea_horizontal_4, getResources().getColor(R.color.colorRojo));
                                    }
                                });
                                // Boton Gana 2
                                gana2Btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pintarLinea(R.id.tres_comp_linea_horizontal_2, getResources().getColor(R.color.colorAccent2));
                                        pintarLinea(R.id.tres_comp_linea_vertical_2, getResources().getColor(R.color.colorAccent2));
                                    }
                                });
                                // Boton Gana 3
                                gana3Btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pintarLinea(R.id.tres_comp_linea_horizontal_3, getResources().getColor(R.color.colorAccent2));
                                        pintarLinea(R.id.tres_comp_linea_vertical_3, getResources().getColor(R.color.colorAccent2));
                                        pintarLinea(R.id.tres_comp_linea_horizontal_4, getResources().getColor(R.color.colorAccent2));
                                    }
                                });

                                break;
                            }
                            case 4:{
                                break;
                            }
                            case 5:{
                                break;
                            }
                            case 6:{
                                break;
                            }
                            case 7:{
                                break;
                            }
                            case 8:{
                                break;
                            }
                            case 9:{
                                break;
                            }
                            case 10:{
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    // Este método se encarga de pintar del color correcto la línea correspondiente al competidor que gana un combate
    public void pintarLinea(int idLinea, int color){
        View linea = (View) findViewById(idLinea);

        linea.setBackgroundColor(color);
    }
    //endregion

    // region Añadir Combate BD
    public void addCombate(final String numCombate, Bundle extras, final String idRojo, final String idAzul,
                           final String sigCombGanador, final String sigCombPerdedor, final Emparejamientos.EsFinal esFinal, final List<Emparejamientos> cuadro){
        // Evitar duplicados comprobando el número del combate. No puede haber dos combates con el mismo número en una categoría
        // Los emparejamientos dependen de una categoría. El idCat lo obtenemos del Bundle extras.
        final String idCat = extras.getString("idCat");
        final String idMod = extras.getString("idMod");
        final String idCamp = extras.getString("idCamp");

        // Toast.makeText(this, "addCombate --> URL emparejamientos " + mEmparejamientosDB.toString(), Toast.LENGTH_SHORT).show();

        Query consulta = mEmparejamientosDB.child(idCat);

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // Vamos a comprobar si ya existe el emparejamiento que queremos insertar.
                //  Si no existen emparejamientos en esta categoría añadimos el primero.
                /*if(dataSnapshot.getChildrenCount() == 0){
                    // Generar el ID único.
                    String idEmp = mEmparejamientosDB.child(idCat).push().getKey();
                    // Invocar al método que trabaja con la BD.
                    crearEmparejamiento(cuadro, idEmp, numCombate, idRojo, idAzul, esFinal,
                            sigCombGanador, sigCombPerdedor, idCamp, idMod, idCat);

                } else {*/
                    boolean crear = true;
                    // Si ya existen emparejamientos debemos comprobar si ya existe el emparejamiento que queremos insertar.
                    for(DataSnapshot empSpnapshot: dataSnapshot.getChildren()){
                        Emparejamientos emp = empSpnapshot.getValue(Emparejamientos.class);
                        if(emp.getNumeroCombate().equals(numCombate)){
                            // Ya existe un combate con ese número en esta categoría.
                            Toast.makeText(MostrarEmparejamientosActivity.this,
                                    "Ya existe un combate con el número " + numCombate + " en la categoría cuyo ID es " + idCat,
                                    Toast.LENGTH_SHORT).show();
                            crear = false;
                        }
                    }
                    // Una vez que hemos recorrido todos los resultados de la consulta, es decir, todos los emparejamientos de esta categoría sin haber encontrado
                    // el emparejamiento que queremos insertar, podemos estar seguros de que podemos insertarlo sin provocar duplicados de datos.
                    if(crear){ // Si no hemos encontrado ningún emparejamiento con el mismo Número de Combate...
                        // Generar el ID único.
                        String idEmp = mEmparejamientosDB.child(idCat).push().getKey();
                        // Invocar al método que trabaja con la BD.
                        crearEmparejamiento(cuadro, idEmp, numCombate, idRojo, idAzul, esFinal,
                                sigCombGanador, sigCombPerdedor, idCamp, idMod, idCat);
                    }

                //}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


/*        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // Vamos a comprobar si ya existe el emparejamiento que queremos insertar.
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    // Toast.makeText(MostrarEmparejamientosActivity.this, "NO existen emparejamientos para esta categoría", Toast.LENGTH_SHORT).show();
                    // Si no existe ningún emparejamiento no tenemos que comprobar los duplicados. Podemos insertar el primer emparejamiento en la BD.
                    // Se guarda el combate en la BD.
                    // Generar el ID único.
                    String idEmp = mEmparejamientosDB.child(idCat).push().getKey();
                    // Invocar al método que trabaja con la BD.
                    crearEmparejamiento(cuadro, idEmp, numCombate, idRojo, idAzul, esFinal,
                            sigCombGanador, sigCombPerdedor, idCamp, idMod, idCat);

                    return; // Para que no se comprueben los duplicados al introducir el primer emparejamiento.
                } else {
                    for(DataSnapshot empSnapshot: dataSnapshot.getChildren()){
                        Emparejamientos emparejamiento = empSnapshot.getValue(Emparejamientos.class);
                        if(emparejamiento.getNumeroCombate().equals(numCombate)){
                            // Ya existe un combate con ese número en esta categoría.
                            Toast.makeText(MostrarEmparejamientosActivity.this,
                                    "Ya existe un combate con el número " + numCombate + " en la categoría cuyo ID es " + idCat,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        } else { // Se guarda el combate en la BD.
                            // Generar el ID único.
                            String idEmp = mEmparejamientosDB.child(idCat).push().getKey();
                            // Invocar al método que trabaja con la BD.
                            crearEmparejamiento(cuadro, idEmp, numCombate, idRojo, idAzul, esFinal,
                                    sigCombGanador, sigCombPerdedor, idCamp, idMod, idCat);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });*/
    }

    public Emparejamientos crearEmparejamiento(final List<Emparejamientos> cuadro, String idEmp, final String numCombate,
                                               String idRojo, String idAzul, Emparejamientos.EsFinal esFinal,
                                               String sigCombGanador, String sigCombPerdedor, final String idCamp, final String idMod, final String idCat){


        // Añadir el Combate si no existe

        // Añadir el Emparejamiento a la BD.

        final Emparejamientos emp = new Emparejamientos(idEmp, numCombate, idRojo, idAzul, sigCombGanador, sigCombPerdedor, esFinal, "", "");
        cuadro.add(emp);

        // Añadir una comprobación de confirmación de escritura de los datos.
        mEmparejamientosDB.child(idCat).child(idEmp).setValue(emp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MostrarEmparejamientosActivity.this,
                            "Se ha añadido el emparejamiento para el combate número " + numCombate,
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(MostrarEmparejamientosActivity.this,
                            "El cuadro actual tiene un tamaño de " + cuadro.size(),
                            Toast.LENGTH_SHORT).show();

                    // Añadir el combate en cuestión a la BD

                    String idCombate = mCombatesDB.child(idCat).push().getKey(); // La lista de combates depende de una Categoría.
                    List<Asaltos> listaAsaltos = new ArrayList<>();
                    //Integer num = Integer.parseInt(emp.getNumeroCombate());
                    // Crear objeto de tipo Combates
                    Combates comb = new Combates(idCombate, numCombate,
                            "", "", "", "",
                            emp.getIdRojo(), emp.getIdAzul(),
                            listaAsaltos,
                            idCamp,"", idMod, idCat,
                            Combates.EstadoCombate.Pendiente, 0);
                    // Insertar dicho objeto en la BD.
                    mCombatesDB.child(idCat).child(idCombate).setValue(comb);
                    Toast.makeText(MostrarEmparejamientosActivity.this,
                            "Se ha añadido el combate correspondiente al emparejamiento " + numCombate,
                            Toast.LENGTH_SHORT).show();

                    // Añadir sus Asaltos y actualizar listaAsaltos del combate que acabamos de crear.
                    // Por defecto añadimos tres asaltos para cada combate. Si son necesarios se usan y si no se marcan como cancelados.
                    // La lista de Asaltos depende del combate al que pertenecen.
                    for(int i = 0; i < 3; i++){
                        // Crear dos listas vacías para Puntuaciones e Incidencias
                        List<Puntuaciones> listaPunt = new ArrayList<>();
                        List<Incidencias> listaInc = new ArrayList<>();
                        // Generar el id del Asalto con el push de la BD.
                        String idAsalto = mAsaltosDB.child(idCombate).push().getKey();
                        // Crear objeto tipo Asaltos
                        Asaltos asalto = new Asaltos(idAsalto, i+1,
                                "", "", "",
                                0, 0, "",
                                listaPunt, listaInc,
                                Asaltos.EstadoAsalto.Pendiente);
                        // Añadir los asaltos a la lista de Asaltos que hemos creado en este bloque de código, es decir, en la variable listaAsaltos.
                        listaAsaltos.add(asalto);
                        // Insertar los Asaltos en la BD.
                        mAsaltosDB.child(idCombate).child(idAsalto).setValue(asalto);
                        Toast.makeText(MostrarEmparejamientosActivity.this,
                                "Añadido el asalto nº " + String.valueOf(i+1) + " al combate cuyo ID es " + idCombate,
                                Toast.LENGTH_SHORT).show();
                    }

                    // Actualizar la lista de Asaltos del Combate que acabamos de crear.
                    comb.setListaAsaltos(listaAsaltos);
                    mCombatesDB.child(idCat).child(idCombate).setValue(comb);
                    Toast.makeText(MostrarEmparejamientosActivity.this,
                            "Actualizada la lista de Asaltos del Combate cuyo ID es " + idCombate,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Se ha producido un error al añadir el emparejamiento a la BD.
                    Toast.makeText(MostrarEmparejamientosActivity.this,
                            "ERROR: Se ha producido un error al añadir el emparejamiento a la BD. (numCombate --> " + numCombate + " )",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  emp;

    }

    // endregion
}
