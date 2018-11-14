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
                asignarCompetidores(array);

                //simularCuadro();
                break;
            }
            case R.layout.emparejamientos_3_competidores_layout:{
                mNombreCat = (TextView) findViewById((R.id.tres_comp_nombreCat));
                mPruebaArray = (TextView) findViewById(R.id.tres_comp_pruebaArray);
                simularCuadro();
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
        Bundle extras = intent.getExtras();
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

                                    addCombatesEmparejamientos(array, listaComp);

                                    // Click sobre el botón Empezar Combate
                                    Button comenzarBtn = (Button) findViewById(R.id.dos_comp_emmpezar_combate_btn);
                                    comenzarBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Recopilar datos a pasar al intent. (Bundle)
                                            // Crear intent para arbitraje (Mesa)
                                            Intent arbitrarIntent = new Intent(MostrarEmparejamientosActivity.this, MesaArbitrajeActivity.class);
                                            // Pasarle datos del Bundle al intent.
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
                                    TextView paisDos = (TextView) findViewById(R.id.tres_comp_pais_3);

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

                                    addCombatesEmparejamientos(array, listaComp);

                                    // Comportamiento Botones Empezar Combate
                                    Button empezarUnoBtn = (Button) findViewById(R.id.tres_comp_comenzar_1_btn);
                                    empezarUnoBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent empezarUnoIntent = new Intent(MostrarEmparejamientosActivity.this, MesaArbitrajeActivity.class);
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
    public void addCombatesEmparejamientos(Integer[] array, List<Competidores> listaComp){
        // Se recorre el array y la lista y se generan un array bidimensional con el índice del array y el id del Competidor que le corresponde en la lista.
        // Ejemplo --> arrayBidimensional[0] = array[0], listaComp.get(0)
        // Para poder mantener el String del idComp el array será de String y se convertirá en String el índice del array de Integer.
        int tamano = listaComp.size();
        String[][] arrayBidim = new String[tamano][2];

        for(int i = 0; i < tamano; i++){
            arrayBidim[i][0] = array[i].toString();
            arrayBidim[i][1] = listaComp.get(i).getDni();
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
            case 3:{ // Triangular
                numEmparejamientosSup = 3;
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
        // CUADRO SUPERIOR
        List<Emparejamientos> cuadroSuperior = new ArrayList<>();
        // CUADRO INFERIOR
        List<Emparejamientos> cuadroInferior = new ArrayList<>();

        switch (numEmparejamientosSup){
            case 1: { // Final Directa, 2 Competidores

                Intent intent = getIntent();
                Bundle extras = intent.getExtras();

                String idCat = extras.getString("idCat");
                String idMod = extras.getString("idMod");
                String idCamp = extras.getString("idCamp");

                // Añadir el Emparejamiento a la BD.
                String idEmp = mEmparejamientosDB.child(idCat).push().getKey();
                Emparejamientos emp001 = new Emparejamientos(idEmp, "001", arrayBidim[0][1],arrayBidim[1][1],
                        null, null, Emparejamientos.EsFinal.SI, null, null);
                cuadroSuperior.add(emp001);
                mEmparejamientosDB.child(idCat).child(idEmp).setValue(emp001);

                // Añadir el combate en cuestión a la BD

                String idCombate = mCombatesDB.child(idCat).push().getKey(); // La lista de combates depende de una Categoría.
                List<Asaltos> listaAsaltos = new ArrayList<>();
                Integer num = Integer.parseInt(emp001.getNumeroCombate());
                // Crear objeto de tipo Combates
                Combates comb = new Combates(idCombate, num,
                        "", "", "", "",
                        emp001.getIdRojo(), emp001.getIdAzul(),
                        listaAsaltos,
                        idCamp, idMod, idCat,
                        Combates.EstadoCombate.Pendiente);
                // Insertar dicho objeto en la BD.
                mCombatesDB.child(idCat).child(idCombate).setValue(comb);
                Toast.makeText(this, "Se ha añadido el combate correspondiente al emparejamiento " + num, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(this, "Añadido el asalto nº " + String.valueOf(i+1) + " al combate cuyo ID es " + idCombate, Toast.LENGTH_SHORT).show();
                }

                // Actualizar la lista de Asaltos del Combate que acabamos de crear.
                comb.setListaAsaltos(listaAsaltos);
                mCombatesDB.child(idCat).child(idCombate).setValue(comb);
                Toast.makeText(this, "Actualizada la lista de Asaltos del Combate cuyo ID es " + idCombate, Toast.LENGTH_SHORT).show();

                break;
            }
            /*case 2: { // Uno se clasifica directo a la final y los otros dos hacen semifinal, 3 Competidores
                // Semi
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[1][1], arrayBidim[2][1],
                        "002-A", null, NO, null, null);
                cuadroSuperior.add(emp001);
                // Final
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[0][1], emp001.getIdGanador(), // El id del ganador del combate emp001
                        null, null, Emparejamientos.EsFinal.SI, null, null);
                break;
            }
            case 3: { // Semis y final, 4 Competidores
                // Semi 1
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[0][1], arrayBidim[3][1], // 1 vs 4
                         "003-R", "004-A", NO, null, null);
                cuadroSuperior.add(emp001);
                // Semi 2
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[2][1], arrayBidim[1][2], // 3 vs 2
                        "003-A", "004-R", NO, null, null);
                cuadroSuperior.add(emp002);
                // Final
                Emparejamientos emp003 = new Emparejamientos("003", emp001.getIdGanador(), emp002.getIdGanador(), // ganador de emp001 vs ganador de emp002
                         null, null, SI, null, null);
                cuadroSuperior.add(emp003);

                // Cuadro Inferior
                Emparejamientos emp004 = new Emparejamientos("004", emp001.getIdPerdedor(), emp002.getIdPerdedor(),
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp004);
                break;
            }
            case 4: { // 1 combate de cuartos, dos semis y final, 5 Competidores
                // Cuartos
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[0][1], arrayBidim[4][1], // 1 vs 5
                        "002-R", "006-R", NO, null, null);
                cuadroSuperior.add(emp001);
                // Semi 1
                Emparejamientos emp002 = new Emparejamientos("002", emp001.getIdGanador(), arrayBidim[2][1], // ganador de 001 vs 3
                        "004-R", "005-A", NO, null, null);
                cuadroSuperior.add(emp002);
                // Semi 2
                Emparejamientos emp003 = new Emparejamientos("003", arrayBidim[3][1], arrayBidim[1][1], // 4 vs 2
                        "004-A", "005-R", NO, null, null);
                cuadroSuperior.add(emp003);
                // Final
                Emparejamientos emp004 = new Emparejamientos("004", emp002.getIdGanador(), emp003.getIdGanador(), // ganador de emp002 vs ganador de emp003
                        null, null, SI, null, null);
                cuadroSuperior.add(emp004);
                // Cuadro Inferior
                // 1
                Emparejamientos emp005 = new Emparejamientos("005", emp003.getIdPerdedor(), emp002.getIdPerdedor(), // perdedor de emp003 vs perdedor de emp002
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp005);
                // 2 -- El perdedor del emp001 es tercero --> Hace cuartos y semis en el cuadro superior
                Emparejamientos emp006 = new Emparejamientos("006", emp001.getIdPerdedor(), null, // No hay rival para este combate.
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp006);
                break;
            }
            case 5: { // 2 cuartos, 2 semis y final , 6 Competidores
                // Cuartos 1
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[0][1], arrayBidim[5][1], // 1 vs 6
                        "003-R", "006-A", NO, null, null);
                cuadroSuperior.add(emp001);
                // Cuartos 2
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[1][1], arrayBidim[4][1], // 5 vs 2
                        "004-A", "007-R", NO, null, null);
                cuadroSuperior.add(emp002);
                // Semis 1
                Emparejamientos emp003 = new Emparejamientos("003", emp001.getIdGanador(), arrayBidim[2][1], // ganador001 vs 3
                        "005-R", "007-A", NO, null, null);
                cuadroSuperior.add(emp003);
                // Semis 2
                Emparejamientos emp004 = new Emparejamientos("004", arrayBidim[3][1], emp002.getIdGanador(), // 4 vs ganador002
                        "005-A", "006-R", NO, null, null);
                cuadroSuperior.add(emp004);
                // Final
                Emparejamientos emp005 = new Emparejamientos("005", emp003.getIdGanador(), emp004.getIdGanador(), // ganador003 vs ganador004
                        null, null, SI, null, null);
                cuadroSuperior.add(emp005);
                // Cuadro Inferior
                // 1
                Emparejamientos emp006 = new Emparejamientos("006", emp004.getIdPerdedor(), emp001.getIdPerdedor(), // perdedor004 vs perdedor001
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp006);
                // 2
                Emparejamientos emp007 = new Emparejamientos("007", emp002.getIdPerdedor(), emp003.getIdPerdedor(), // perdedor002 vs perdedor003
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp007);
                break;
            }
            case 6: { // 3 cuartos, 2 semis y 1 final 7 Competidores
                // Cuartos
                // 1
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "004-A", "007-R", NO, null, null);
                cuadroSuperior.add(emp001);
                // 2
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "005-R", "008-A", NO, null, null);
                cuadroSuperior.add(emp002);
                // 3
                Emparejamientos emp003 = new Emparejamientos("003", arrayBidim[6][1], arrayBidim[1][1], // 7 vs 2
                        "005-A", "008-R", NO, null, null);
                cuadroSuperior.add(emp003);
                // Semis
                // 1
                Emparejamientos emp004 = new Emparejamientos("004", arrayBidim[0][1], emp001.getIdGanador(), // 1 vs ganador001
                        "006-R", "009-A", NO, null, null);
                cuadroSuperior.add(emp004);
                // 2
                Emparejamientos emp005 = new Emparejamientos("005", emp002.getIdGanador(), emp003.getIdGanador(), // ganador002 vs ganador003
                        "006-A", "007-A", NO, null, null);
                cuadroSuperior.add(emp005);
                // Final
                Emparejamientos emp006 = new Emparejamientos("006", emp004.getIdGanador(), emp005.getIdGanador(), // ganador004 vs ganador005
                        null, null, SI, null, null);
                cuadroSuperior.add(emp006);
                // Cuadro Inferior
                // 1
                Emparejamientos emp007 = new Emparejamientos("007", emp001.getIdPerdedor(), emp005.getIdPerdedor(), // perdedor001 vs perdedor005
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp007);
                // 2
                Emparejamientos emp008 = new Emparejamientos("008", emp003.getIdPerdedor(), emp002.getIdPerdedor(), // perdedor003 vs perdedor002
                        "009-R", null, NO, null, null);
                cuadroSuperior.add(emp008);
                // 3
                Emparejamientos emp009 = new Emparejamientos("009", emp008.getIdGanador(), emp004.getIdPerdedor(), // ganador008 vs perdedor004
                        null, null, TERCEROS, null, null);
                cuadroSuperior.add(emp009);
                break;
            }
            case 7: { // 4 cuartos, 2 semis y final 8 Competidores
                // Cuartos
                // 1
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[0][1], arrayBidim[7][1], // 1 vs 8
                            "005-R", "008-A", NO, null, null);
                cuadroSuperior.add(emp001);
                // 2
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                            "005-A", "008-R", NO, null, null);
                cuadroSuperior.add(emp002);
                // 3
                Emparejamientos emp003 = new Emparejamientos("003", arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                            "006-R", "009-A", NO, null, null);
                cuadroSuperior.add(emp003);
                // 4
                Emparejamientos emp004 = new Emparejamientos("004", arrayBidim[6][1], arrayBidim[1][1], // 7 vs 2
                            "006-A", "009-R", NO, null, null);
                cuadroSuperior.add(emp004);
                // Semis
                // 1
                Emparejamientos emp005 = new Emparejamientos("005", emp001.getIdGanador(), emp002.getIdGanador(), // ganador001 vs ganador002
                        "007-R", "011-R", NO, null, null);
                cuadroSuperior.add(emp005);
                // 2
                Emparejamientos emp006 = new Emparejamientos("006", emp003.getIdGanador(), emp004.getIdGanador(), // ganador003 vs ganador004
                        "007-A", "010-A", NO, null, null);
                cuadroSuperior.add(emp006);
                // Final
                Emparejamientos emp007 = new Emparejamientos("007", emp005.getIdGanador(), emp006.getIdGanador(), // ganador005 vs ganador006
                        null, null, SI, null, null);
                cuadroSuperior.add(emp007);
                // Cuadro Inferior
                // 1
                Emparejamientos emp008 = new Emparejamientos("008", emp002.getIdPerdedor(), emp001.getIdPerdedor(), // perdedor002 vs perdedor001
                         "010-R", null, NO, null, null);
                cuadroInferior.add(emp008);
                // 2
                Emparejamientos emp009 = new Emparejamientos("009", emp004.getIdPerdedor(), emp003.getIdPerdedor(), // perdedor004 vs perdedor003
                        "011-A", null, NO, null, null);
                cuadroInferior.add(emp009);
                // 3
                Emparejamientos emp010 = new Emparejamientos("010", emp008.getIdGanador(), emp006.getIdPerdedor(), // ganador008 vs perdedor006
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp010);
                // 4
                Emparejamientos emp011 = new Emparejamientos("011", emp005.getIdPerdedor(), emp009.getIdGanador(), // perdedor005 vs ganador009
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp011);

            }
            case 8: { // 1 octavo, 4 cuartos, 2 semis y final 9 Competidores
                // Octavos
                // 1
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[8][1], arrayBidim[7][1], // 9 vs 8
                        "002-A", "009-R", NO, null, null);
                cuadroSuperior.add(emp001);
                // Cuartos
                // 1
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[0][1], emp001.getIdGanador(), // 1 vs ganador001
                        "006-R", "011-A", NO, null, null);
                cuadroSuperior.add(emp002);
                // 2
                Emparejamientos emp003 = new Emparejamientos("003", arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "006-A", "009-A", NO, null, null);
                cuadroSuperior.add(emp003);
                // 3
                Emparejamientos emp004 = new Emparejamientos("004", arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "007-R", "012-A", NO, null, null);
                cuadroSuperior.add(emp004);
                // 4
                Emparejamientos emp005 = new Emparejamientos("005", arrayBidim[6][1], arrayBidim[1][1], // 7 vs 2
                        "007-A", "010-A", NO, null, null);
                cuadroSuperior.add(emp005);
                // Semis
                // 1
                Emparejamientos emp006 = new Emparejamientos("006", emp002.getIdGanador(), emp003.getIdGanador(), // ganador002 vs ganador003
                        "008-R", "013-A", NO, null, null);
                cuadroSuperior.add(emp006);
                // 2
                Emparejamientos emp007 = new Emparejamientos("007", emp004.getIdGanador(), emp005.getIdGanador(), // ganador004 vs ganador005
                        "008-A", "012-A", NO, null, null);
                cuadroSuperior.add(emp007);
                // Final
                Emparejamientos emp008 = new Emparejamientos("008", emp006.getIdGanador(), emp007.getIdGanador(), // ganador006 vs ganador007
                        null, null, SI, null, null);
                cuadroSuperior.add(emp008);
                // Cuadro Inferior
                // 1
                Emparejamientos emp009 = new Emparejamientos("009", emp001.getIdPerdedor(), emp003.getIdPerdedor(), // perdedor001 vs perdedor003
                        "011-R", null, NO, null, null);
                cuadroInferior.add(emp009);
                // 2
                Emparejamientos emp010 = new Emparejamientos("010", emp009.getIdGanador(), emp002.getIdPerdedor(),// ganador009 vs perdedor002
                        "011-R", null, NO, null, null);
                cuadroInferior.add(emp010);
                // 3
                Emparejamientos emp011 = new Emparejamientos("011", emp010.getIdGanador(), emp007.getIdPerdedor(), // ganador010 vs perdedor007
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp011);
                // 4
                Emparejamientos emp012 = new Emparejamientos("012", emp005.getIdPerdedor(), emp004.getIdPerdedor(), // perdedor005 vs perdedor004
                        "013-R", null, NO, null, null);
                cuadroInferior.add(emp012);
                // 5
                Emparejamientos emp013 = new Emparejamientos("013", emp012.getIdGanador(), emp006.getIdPerdedor(), // ganador012 vs perdedor006
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp013);
                break;
            }
            case 9: { // 2 octavos, 4 cuartos, 2 semis y 1 final 10 Competidores
                // Octavos
                // 1
                Emparejamientos emp001 = new Emparejamientos("001", arrayBidim[8][1], arrayBidim[7][1], // 9 vs 8
                        "003-A", "010-R", NO, null, null);
                cuadroSuperior.add(emp001);
                // 2
                Emparejamientos emp002 = new Emparejamientos("002", arrayBidim[6][1], arrayBidim[9][1], // 7 vs 10
                        "006-R", "011-R", NO, null, null);
                cuadroSuperior.add(emp002);
                // Cuartos
                // 1
                Emparejamientos emp003 = new Emparejamientos("003", arrayBidim[0][1], emp001.getIdGanador(), // 1 vs ganador001
                        "007-R", "012-A", NO, null, null);
                cuadroSuperior.add(emp003);
                // 2
                Emparejamientos emp004 = new Emparejamientos("004", arrayBidim[4][1], arrayBidim[3][1], // 5 vs 4
                        "007-A", "010-A", NO, null, null);
                cuadroSuperior.add(emp004);
                // 3
                Emparejamientos emp005 = new Emparejamientos("005", arrayBidim[2][1], arrayBidim[5][1], // 3 vs 6
                        "008-R", "011-A", NO, null, null);
                cuadroSuperior.add(emp005);
                // 4
                Emparejamientos emp006 = new Emparejamientos("006", emp002.getIdGanador(), arrayBidim[5][1], // ganador001 vs 6
                        "008-A", "013-R", NO, null, null);
                cuadroSuperior.add(emp006);
                // Semis
                // 1
                Emparejamientos emp007 = new Emparejamientos("007", emp003.getIdGanador(), emp004.getIdGanador(), // ganador003 vs ganador004
                        "009-R", "015-A", NO, null, null);
                cuadroSuperior.add(emp007);
                // 2
                Emparejamientos emp008 = new Emparejamientos("008", emp005.getIdGanador(), emp006.getIdGanador(), // ganador005 vs ganador006
                        "009-A", "014-A", NO, null, null);
                cuadroSuperior.add(emp008);
                // Final
                Emparejamientos emp009 = new Emparejamientos("009", emp007.getIdGanador(), emp008.getIdGanador(), // ganador007 vs ganador008
                        null, null, SI, null, null);
                cuadroSuperior.add(emp009);
                // Cuadro inferior
                // 1
                Emparejamientos emp010 = new Emparejamientos("010", emp001.getIdPerdedor(), emp004.getIdPerdedor(), // perdedor001 vs perdedor004
                        "012-R", null, NO, null, null);
                cuadroInferior.add(emp010);
                // 2
                Emparejamientos emp011 = new Emparejamientos("011", emp002.getIdPerdedor(), emp005.getIdPerdedor(), // perdedor002 vs perdedor005
                        "013-A", null, NO, null, null);
                cuadroInferior.add(emp011);
                // 3
                Emparejamientos emp012 = new Emparejamientos("012", emp010.getIdGanador(), emp003.getIdPerdedor(), // ganador010 vs perdedor003
                        "014-R", null, NO, null, null);
                cuadroInferior.add(emp012);
                // 4
                Emparejamientos emp013 = new Emparejamientos("013", emp006.getIdPerdedor(), emp011.getIdGanador(), // perdedor006 vs ganador011
                        "015-R", null, NO, null, null);
                cuadroInferior.add(emp013);
                // 5
                Emparejamientos emp014 = new Emparejamientos("014", emp012.getIdGanador(), emp008.getIdPerdedor(), // ganador012 vs perdedor008
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp014);
                // 6
                Emparejamientos emp015 = new Emparejamientos("015", emp013.getIdGanador(), emp007.getIdPerdedor(), // ganador013 vs perdedor007
                        null, null, TERCEROS, null, null);
                cuadroInferior.add(emp015);
                break;
            }*/
            // El resto de casos, es decir, cuando haya más de 10 competidores en una categoría.
        }

    }


    public void addCombate(String idCamp, String idMod, final String idCat, String idRojo, String idAzul, final int numCombate){
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
    }
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

    // region Añadir Combate
    public void addCombate(){
        // Evitar duplicados comprobando el número del combate. No puede haber dos combates con el mismo número en una categoría

        // Añadir el Combate si no existe
    }

    public void updateListaAsaltos(int idCombate, List<Asaltos> lista){

    }
    // endregion

    // region Añadir Asalto
    public void addAsalto(int idCombate){

    }
    // endregion
}
