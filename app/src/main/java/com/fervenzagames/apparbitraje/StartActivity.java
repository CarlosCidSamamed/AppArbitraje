package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.User_Activities.LoginActivity;
import com.fervenzagames.apparbitraje.User_Activities.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLoginBtn;

    private int tipo;

    private DatabaseReference mRootDB;
    private DatabaseReference mArbitrosDB;

    private List<Arbitros> arbitrosList;
    private Arbitros arbitro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arbitrosList = new ArrayList<>();
        arbitro = new Arbitros();

        // Detectar el tipo de dispositivo para cargar el layout correspondiente
        tipo = detectarTipoDispostivo();
        if(tipo == 1){ // TABLET
            setContentView(R.layout.activity_start);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mRegBtn = (Button) findViewById(R.id.start_reg_btn);
            mLoginBtn = (Button) findViewById(R.id.start_login_btn);
        } else if(tipo == 0) { // MÓVIL
            setContentView(R.layout.phone_start);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mRegBtn = (Button) findViewById(R.id.phone_start_reg_btn);
            mLoginBtn = (Button) findViewById(R.id.phone_start_login_btn);
        }

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Una vez que se pulsa el botón de Registro de una nueva cuenta se lanza un intent
                * desde esta actividad (StartActivity.this) a la actividad de Registro (RegisterActivity.class)*/
                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        try {
            String uid = extras.getString("uid");
            Toast.makeText(this, "(Start) Extras -- uid " + uid, Toast.LENGTH_SHORT).show();
            //actualizarEstadoArbitro(uid, false);
            //obtenerArbitro(uid);
            obtenerListaArbitros();
            //actualizarEstadoArbitro2(arbitro);
            // verValorConectado(uid);
        } catch (NullPointerException e) {
            Toast.makeText(this, "(Start) No se ha podido recuperar el UID de los extras...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /*    @Override
    protected void onResume() {
        super.onResume();

        String uid = null;
        // Modificar el campo conectado del árbitro si se ha cerrado sesión antes de volver a esta actividad.
        try {
            Bundle extras = getIntent().getExtras();
            uid = extras.getString("uid");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if(uid != null){
            Toast.makeText(this, "Extras (StartActivity) --> UID : " + uid, Toast.LENGTH_SHORT).show();
            actualizarEstadoArbitro(uid, false);
        } else {
            Toast.makeText(this, "No se ha pasado el UID", Toast.LENGTH_SHORT).show();
        }
    }*/

    // Este método se encarga de detectar el tamaño de la pantalla para saber si el dispostivo es un móvil o una tablet.
    // MÓVIL <7 pulgadas TABLET 7 o más.
    // MÓVIL  --> 0
    // TABLET --> 1
    public int detectarTipoDispostivo(){

        int tipo = 0;
        int screenWidth = 0;
        int screenHeight = 0;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        double x = Math.pow(screenWidth / dm.xdpi, 2);
        double y = Math.pow(screenHeight / dm.xdpi, 2);

        double screenInches = Math.sqrt(x + y);
        screenInches = (double) Math.round(screenInches * 10) / 10;

        Toast.makeText(this, "Tamaño en Pulgadas de la Pantalla --> " + screenInches, Toast.LENGTH_LONG).show();

        if(screenInches < 7.0f)
        {
            tipo = 0; // MÓVIL
        } else if (screenInches >= 7.0f){
            tipo = 1; // TABLET
        }

        return tipo;
    }

    public void actualizarEstadoArbitro(String uid, final boolean estado){
        // Como parámetros se pasan:
        // 1. el UID que permite identificar al usuario y al árbitro correspondiente.
        // 2. el nuevo estado del árbitro.
        final String id = uid;

        // El proceso de actualizar usando updateChildren lo he cogido de la documentación de Firebase: https://firebase.google.com/docs/database/android/read-and-write?hl=es-419

        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(id);

        final Query consulta = mArbitrosDB;

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    // Comprobación de la ruta de la consulta
                    Toast.makeText(StartActivity.this, "Ruta de la consulta (conectado = FALSE) --> " + consulta.getRef().toString(), Toast.LENGTH_SHORT).show();
                    // Recuperamos los datos del árbitro
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    // Modificamos el estado
                    arbi.setConectado(estado);
                    Toast.makeText(StartActivity.this, "(Start) Actualizando estado Árbitro --> " + estado, Toast.LENGTH_SHORT).show();
                    // Actualizamos los datos del Árbitro en la BD con el nuevo estado de conexión.
                    Map<String, Object> nuevoArbi = arbi.toMap();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("/Arbitros/" + id, nuevoArbi);
                    mRootDB.updateChildren(updates);
                    Toast.makeText(StartActivity.this, "(Start) Nuevo estado Arbitro en BD --> " + arbi.getConectado(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StartActivity.this, "Error al localizar al Árbitro cuyo ID es " + id, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void verValorConectado(final String uid){
        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(uid);
        Query consulta = mArbitrosDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(StartActivity.this, "Error al recuperar los datos del árbitro cuyo id es " + uid, Toast.LENGTH_SHORT).show();
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    Toast.makeText(StartActivity.this, "(Start) El árbitro " + arbi.getNombre() + " tiene el estado de " + arbi.getConectado(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerArbitro(final String uid){

        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros");

        // Recuperar el arbitro de la BD
        Query consulta = mArbitrosDB.orderByChild("idArbitro").equalTo(uid);
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(StartActivity.this, "(obtenerArbitro - Start) Error al localizar a los Árbitros...", Toast.LENGTH_SHORT).show();
                } else {
                    arbitro = dataSnapshot.getValue(Arbitros.class);
                    Toast.makeText(StartActivity.this, "(obtenerArbitro - Start) Árbitro : " + arbitro.getNombre(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void obtenerListaArbitros(){
        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros");
        Query consulta = mArbitrosDB.orderByChild("idArbitro");
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(StartActivity.this, "(Start) No existen Árbitros en la BD", Toast.LENGTH_SHORT).show();
                } else {
                    arbitrosList.clear();
                    for(DataSnapshot arbi: dataSnapshot.getChildren()){
                        Arbitros a = arbi.getValue(Arbitros.class);
                        arbitrosList.add(a);
                    }

                    if(arbitrosList.size() > 0) {
                        Toast.makeText(StartActivity.this, "(obtenerListaArbitros) Número de Árbitros en la BD : " + arbitrosList.size(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StartActivity.this, "(obtenerListaArbitros) Lista Árbitros VACÍA", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void actualizarEstadoArbitro2(Arbitros arbitro){
        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        Map<String ,Object> arbiMap = arbitro.toMap();

        Map<String, Object> update = new HashMap<>();
        update.put("/Arbitros/" + arbitro.getIdArbitro(), arbiMap);
        mRootDB.updateChildren(update);
    }
}
