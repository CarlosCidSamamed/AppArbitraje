package com.fervenzagames.apparbitraje.Arbitraje_Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.CampeonatosActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.User_Activities.SettingsActivity;
import com.fervenzagames.apparbitraje.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesaArbitrajeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final long START_TIME_IN_MILLIS = 60000;    // Un minuto
    private static final long START_TIME_IN_MILLIS_2 = 120000; // Dos minutos
    private static final long START_TIME_IN_MILLIS_3 = 180000; // Tres minutos

    private Toolbar mToolbar;

    //region Atributos para el CRONO
    private TextView mCrono;
    private long pauseOffset;
    private boolean mTimerRunning;

    private CountDownTimer mCountdownTimer;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_2; // DOS MINUTOS

    private enum Estado  {COMBATE, DESCANSO_ENTRE_ASALTOS, DESCANSO_ENTRE_COMBATES}
    private Estado estado;

    private Button mStartPauseBtn;
    private Button mResetBtn;

    private Button mFinAsalto;
    private Button mFinCombate;
    //endregion

    //region Database References
    private DatabaseReference mCombateDB;
    private DatabaseReference mCompetidoresDB;
    private DatabaseReference mAsaltoDB;
    private DatabaseReference mArbisDB;
    private DatabaseReference mIncDB;
    //endregion

    private String mDniJuez;
    private String mIdInc;
    private String mIdJuez;
    private String mIdAsalto;

    private String mNumAsalto;

    // Contadores de Incidencias a tener en cuenta para mostrar en la UI y para terminar el combate en casos especiales.
    private int mContadorAmRojo;
    private int mContadorAmAzul;
    private int mContadorPenRojo;
    private int mContadorPenAzul;
    private int mContadorSalRojo;
    private int mContadorSalAzul;
    private int mContadorCuenRojo;
    private int mContadorCuentAzul;
    // Contadores para cada competidor en cada asalto
    // Amonestaciones
    private int mContAmRojoR1;
    private int mContAmRojoR2;
    private int mContAmRojoR3;

    private int mContAmAzulR1;
    private int mContAmAzulR2;
    private int mContAmAzulR3;

    //Penalizaciones
    private int mContPenRojoR1;
    private int mContPenRojoR2;
    private int mContPenRojoR3;

    private int mContPenAzulR1;
    private int mContPenAzulR2;
    private int mContPenAzulR3;

    //Salidas
    private int mContSalRojoR1;
    private int mContSalRojoR2;
    private int mContSalRojoR3;

    private int mContSalAzulR1;
    private int mContSalAzulR2;
    private int mContSalAzulR3;

    //Cuentas
    private int mContCuenRojoR1;
    private int mContCuenRojoR2;
    private int mContCuenRojoR3;

    private int mContCuenAzulR1;
    private int mContCuenAzulR2;
    private int mContCuenAzulR3;


    //region Atributos UI ROJO
    private CircleImageView mFotoRojo;
    private TextView mNombreRojo;
    //region Botones
    private Button mAmRojoBtn;
    private Button mPenRojoBtn;
    private Button mSalidasRojoBtn;
    private Button mCuentasRojoBtn;
    //endregion

    //region TextViews
    //region Round 1
    private TextView mAmRojoR1Text;
    private TextView mPenRojoR1Text;
    private TextView mSalidasRojoR1Text;
    private TextView mCuentasRojoR1Text;
    //endregion

    //region Round 2
    private TextView mAmRojoR2Text;
    private TextView mPenRojoR2Text;
    private TextView mSalidasRojoR2Text;
    private TextView mCuentasRojoR2Text;
    //endregion

    //region Round 3
    private TextView mAmRojoR3Text;
    private TextView mPenRojoR3Text;
    private TextView mSalidasRojoR3Text;
    private TextView mCuentasRojoR3Text;
    //endregion
    //endregion

    //endregion

    //region Atributos UI AZUL
    private CircleImageView mFotoAzul;
    private TextView mNombreAzul;
    //region Botones
    private Button mAmAzulBtn;
    private Button mPenAzulBtn;
    private Button mSalidasAzulBtn;
    private Button mCuentasAzulBtn;
    //endregion

    //region TextViews

    //region Round 1
    private TextView mAmAzulR1Text;
    private TextView mPenAzulR1Text;
    private TextView mSalidasAzulR1Text;
    private TextView mCuentasAzulR1Text;
    //endregion

    //region Round 2
    private TextView mAmAzulR2Text;
    private TextView mPenAzulR2Text;
    private TextView mSalidasAzulR2Text;
    private TextView mCuentasAzulR2Text;
    //endregion

    //region Round 3
    private TextView mAmAzulR3Text;
    private TextView mPenAzulR3Text;
    private TextView mSalidasAzulR3Text;
    private TextView mCuentasAzulR3Text;
    //endregion


    //endregion

    //endregion

    //region Atributos KO TKO
    private Button KOBtn;
    private Button TKOBtn;
    //endregion

    //region Atributos numCombate y numAsalto
    private TextView mNumCombateText;
    private TextView mNumAsaltoText;
    //endregion

    //region Atributos Trabajo con la BD
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa_arbitraje);

        mAuth = FirebaseAuth.getInstance();
        mIdJuez = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getDniJuez(mIdJuez);

        mToolbar = findViewById(R.id.mesa_arbitraje_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SD -85 M ABS");

        mCrono = findViewById(R.id.crono);
        mStartPauseBtn = findViewById(R.id.pausa_btn);
        mStartPauseBtn.setText("Iniciar");
        mResetBtn = findViewById(R.id.reset_btn);

        mFinAsalto = findViewById(R.id.terminar_asalto);
        mFinCombate = findViewById(R.id.terminar_combate);

        estado = Estado.DESCANSO_ENTRE_COMBATES;
        mResetBtn.setVisibility(View.INVISIBLE);

        mNumCombateText = findViewById(R.id.num_combate);
        mNumAsaltoText = findViewById(R.id.num_asalto);

        // Reproducir Sonido Campana
        final MediaPlayer player = MediaPlayer.create(MesaArbitrajeActivity.this, R.raw.bell);

        //region Botones CRONO
        mStartPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning) {
                    mResetBtn.setVisibility(View.VISIBLE);
                    pausarCrono();
                } else {
                    if(estado == Estado.COMBATE){ // Se reanuda el crono después de una pausa

                        iniciarCrono();

                    } else { // Nuevo Asalto
                        // Cambio de color a VERDE --> Estado = COMBATE
                        estado = Estado.COMBATE;
                        iniciarCrono();
                        player.start();
                    }
                }
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetBtn.setVisibility(View.INVISIBLE);
                estado = Estado.DESCANSO_ENTRE_COMBATES;
                reiniciarCrono(START_TIME_IN_MILLIS_2);
            }
        });

        mFinAsalto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = Estado.DESCANSO_ENTRE_ASALTOS;
                reiniciarCrono(START_TIME_IN_MILLIS); // UN MINUTO de DESCANSO
                iniciarCrono();
                player.start();
            }
        });

        mFinCombate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = Estado.DESCANSO_ENTRE_COMBATES;
                reiniciarCrono(START_TIME_IN_MILLIS_3); // TRES MINUTOS de DESCANSO
                iniciarCrono();
                player.start();
            }
        });

        actualizarCrono();
        //endregion

        //region Datos Bundle Extra y DBref
        Intent intent =  getIntent();
        Bundle extras = intent.getExtras();
        final String idComb = extras.getString("idComb");
        final String idAsalto = extras.getString("idAsalto");
        mIdAsalto = idAsalto;

        //String idCamp = extras.getString("idCamp");
        //String idMod = extras.getString("idMod");
        String idCat = extras.getString("idCat");
        //mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCat);   // Lista de Combates de esta Categoría.
        mCompetidoresDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");          // Lista de Compatidores de la BD.

        mArbisDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        mIncDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Incidencias");

        //endregion

        //region UI ROJO
        // Cargar foto Rojo y Nombre Rojo
        mFotoRojo = (CircleImageView) findViewById(R.id.foto_rojo);
        mNombreRojo = (TextView) findViewById(R.id.nombre_rojo);
        final String idRojo = extras.getString("idRojo");
        Toast.makeText(this, "MesaArbitraje --- DNI Rojo --> " + idRojo, Toast.LENGTH_SHORT).show();
        Query consulta = mCompetidoresDB.child(idRojo);
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Competidores comp = dataSnapshot.getValue(Competidores.class);
                Picasso.get().load(comp.getFoto()).into(mFotoRojo);
                String nombreCompleto = comp.getNombre() + " " + comp.getApellido1() + " " + comp.getApellido2();
                mNombreRojo.setText(nombreCompleto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion

        //region UI AZUL
        mFotoAzul = (CircleImageView) findViewById(R.id.foto_azul);
        mNombreAzul = (TextView) findViewById(R.id.nombre_azul);
        final String idAzul = extras.getString("idAzul");
        Query consulta2 = mCompetidoresDB.child(idAzul);
        consulta2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Competidores comp = dataSnapshot.getValue(Competidores.class);
                Picasso.get().load(comp.getFoto()).into(mFotoAzul);
                String nombreCompleto = comp.getNombre() + " " + comp.getApellido1() + " " + comp.getApellido2();
                mNombreAzul.setText(nombreCompleto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion

        //region Número de Combate y de Asalto
        cargarDatosCombate(idCat, idComb);
        cargarDatosAsalto(idComb, idAsalto);
        //endregion

        //region Botones ROJO onClick Events
        mAmRojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Amonestación", idAsalto, idRojo, idComb, "Rojo");
            }
        });
        mPenRojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Penalización", idAsalto, idRojo, idComb, "Rojo");
            }
        });
        mSalidasRojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Salida", idAsalto, idRojo, idComb, "Rojo");
            }
        });
        mCuentasRojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Cuenta", idAsalto, idRojo, idComb, "Rojo");
            }
        });
        //endregion

        //region Botones AZUL onClick Events
        mAmAzulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Amonestaciones", idAsalto, idAzul, idComb, "Azul");
            }
        });
        mPenAzulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Penalización", idAsalto, idAzul, idComb, "Azul");
            }
        });
        mSalidasAzulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Salida", idAsalto, idAzul, idComb, "Azul");
            }
        });
        mCuentasAzulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirIncidencia("Cuenta", idAsalto, idAzul, idComb, "Azul");
            }
        });
        //endregion

    }

    //region CRONO Comportamiento y Definición
    // Métodos para el CRONÓMETRO
    public void iniciarCrono (){ //START
        mCountdownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) { // Empieza con el valor indicado en mTimeLeftInMillis y se actualiza cada 1000 ms (cada segundo)
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                actualizarCrono();
            }

            @Override
            public void onFinish() {
                // Vamos a especificar qué pasará cuando el crono acabe de contar.
                // Puede ser al final de un asalto, al final del descanso entre asaltos o al final de un descanso entre combates.
                /*switch(estado){
                    case COMBATE:{
                        // Si un competidor ha ganado el combate, es decir, FIN COMBATE.
                        // Deberemos recopilar los datos del combate, es decir, ganador y motivo para actualizar la entrada correspondiente a este combate en la BD.
                        // Número de asaltos ganados para cada competidor.
                        break;
                    }
                    case DESCANSO_ENTRE_ASALTOS:{
                        // Si debemos preparar el crono para el siguiente asalto.
                        // Deberemos recopilar los datos del asalto, es decir, ganador, motivo, descripción, puntuaciónRojo, puntuaciónAzul y duración del asalto.
                        // Con esos datos podremos actualizar la entrada correspondiente a este asalto en la BD.
                        // Incrementar el número de asaltos ganados por el ganador.
                        break;
                    }
                    case DESCANSO_ENTRE_COMBATES:{
                        // Si debemos preparar el crono para comenzar un nuevo combate, es decir, PRIMER asalto.
                        // Inicializar las variables del combate y del primer asalto.
                        break;
                    }
                }*/
                estado = Estado.DESCANSO_ENTRE_ASALTOS;
                mTimerRunning = false;
                mStartPauseBtn.setText("Iniciar");
                //reiniciarCrono(START_TIME_IN_MILLIS);
            }
        }.start(); // Iniciar el CountDownTimer que hemos creado.

        mTimerRunning = true;
        mStartPauseBtn.setText("Pausar");
    }

    public void pausarCrono (){ // PAUSE
        mCountdownTimer.cancel();
        mTimerRunning = false;
        mStartPauseBtn.setText("Reanudar");
        actualizarCrono();
    }

    public void reiniciarCrono (long startTime){ // RESET
        // estado = Estado.COMBATE;
        mCountdownTimer.cancel();
        mTimerRunning = false;
        mTimeLeftInMillis = startTime;
        mStartPauseBtn.setText("Iniciar");
        actualizarCrono();
    }

    public void actualizarCrono() { // Actualizar el TextView con el crono
        int minutos = (int) mTimeLeftInMillis / 1000 / 60;
        int segundos = (int) mTimeLeftInMillis / 1000 % 60;

        String tiempoFormateado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);

        // Dependiendo del valor del atributo Estado se deberá modificar el color del Texto.
        switch (estado) {
            case COMBATE:{
                mCrono.setTextColor(getResources().getColor(R.color.colorVerde));
                break;
            }
            case DESCANSO_ENTRE_ASALTOS:{
                mCrono.setTextColor(getResources().getColor(R.color.colorRojo));
                break;
            }
            case DESCANSO_ENTRE_COMBATES:{
                mCrono.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                break;
            }
            default:{
                mCrono.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                break;
            }
        }

        mCrono.setText(tiempoFormateado);
    }
    //endregion

    //region Trabajo con la DB

    //region Cargar Datos desde la BD
    // Combate
    private void cargarDatosCombate(String idCat, String idCombate){
        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCat).child(idCombate);
        Query consulta = mCombateDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "Error al cargar los datos del Combate...", Toast.LENGTH_SHORT).show();
                } else {
                    Combates comb = dataSnapshot.getValue(Combates.class);
                    // Número de Combate
                    try {
                        String num = "COMBATE \n" + comb.getNumCombate();
                        mNumCombateText.setText(num);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // Asalto
    private void cargarDatosAsalto(String idCombate, String idAsalto){
        mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(idCombate).child(idAsalto);
        Query consulta = mAsaltoDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "Error al cargar los datos del Asalto...", Toast.LENGTH_SHORT).show();
                } else {
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                    // Número de Asalto
                    try {
                        String num = "ASALTO \n" + asalto.getNumAsalto();
                        mNumAsaltoText.setText(num);
                        mNumAsalto = num;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Vamos a localizar el combate en cuestión mediante su Número de Combate dentro de la Categoría y la Modalidad indicadas.
    // En el Bundle se le pasan el idCamp, idMod e idCat.
    public Combates buscarCombate(Bundle extras, String numCombate){

        Combates combate = new Combates();
        return combate;

    }

    // Método para obtener el número de un tipo de incidencia para el asalto indicado. Por ejemplo, número de Amonestaciones para el primer asalto del combate indicado.
    private void obtenerNumIncidencias(final String tipo, String idAsalto, final String numAsalto, String idCombate, final String lado){
        final List<Incidencias> lista2 = new ArrayList<>();
        mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(idCombate).child(idAsalto); // Referencia al asalto deseado.
        final Query consulta = mAsaltoDB;
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No es posible recuperar el número de " + tipo + " para el asalto actual...", Toast.LENGTH_SHORT).show();
                } else {
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                    // Una vez obtenidos los datos del asalto deseado deberemos localizar su lista de Incidencias y contar las del tipo deseado.
                    try {
                        List<Incidencias> lista = asalto.getListaIncidencias();
                        // Recorreremos la lista de Incidencias recuperando las que cumplan las condiciones.
                        if(lista.size() > 0){
                            for(int i = 0; i < lista.size(); i++){
                                Incidencias inc = lista.get(i);
                                if(inc.getTipo().equals(tipo)){
                                    lista2.add(inc);
                                }
                            }
                            // Obtenemos el número de incidencias al consultar el tamaño de la lista2
                            int contador = lista2.size();
                            if(contador <= 0){
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No existen incidencias de tipo " + tipo + " para este asalto...", Toast.LENGTH_SHORT).show();
                            } else {
                                // Actualizar el valor del contador correspondiente
                                switch(tipo){
                                    case "Amonestación":{
                                        if (numAsalto.equals("1")){
                                            mContAmRojoR1 = contador;
                                        } else if(numAsalto.equals("2")){
                                            mContAmRojoR2 = contador;
                                        } else if(numAsalto.equals("3")){
                                            mContAmRojoR3 = contador;
                                        } // Actualizar el valor correspondiente al contador global de ese tipo de incidencias para el combate
                                        if(lado.equals("Rojo")){
                                            mContadorAmRojo += contador;
                                        } else if(lado.equals("Azul")){
                                            mContadorAmAzul += contador;
                                        }
                                        break;
                                    }
                                    case "Penalización":{
                                        if (numAsalto.equals("1")){
                                            mContPenRojoR1 = contador;
                                        } else if(numAsalto.equals("2")){
                                            mContPenRojoR2 = contador;
                                        } else if(numAsalto.equals("3")){
                                            mContPenRojoR3 = contador;
                                        }
                                        if(lado.equals("Rojo")){
                                            mContadorPenRojo += contador;
                                        } else if(lado.equals("Azul")){
                                            mContadorPenAzul += contador;
                                        }
                                        break;
                                    }
                                    case "Salida":{
                                        if (numAsalto.equals("1")){
                                            mContSalRojoR1 = contador;
                                        } else if(numAsalto.equals("2")){
                                            mContSalRojoR2 = contador;
                                        } else if(numAsalto.equals("3")){
                                            mContSalRojoR3 = contador;
                                        }
                                        if(lado.equals("Rojo")){
                                            mContadorSalRojo += contador;
                                        } else if(lado.equals("Azul")){
                                            mContadorSalAzul += contador;
                                        }
                                        break;
                                    }
                                    case "Cuenta":{
                                        if (numAsalto.equals("1")){
                                            mContCuenRojoR1 = contador;
                                        } else if(numAsalto.equals("2")){
                                            mContCuenRojoR2 = contador;
                                        } else if(numAsalto.equals("3")){
                                            mContCuenRojoR3 = contador;
                                        }
                                        if(lado.equals("Rojo")){
                                            mContadorCuenRojo += contador;
                                        } else if(lado.equals("Azul")){
                                            mContadorCuentAzul += contador;
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Este Asalto no posee Incidencias registradas", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NullPointerException e) {
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al recuperar la lista de Incidencias de este Asalto", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Para crear objetos de tipo Puntuaciones e Incidencias necesitaremos el DNI del Juez correspondiente.
    // El DNI se obtendrá en la rama Arbitros
    private void getDniJuez(String idJuez){
        Query consulta = mArbisDB.child(idJuez);
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No se encuentra en la BD ningún Árbitro con ese ID", Toast.LENGTH_SHORT).show();
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    try {
                        mDniJuez = arbi.getDni();
                    } catch (NullPointerException e) {
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje} DNI no encontrado para ese ID", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //endregion

    //region Guardar Datos en la BD


    public void insertarIncidenciaBD(final Incidencias incidencia){
        mIdInc = mIncDB.child(mIdAsalto).push().getKey(); // Incidencia --- idAsalto --- idIncidencia --- datos de la incidencia
        // El ID de la Incidencia generado con el push siempre será único --> No será necesario comprobar duplicados.
        incidencia.setId(mIdInc); // Se actualiza el ID al generarlo
        Query consulta = mIncDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Insertar la Incidencia en la BD
                mIncDB.child(mIdAsalto).child(mIdInc).setValue(incidencia);
                // Toast de Comprobación
                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) El juez " + mIdJuez + " ha añadido la Incidencia " + mIdInc + " al Asalto " + mIdAsalto, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método para añadir una Incidencia a la BD, obtener el número de Incidencias de ese tipo y actualizar la UI.
    public void añadirIncidencia(String tipo, String idAsalto, String idComp, String idComb, String lado){
        switch(tipo){
            case "Amonestación":{
                // Competidor ROJO
                if(lado.equals("Rojo")){
                    switch(mNumAsalto){
                        case "1":{ // R1
                            // En el primer Asalto, si se puls el botón de Amonestaciones para el Competidor Rojo
                            // Se inserta una Amonestación en la BD con los datos del Juez y del Asalto correspondientes...
                            String valorCrono = mCrono.getText().toString();
                            Incidencias inc = new Incidencias(null, mDniJuez, idComp, 1, "Amonestación", "", valorCrono);
                            insertarIncidenciaBD(inc);
                            // y se muestra el valor actualizado en la UI en el TextView correspondiente a las Amonestaciones del primer Asalto.
                            obtenerNumIncidencias("Amonestación", idAsalto, mNumAsalto, idComb, "Rojo");
                            String num = String.valueOf(mContAmRojoR1);
                            mAmRojoR1Text.setText(num);
                            //TODO: Añadir comprobación en todos los asaltos para el número máximo y actual de Amonestaciones, Penalizaciones, Salidas y Cuentas.
                            //TODO: Condiciones especiales de Fin de Asalto y de Combate.
                            break;
                        }
                        case "2":{ // R2
                            String valorCrono = mCrono.getText().toString();
                            Incidencias inc = new Incidencias(null, mDniJuez, idComp, 1, "Amonestación", "", valorCrono);
                            insertarIncidenciaBD(inc);
                            obtenerNumIncidencias("Amonestación", idAsalto, mNumAsalto, idComb, "Rojo");
                            String num = String.valueOf(mContAmRojoR2);
                            mAmRojoR2Text.setText(num);
                            break;
                        }
                        case "3":{ // R3
                            String valorCrono = mCrono.getText().toString();
                            Incidencias inc = new Incidencias(null, mDniJuez, idComp, 1, "Amonestación", "", valorCrono);
                            insertarIncidenciaBD(inc);
                            obtenerNumIncidencias("Amonestación", idAsalto, mNumAsalto, idComb, "Rojo");
                            String num = String.valueOf(mContAmRojoR3);
                            mAmRojoR3Text.setText(num);
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                } else if(lado.equals("Azul")){
                    // Competidor AZUL
                    // R1
                    // R2
                    // R3
                }
                break;
            }
            case "Penalización":{
                // Competidor ROJO
                // R1
                // R2
                // R3
                // Competidor AZUL
                // R1
                // R2
                // R3
                break;
            }
            case "Salida":{
                // Competidor ROJO
                // R1
                // R2
                // R3
                // Competidor AZUL
                // R1
                // R2
                // R3
                break;
            }
            case "Cuenta":{
                // Competidor ROJO
                // R1
                // R2
                // R3
                // Competidor AZUL
                // R1
                // R2
                // R3
            }
            default:{
                break;
            }
        }
    }

    //endregion

    //region Actualizar Datos en la BD

    //endregion

    //endregion

    /*--------------------------------------------------- MENU PRINCIPAL ---------------------------------------------------*/
    //region Menú Principal
    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        // Si el usuario no ha iniciado sesión deberemos abrir la pantalla de LOGIN
        if(currentUser == null){
            sendToStart();
        }
    }

    private void sendToStart() {
        /* Se crea un Intent para la página de LOGIN */
        Intent startIntent = new Intent(MesaArbitrajeActivity.this, StartActivity.class);
        /* Se inicia ese Intent */
        startActivity(startIntent);
        /* Con esta línea evitamos que al pulsar el botón para retroceder se vuelva a esta actividad. */
        finish();
    }

    // Selecionar el menú principal que deseamos y mostrarlo.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    // ¿Qué ocurre si se pulsa algún botón en el menú principal?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.main_logout_btn:{
                // Vamos a incluir el código para cerrar la sesión.
                FirebaseAuth.getInstance().signOut(); // Cerrar sesión con Firebase Auth.
                sendToStart(); // Redirigir al inicio de la app.
                break;
            }
            case R.id.main_settings_btn:{
                Toast.makeText(MesaArbitrajeActivity.this, "Ha pulsado el botón Opciones", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_account_settings:{
                // Toast.makeText(MainActivity.this, "Ha pulsado el botón de Cuenta de Usuario", Toast.LENGTH_SHORT).show();
                // Crear el Intent de la página de Settings
                Intent sett_intent = new Intent(MesaArbitrajeActivity.this, SettingsActivity.class);
                // E iniciarlo
                startActivity(sett_intent);
                break;
            }
            case R.id.main_app_type:{
                Toast.makeText(MesaArbitrajeActivity.this, "Ha pulsado el botón de Tipo de App", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_ver_campeonatos:{
                Toast.makeText(MesaArbitrajeActivity.this, "Ver los Campeonatos de la BD", Toast.LENGTH_SHORT).show();
                Intent campIntent = new Intent(MesaArbitrajeActivity.this, CampeonatosActivity.class);
                startActivity(campIntent);
                break;
            }
            case R.id.main_arbitrar_combate:{
                Intent arbitrarIntent = new Intent(MesaArbitrajeActivity.this, MesaArbitrajeActivity.class);
                startActivity(arbitrarIntent);
            }
        }

        return true;
    }
    //endregion
}
