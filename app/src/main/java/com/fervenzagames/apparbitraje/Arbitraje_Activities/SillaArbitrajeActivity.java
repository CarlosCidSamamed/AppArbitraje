package com.fervenzagames.apparbitraje.Arbitraje_Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.MiSpinnerAdapter;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SillaArbitrajeActivity extends AppCompatActivity {

    private Toolbar mTooolbar;

    private Button mPuñoRojo;
    private Button mPatUnoRojo;
    private Button mPatDosRojo;
    private Button mProyUnoRojo;
    private Button mProyDosRojo;
    private Button mAddUnoRojo;
    private Button mAddDosRojo;

    private Button mPuñoAzul;
    private Button mPatUnoAzul;
    private Button mPatDosAzul;
    private Button mProyUnoAzul;
    private Button mProyDosAzul;
    private Button mAddUnoAzul;
    private Button mAddDosAzul;

    private Button mCartulinas;

    private TextView mCrono;

    private DatabaseReference mPuntDB;
    private DatabaseReference mIncDB;
    private DatabaseReference mArbisDB;

    private String mIdJuez;
    private String mIdAsalto;
    private String mIdPunt; // Se obtiene mediante el PUSH a la BD.
    private String mIdInc;  // Se obtiene mediante el PUSH a la BD.
    
    private String mDniJuez;
    private String mIdRojo;
    private String mIdAzul;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silla_arbitraje);

        mTooolbar = findViewById(R.id.silla_arbitraje_bar);
        setSupportActionBar(mTooolbar);
        getSupportActionBar().setTitle("Arbitraje Silla");

        mPuñoRojo = findViewById(R.id.punch_Rojo_btn);
        mPatUnoRojo = findViewById(R.id.patada_1_Rojo_btn);
        mPatDosRojo = findViewById(R.id.patada_2_Rojo_btn);
        mProyUnoRojo = findViewById(R.id.proy_1_Rojo_btn);
        mProyDosRojo = findViewById(R.id.proy_2_Rojo_btn);
        mAddUnoRojo = findViewById(R.id.un_punto_Rojo_btn);
        mAddDosRojo = findViewById(R.id.dos_puntos_Rojo_btn);

        mPuñoAzul = findViewById(R.id.punch_Azul_btn);
        mPatUnoAzul = findViewById(R.id.patada_1_Azul_btn);
        mPatDosAzul = findViewById(R.id.patada_2_Azul_btn);
        mProyUnoAzul = findViewById(R.id.proy_1_Azul_btn);
        mProyDosAzul = findViewById(R.id.proy_2_Azul_btn);
        mAddUnoAzul = findViewById(R.id.un_punto_Azul_btn);
        mAddDosAzul = findViewById(R.id.dos_puntos_Azul_btn);

        mCrono = findViewById(R.id.silla_arbitraje_crono);
        mCartulinas = findViewById(R.id.cartulinas_btn);

        mPuntDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Puntuaciones");
        mIncDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Incidencias");
        mArbisDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");

        try {
            mIdJuez = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getDniJuez(mIdJuez);
        } catch (NullPointerException e) {
            Toast.makeText(this, "(SillaArbirtaje) No se ha iniciado sesión por lo que no se podrá realizar el Arbitraje.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
        Bundle extras = getIntent().getExtras();
        try {
            mIdAsalto = extras.getString("idAsalto");
        } catch (NullPointerException e) {
            Toast.makeText(this, "(SillaArbitraje) El bundle de extras no incluye el ID del Asalto. ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try {
            mIdRojo = extras.getString("idRojo");
        } catch (NullPointerException e) {
            Toast.makeText(this, "(SillaArbitraje) El bundle de extras no incluye el ID del Competidor ROJO. ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try {
            mIdAzul = extras.getString("idAzul");
        } catch (NullPointerException e) {
            Toast.makeText(this, "(SillaArbitraje) El bundle de extras no incluye el ID del Competidor AZUL. ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //region Click Botones
        //region ROJO
        mPuñoRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mPuñoRojo");
            }
        });
        mPatUnoRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mPatUnoRojo");
            }
        });
        mPatDosRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mPatDosRojo");
            }
        });
        mProyUnoRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mProyUnoRojo");
            }
        });
        mProyDosRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mProyDosRojo");
            }
        });
        mAddUnoRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mAddUnoRojo");
            }
        });
        mAddDosRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mAddDosRojo");
            }
        });
        //endregion
        //region AZUL
        mPuñoAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mPuñoAzul");
            }
        });
        mPatUnoAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mPatUnoAzul");
            }
        });
        mPatDosAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mPatDosAzul");
            }
        });
        mProyUnoAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mProyUnoAzul");
            }
        });
        mProyDosAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mProyDosAzul");
            }
        });
        mAddUnoAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mAddUnoAzul");
            }
        });
        mAddDosAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluarBotonPulsado("mAddDosAzul");
            }
        });
        //endregion
        //endregion
    }

    // Este método recibe un objeto de tipo Puntuaciones y lo inserta en la BD
    public void insertarPuntuacionBD (final Puntuaciones puntuacion){
        mIdPunt = mPuntDB.child(mIdAsalto).push().getKey(); // Puntuaciones --- idAsalto --- idPunt --- datos de la puntuación
        puntuacion.setId(mIdPunt); // Se actualiza el ID al generarlo
        /* NO será necesario comporbar si existen duplicados porque cada una de las puntuaciones de un asalto tendrán los siguientes datos que las diferencien:
            idPunt --> Se genera con el push
            idJuez --> Depende del árbitro que haya iniciado sesión en cada dispositivo
        */
        Query consulta = mPuntDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Insertar la Puntuación en la BD.
                mPuntDB.child(mIdAsalto).child(mIdPunt).setValue(puntuacion);
                // Toast de Comprobación
                Toast.makeText(SillaArbitrajeActivity.this, "(SillaArbitraje) El juez " + mIdJuez + " ha añadido la Puntuación " + mIdPunt + " al Asalto " + mIdAsalto, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //region CRONO

    //endregion

    //region Botones
    // Método que evalúa el nombre del botón que se ha pulsado para generar la puntuación o incidencia correspondiente y añadirla a la BD.
    private void evaluarBotonPulsado(String nombreBoton){
        String valorCrono;
        switch(nombreBoton){
            //region ROJO
            case "mPuñoRojo":{
                // Obtener el valor del crono
                valorCrono = mCrono.getText().toString();
                // La Puntuación se genera con el id como null porque antes de insertarlo en la BD se genera el ID y se actualiza su valor.
                Puntuaciones punt = new Puntuaciones(null ,mDniJuez, mIdAsalto, mIdRojo, 1, "Golpe", "", "Puñetazo", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mPatUnoRojo":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdRojo, 1, "Golpe", "", "Patada", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mPatDosRojo":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdRojo, 2, "Golpe", "", "Patada", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mProyUnoRojo":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdRojo, 1, "Proyección", "", "Proyección", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mProyDosRojo":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdRojo, 2, "Proyección", "", "mProyección", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mAddUnoRojo":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdRojo, 1, "+1", "", "", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mAddDosRojo":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdRojo, 2, "+2", "", "", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            //endregion
            //region AZUL
            case "mPuñoAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 1, "Golpe", "", "Puñetazo", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mPatUnoAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 1, "Golpe", "", "Patada", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mPatDosAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 2, "Golpe", "", "Patada", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mProyUnoAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 1, "Proyección", "", "Proyección", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mProyDosAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 2, "Proyección", "", "Proyección", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mAddUnoAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 1, "+1", "", "", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            case "mAddDosAzul":{
                valorCrono = mCrono.getText().toString();
                Puntuaciones punt = new Puntuaciones(null, mDniJuez, mIdAsalto, mIdAzul, 2, "+2", "", "", valorCrono);
                insertarPuntuacionBD(punt);
                break;
            }
            //endregion
            default:{
                break;
            }
        }
    }
    //endregion

    // Para crear objetos de tipo Puntuaciones e Incidencias necesitaremos el DNI del Juez correspondiente.
    // El DNI se obtendrá en la rama Arbitros
    private void getDniJuez(String idJuez){
        Query consulta = mArbisDB.child(idJuez);
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(SillaArbitrajeActivity.this, "(SillaArbitraje) No se encuentra en la BD ningún Árbitro con ese ID", Toast.LENGTH_SHORT).show();
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    try {
                        mDniJuez = arbi.getDni();
                    } catch (NullPointerException e) {
                        Toast.makeText(SillaArbitrajeActivity.this, "(SillaArbitraje} DNI no encontrado para ese ID", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
