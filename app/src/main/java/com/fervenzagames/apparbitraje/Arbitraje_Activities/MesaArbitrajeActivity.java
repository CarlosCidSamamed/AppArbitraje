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
import com.fervenzagames.apparbitraje.Models.DatosExtraZonasCombate;
import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.Models.Modalidades;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.Models.ZonasCombate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesaArbitrajeActivity extends AppCompatActivity {

    //TODO: Añadir comprobación en todos los asaltos para el número máximo y actual de Amonestaciones, Penalizaciones, Salidas y Cuentas.
    //TODO: Condiciones especiales de Fin de Asalto y de Combate.
    // SANDA y KC -->
    //      2 Salidas o 2 cuentas --> FIN ASALTO
    //      3 cuentas o 3 penalizaciones --> FIN COMBATE
    // QINGDA -->
    //      3 Salidas --> FIN ASALTO
    //      3 puntos de descuento (Am o Pen) --> FIN ASALTO
    //      6 puntos de descuento (Am o Pen) --> FIN COMBATE
    // SJ -->
    //      8 puntos de ventaja --> FIN COMBATE

    private String mModalidadCombate;

    private final static String SANDA = "Sanda SD";
    private final static String QINGDA = "Qingda QD";
    private final static String SHUAIJIAO = "Shuai Jiao SJ";
    private final static String KUNGFUCOMBAT = "Kungfu Combat KC";

    private int mNumAsaltosGanadosRojo = 0;
    private int mNumAsaltosGanadosAzul = 0;

    private int mNumAsaltosCombate = 3; // Nº de asaltos totales del Combate. Por defecto será 3.

    private int mPuntFinAsaltoRojo = 0;
    private int mPuntFinAsaltoAzul = 0;

    private int mPuntRojo = 0;
    private int mPuntAzul = 0;

    private boolean mAcabarAsalto;
    private boolean mAcabarCombate;
    private int resAsalto = 0;
    private String motivo = "";

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

    private String mIdCamp;
    private ZonasCombate mZonaActual;

    //region Database References
    private DatabaseReference mRootDB;
    private DatabaseReference mCombateDB;
    private DatabaseReference mCompetidoresDB;
    private DatabaseReference mAsaltoDB;
    private DatabaseReference mArbisDB;
    private DatabaseReference mPuntDB;
    private DatabaseReference mIncDB;
    private DatabaseReference mModDB;
    private DatabaseReference mZonaDB;
    //endregion

    private Combates mCombateActual;

    private List<Puntuaciones> mListaPuntRojo;
    private List<Incidencias> mListaIncRojo;
    private List<Puntuaciones> mListaPuntAzul;
    private List<Incidencias> mListaIncAzul;

    private int mSumaPuntRojo;
    private int mSumaIncRojo;
    private int mSumaPuntAzul;
    private int mSumaIncAzul;

    private int mMediaPuntRojo;
    private int mMediaPuntAzul;

    // Variables para cada juez de silla
    private int mSumaPuntRojoSillaUno;
    private int mSumaPuntRojoSillaDos;
    private int mSumaPuntRojoSillaTres;
    private int mSumaPuntRojoSillaCuatro;
    private int mSumaPuntRojoSillaCinco;

    private int mSumaPuntAzulSillaUno;
    private int mSumaPuntAzulSillaDos;
    private int mSumaPuntAzulSillaTres;
    private int mSumaPuntAzulSillaCuatro;
    private int mSumaPuntAzulSillaCinco;

    private String mDniJuez;
    private String mIdInc;
    private String mIdJuez;
    private String mIdAsalto;
    private String mIdCombate;
    private String mIdCat;

    private String mDNIJuezUno;
    private String mDNIJuezDos;
    private String mDNIJuezTres;
    private String mDNIJuezCuatro;
    private String mDNIJuezCinco;

    private String mNumAsalto;
    private int mNumTotalAsaltos;

    // Contadores de Incidencias a tener en cuenta para mostrar en la UI y para terminar el combate en casos especiales.
    private int mContadorAmRojo;
    private int mContadorAmAzul;
    private int mContadorPenRojo;
    private int mContadorPenAzul;
    private int mContadorSalRojo;
    private int mContadorSalAzul;
    private int mContadorCuenRojo;
    private int mContadorCuenAzul;
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

    private String mIdRojo;
    private float mPesoRojo;
    //region Atributos UI ROJO
    private CircleImageView mFotoRojo;
    private TextView mNombreRojo;
    private TextView mPuntRojoText;
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

    private String mIdAzul;
    private float mPesoAzul;
    //region Atributos UI AZUL
    private CircleImageView mFotoAzul;
    private TextView mNombreAzul;
    private TextView mPuntAzulText;
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

        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");

        mModalidadCombate = "";
        mAcabarAsalto = false;
        mAcabarCombate = false;

        mListaPuntRojo = new ArrayList<>();
        mListaIncRojo = new ArrayList<>();
        mListaPuntAzul = new ArrayList<>();
        mListaIncAzul = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        try {
            mIdJuez = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Toast.makeText(this, "(MesaArbitraje) ID JUEZ --> " + mIdJuez, Toast.LENGTH_SHORT).show();
            getDniJuez(mIdJuez);
        } catch (NullPointerException e) {
            Toast.makeText(this, "(MesaArbitraje) Error al obtener el UID del juez a partir de FirebaseAuth...", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Excepcion " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

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
        final String idComb = extras.getString("idCombate");
        mIdCombate = idComb;

        final String idAsalto = extras.getString("idAsalto");
        mIdAsalto = idAsalto;

        mIdCamp = extras.getString("idCamp");
        //String idMod = extras.getString("idMod");
        mIdCat = extras.getString("idCat");
        //mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCat);   // Lista de Combates de esta Categoría.
        mCompetidoresDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");          // Lista de Compatidores de la BD.

        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(mIdCat).child(mIdCombate);


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
                mIdRojo = comp.getId();
                mPesoRojo = comp.getPeso();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mPuntRojoText = findViewById(R.id.puntosRojoText);
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
                mIdAzul = comp.getId();
                mPesoAzul = comp.getPeso();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mPuntAzulText = findViewById(R.id.puntosAzulText);
        //endregion

        //region Número de Combate y de Asalto
        cargarDatosCombate(mIdCat, idComb);
        cargarDatosAsalto(idComb, idAsalto);
        //endregion

        mAmRojoBtn = findViewById(R.id.am_rojo_btn);
        mPenRojoBtn = findViewById(R.id.pen_rojo_btn);
        mSalidasRojoBtn = findViewById(R.id.salidas_rojo_btn);
        mCuentasRojoBtn = findViewById(R.id.cuentas_rojo_btn);

        mAmAzulBtn = findViewById(R.id.am_azul_btn);
        mPenAzulBtn = findViewById(R.id.pen_azul_btn);
        mSalidasAzulBtn = findViewById(R.id.salidas_azul_btn);
        mCuentasAzulBtn = findViewById(R.id.cuentas_azul_btn);

        //region Botones Incidencias - onClick Events
        if(estado == Estado.COMBATE) { // Los botones para las Incidencias solo serán funcionales durante los combates.
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
                // Actualizar Puntuaciones Medias
                calcularPuntMediaCompetidor(mIdAsalto, mIdRojo, "Rojo", mCombateActual.getIdZonaCombate(), mIdCombate);
                calcularPuntMediaCompetidor(mIdAsalto, mIdAzul, "Azul", mCombateActual.getIdZonaCombate(), mIdCombate);
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
                    mCombateActual = comb;
                    // Número de Combate
                    try {
                        String num = "COMBATE \n" + comb.getNumCombate();
                        mNumCombateText.setText(num);
                        // Obtener la modalidad del combate para poder actuar en consecuencia. Existen diferencias entre modalidades. Ejemplo --> Salidas en SANDA y QINGDA para FIN ASALTO.
                        obtenerNombreModalidad(comb.getModalidad(), comb.getCampeonato()); // Obtener el nombre de la Modalidad a partir de su ID de Modalidad.
                        // Obtener el número de Asaltos del Combate.
                        if(comb.getListaAsaltos().size() > 0){
                            mNumTotalAsaltos = comb.getListaAsaltos().size();
                        } else {
                            mNumTotalAsaltos = 0;
                        }
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
    private void  obtenerNombreModalidad(String idMod, String idCamp){
        mModDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Modalidades").child(idCamp).child(idMod);
        Query consulta = mModDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el nombre de la Modalidad de este Combate...", Toast.LENGTH_SHORT).show();
                } else {
                    Modalidades mod = dataSnapshot.getValue(Modalidades.class);
                    mModalidadCombate = mod.getNombre();
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
                                            mContadorCuenAzul += contador;
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
        mArbisDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
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
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) getDniJuez -- DNI : " + mDniJuez, Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje} DNI no encontrado para ese ID", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MesaArbitrajeActivity.this, "Excepción " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Obtiene todos los DNIs de los jueces asignados a una zona de combate. El número que se pasa como parámetro determina el número de DNIs a recuperar (3 o 5)
    private void getDnisJueces(final int numJueces){
        switch (numJueces){
            case 1:
            case 2:
            case 4:{
                Toast.makeText(this, "(MesaArbitraje) (getDNIsJueces) El número de árbitros asignados a esta zona es incorrecto... ( " + numJueces + " )", Toast.LENGTH_SHORT).show();
                break;
            }
            case 3:
            case 5:{
                // Localizar a los árbitros de esta zona de combate. Consulta en mArbisDB usando el idCamp y el idZona para filtrar resultados.
                // mIdCamp ya contiene el valor del id del campeonato actual.
                String idZona = mZonaActual.getIdZona();
                final int numZona = mZonaActual.getNumZona();
                Query consulta = mArbisDB;
                final List<Arbitros> lista = new ArrayList<>();
                consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No existen Árbitros en la BD...", Toast.LENGTH_SHORT).show();
                        } else {
                            for(DataSnapshot arbiSnap : dataSnapshot.getChildren()){
                                Arbitros arbi = arbiSnap.getValue(Arbitros.class);
                                if((arbi.getIdCamp().equals(mIdCamp)) &&
                                        (arbi.getZonaCombate() == numZona)){ // Si los árbitros se corresponden con el campeonato y la zona de combate correctos...
                                    lista.add(arbi);
                                }
                            }
                            // Una vez obtenida la lista de árbitros que cumplen los requisitos asignaremos sus DNIs a las variables correspondientes.
                            // 3 árbitros --> 3 DNIs
                            if(numJueces == 3){
                                mDNIJuezUno = lista.get(0).getDni();
                                mDNIJuezDos = lista.get(1).getDni();
                                mDNIJuezTres = lista.get(2).getDni();
                            } else if(numJueces == 5) { // 5 árbitros --> 5 DNIs
                                mDNIJuezUno = lista.get(0).getDni();
                                mDNIJuezDos = lista.get(1).getDni();
                                mDNIJuezTres = lista.get(2).getDni();
                                mDNIJuezCuatro = lista.get(3).getDni();
                                mDNIJuezCinco = lista.get(4).getDni();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            }
        }
    }

    //endregion

    //region Guardar Datos en la BD

    public void insertarIncidenciaBD(final Incidencias incidencia){
        mIncDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Incidencias");
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

    // Método para añadir una Incidencia a la BD, obtener el número de Incidencias de ese tipo y actualizar la UI. TODO
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
                            //TODO: Añadir Comprobación Condiciones FIN ASALTO y FIN COMBATE
                            //comprobarFinAsaltoCombate();
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

    // Comprobar las Condiciones de FIN de ASALTO y de FIN de COMBATE
    // En este método comprobaré las condiciones especiales que pueden marcar el fin de un Asalto y de un Combate
    // Condiciones Normales --> 1. El crono llega a CERO --> FIN ASALTO y/o COMBATE (depende del asalto actual)
    //                          2. Número necesario de asaltos ganados --> FIN COMBATE
    // Condiciones Especiales -->
    //      1. KO (Botón UI) --> FIN COMBATE
    //      2. TKO (Botón UI) --> FIN COMBATE
    //      3. Final por Decisión Arbitral , Lesión, Rendición o Decisión Médica --> FIN COMBATE
    //      4. Relacionadas con Incidencias (Amonestaciones, Penalizaciones, Salidas o Cuentas)
    //         Son específicas de la modalidad, por eso se evaluará la variable mModalidadCombate.
                // SANDA y KC -->
                //      2 Salidas o 2 cuentas --> FIN ASALTO
                //      3 cuentas o 3 penalizaciones --> FIN COMBATE
                // QINGDA -->
                //      3 Salidas --> FIN ASALTO
                //      3 puntos de descuento (Am o Pen) --> FIN ASALTO
                //      6 puntos de descuento (Am o Pen) --> FIN COMBATE
                // SJ -->
                //      8 puntos de ventaja --> FIN COMBATE
    // Se evaluarán los contadores de cada tipo de incidencia para cada asalto y los contadores globales para el combate.
    // Si se cumplen las condiciones necesarias se marcará el Fin del Asalto (mAcabarAsalto --> TRUE) o del Combate (mAcabarCombate --> TRUE)
    private void comprobarFinAsaltoCombate(@NonNull String numAsalto){
        switch (numAsalto){
            case "1":{
                // ¿CRONO ha llegado a CERO?
                if(mTimeLeftInMillis == 0.0){
                    mAcabarAsalto = true;
                    determinarGanadorAsalto(mIdAsalto, mIdRojo, mIdAzul, mIdCombate); // TODO
                    //} else if() { // ROJO ha ganado suficientes asaltos --> FIN COMBATE TODO
                    //} else if() { // AZUL ha ganado suficientes asaltos --> FIN COMBATE TODO
                    // } else if() { // Se ha pulsado el botón de KO TODO
                    // } else if() { // Se ha pulsado el botón de TKO TODO
                    // } else if() { // Decisión Arbitral o Médica --> Se ha pulsado el botón de TERMINAR ASALTO/ TERMINAR COMBATE TODO
                } else if((mModalidadCombate.equals(SANDA))||(mModalidadCombate.equals(KUNGFUCOMBAT))){ // Condiciones especiales para SD y KC
                    // ROJO PIERDE
                    if(mContSalRojoR1 == 2) { // Salidas
                        mAcabarAsalto = true;
                        determinarGanadorAsalto(mIdAsalto, mIdRojo, mIdAzul, mIdCombate); // TODO
                    } else if(mContCuenRojoR1 == 2) { // Cuentas
                        mAcabarAsalto = true;
                        // Actualizar el número de asaltos ganados del AZUL.
                        actualizarNumAsaltosGanados("Azul");
                    } else if(mContadorCuenRojo == 3) {
                        mAcabarCombate = true;
                        // Actualizar el número de asaltos ganados del AZUL.
                        actualizarNumAsaltosGanados("Azul");
                    } else if(mContadorPenRojo == 3) {
                        mAcabarCombate = true;
                        // Actualizar el número de asaltos ganados del AZUL.
                        actualizarNumAsaltosGanados("Azul");
                    } else if(mContSalAzulR1 == 2) { // AZUL PIERDE
                        mAcabarAsalto = true;
                        // Actualizar el número de asaltos ganados del ROJO.
                        actualizarNumAsaltosGanados("Rojo");
                    } else if(mContCuenAzulR1 == 2) {
                        mAcabarAsalto = true;
                        // Actualizar el número de asaltos ganados del ROJO.
                        actualizarNumAsaltosGanados("Rojo");
                    } else if(mContadorCuenAzul == 3) {
                        mAcabarCombate = true;
                        // Actualizar el número de asaltos ganados del ROJO.
                        actualizarNumAsaltosGanados("Rojo");
                    } else if(mContadorPenAzul == 3){
                        mAcabarCombate = true;
                        // Actualizar el número de asaltos ganados del ROJO.
                        actualizarNumAsaltosGanados("Rojo");
                    }
                } else if(mModalidadCombate.equals(QINGDA)){ // Condiciones especiales para QD

                } else if(mModalidadCombate.equals(SHUAIJIAO)){ // Condiciones especiales para SJ

                }
                break;
            }
            case "2":{
                break;
            }
            case "3":{
                break;
            }
        }

    }

    private void actualizarNumAsaltosGanados(@NonNull String lado){
        switch (lado){
            case "Rojo":{
                mNumAsaltosGanadosRojo++;
                break;
            }
            case "Azul":{
                mNumAsaltosGanadosAzul++;
                break;
            }
            default:{
                break;
            }
        }
    }

    private void obtenerPuntuacion(String idAsalto, @NonNull String idCompetidor){
        mPuntDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Puntuaciones").child(idAsalto);
        Query consulta = mPuntDB.orderByChild("idCompetidor");
        if(idCompetidor.equals(mIdRojo)){
            consulta.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mListaPuntRojo.clear();
                    if(!dataSnapshot.exists()){
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener las puntuaciones del Competidor Rojo para este Asalto", Toast.LENGTH_SHORT).show();
                        mPuntRojo = 0;
                    } else {
                        for(DataSnapshot puntSnap : dataSnapshot.getChildren()){
                            Puntuaciones punt = puntSnap.getValue(Puntuaciones.class);
                            try {
                                String idComp = punt.getIdCompetidor();
                                if(idComp.equals(mIdRojo)){
                                    mListaPuntRojo.add(punt);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el ID del Competidor ROJO (listaPuntuaciones)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        if(mListaPuntRojo.size() > 0){
                            for(int i = 0; i < mListaPuntRojo.size(); i++){
                                mPuntRojo += mListaPuntRojo.get(i).getValor();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if(idCompetidor.equals(mIdAzul)){
            consulta.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mListaPuntAzul.clear();
                    if(!dataSnapshot.exists()){
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener las puntuaciones del Competidor Azul para este Asalto", Toast.LENGTH_SHORT).show();
                        mPuntAzul = 0;
                    } else {
                        for(DataSnapshot puntSnap : dataSnapshot.getChildren()){
                            Puntuaciones punt = puntSnap.getValue(Puntuaciones.class);
                            try {
                                String idComp = punt.getIdCompetidor();
                                if(idComp.equals(mIdAzul)){
                                    mListaPuntAzul.add(punt);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el ID del Competidor Azul (listaPuntuaciones)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        if(mListaPuntAzul.size() > 0){
                            for(int i = 0; i < mListaPuntAzul.size(); i++){
                                mPuntAzul += mListaPuntAzul.get(i).getValor();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void obtenerPuntuacionFinalAsalto(String idAsalto, @NonNull final String idCompetidor){
        //region PUNTUACIONES
        mPuntDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Puntuaciones").child(idAsalto);
        Query consulta = mPuntDB;
        if(idCompetidor.equals(mIdRojo)){ // ROJO
            // Obtener la suma de dichas puntuaciones
            // Recorrer la lista de Incidencias del Asalto y quedarse con las que corresponden al Competidor Rojo.
            consulta.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mListaPuntRojo.clear();
                    if(!dataSnapshot.exists()){
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No existen Puntuaciones para el Competidor ROJO", Toast.LENGTH_SHORT).show();
                        mPuntFinAsaltoRojo = 0; // Si no existen Puntuaciones que contabilizar el valor final para este asalto es CERO.
                    } else {
                        for(DataSnapshot puntSnap: dataSnapshot.getChildren()){
                            Puntuaciones punt = puntSnap.getValue(Puntuaciones.class);
                            try {
                                String id = punt.getIdCompetidor();
                                if(id.equals(mIdRojo)){
                                    mListaPuntRojo.add(punt);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el ID del Competidor ROJO (listaPuntuaciones)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        // Evaluar la lista de puntuaciones del ROJO
                        if(mListaPuntRojo.size() > 0 ){
                            for(int i = 0; i < mListaPuntRojo.size(); i++){
                                mSumaPuntRojo += mListaPuntRojo.get(i).getValor();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if(idCompetidor.equals(mIdAzul)){ // AZUL
            consulta.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mListaPuntAzul.clear();
                    if(!dataSnapshot.exists()){
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No existen Puntuaciones para el Competidor AZUL", Toast.LENGTH_SHORT).show();
                        mPuntFinAsaltoAzul = 0; // Si no existen Puntuaciones que contabilizar el valor final para este asalto es CERO.
                    } else {
                        for(DataSnapshot puntSnap: dataSnapshot.getChildren()){
                            Puntuaciones punt = puntSnap.getValue(Puntuaciones.class);
                            try {
                                String id = punt.getIdCompetidor();
                                if(id.equals(mIdAzul)){
                                    mListaPuntAzul.add(punt);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el ID del Competidor AZUL (listaPuntuaciones)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        // Evaluar la lista de puntuaciones del AZUL
                        if(mListaPuntAzul.size() > 0 ){
                            for(int i = 0; i < mListaPuntAzul.size(); i++){
                                mSumaPuntAzul += mListaPuntAzul.get(i).getValor();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //endregion

        //region INCIDENCIAS
        mIncDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Incidencias").child(idAsalto);
        Query consulta2 = mIncDB;
        if(idCompetidor.equals(mIdRojo)){ // ROJO
            // Obtener su lista de Incidencias
            // Conseguir la suma de las incidencias
            consulta2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbirtaje) No existen Incidencias en este Asalto para el Competidor ROJO", Toast.LENGTH_SHORT).show();
                        mSumaIncRojo = 0;
                    } else {
                        for(DataSnapshot incSnap: dataSnapshot.getChildren()){
                            Incidencias inc = incSnap.getValue(Incidencias.class);
                            try {
                                String id = inc.getIdComp();
                                if(id.equals(idCompetidor)){
                                    mListaIncRojo.add(inc);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el ID del Competidor ROJO (listaIncidencias)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        // Evaluar lista de Incidencias
                        if(mListaIncRojo.size() > 0){
                            for(int i = 0; i < mListaIncRojo.size(); i++){
                                mSumaIncRojo += mListaIncRojo.get(i).getValor();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if(idCompetidor.equals(mIdAzul)){ // AZUL
            consulta2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbirtaje) No existen Incidencias en este Asalto para el Competidor AZUL", Toast.LENGTH_SHORT).show();
                        mSumaIncAzul = 0;
                    } else {
                        for(DataSnapshot incSnap: dataSnapshot.getChildren()){
                            Incidencias inc = incSnap.getValue(Incidencias.class);
                            try {
                                String id = inc.getIdComp();
                                if(id.equals(idCompetidor)){
                                    mListaIncAzul.add(inc);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al obtener el ID del Competidor AZUL (listaIncidencias)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        // Evaluar lista de Incidencias
                        if(mListaIncAzul.size() > 0){
                            for(int i = 0; i < mListaIncAzul.size(); i++){
                                mSumaIncAzul += mListaIncAzul.get(i).getValor();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //endregion

        // Calcular Puntuaciones - Incidencias
        if(idCompetidor.equals(mIdRojo)){
            mPuntFinAsaltoRojo = mSumaPuntRojo - mSumaIncRojo;
            Toast.makeText(this, "(MesaArbitraje) Puntuacion Rojo : " + mPuntFinAsaltoRojo + "( + " + mSumaPuntRojo + " - " + mSumaIncRojo + " )", Toast.LENGTH_SHORT).show();
        } else if(idCompetidor.equals(mIdAzul)){
            mPuntFinAsaltoAzul = mSumaPuntAzul - mSumaIncAzul;
            Toast.makeText(this, "(MesaArbitraje) Puntuacion Azul : " + mPuntFinAsaltoAzul + "( + " + mSumaPuntAzul + " - " + mSumaIncAzul + " )", Toast.LENGTH_SHORT).show();
        }
    }

    // La puntuación a mostrar en la app de Mesa será la MEDIA de las puntuaciones de todos los jueces de Silla.
    // Además ha de mostrarse en la lista de jueces la puntuación individual que lleva cada uno de ellos.
    // El int numJuez indica el número (de 1 a 5) que tiene el juez de silla del que vamos a obtener la puntuación.
    // En SANDA debe haber de 3 a 5 jueces de silla.
    private void obtenerPuntuacionCadaJuez(String idAsalto, final String dniJuez, final String idCompetidor, final String lado, final int numJuez){
        mPuntDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Puntuaciones").child(idAsalto);
        Query consulta = mPuntDB.orderByChild(dniJuez); // Obtener las puntuaciones del Asalto ordenadas por el IdJuez
        consulta.addValueEventListener(new ValueEventListener() { // addValueEventListener porque deberá actualizarse al introducir nuevas puntuaciones o incidencias durante el asalto.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener las puntuaciones de cada uno de los competidores y para el juez deseado
                switch(lado){
                    case "Rojo":{
                        mListaPuntRojo.clear();
                        for(DataSnapshot puntSnap: dataSnapshot.getChildren()){
                            Puntuaciones punt = puntSnap.getValue(Puntuaciones.class);
                            try {
                                String idComp = punt.getIdCompetidor();
                                String dni = punt.getDniJuez();
                                if((idComp.equals(idCompetidor))&&
                                        (dni.equals(dniJuez))){ // Si la puntuación corresponde al Juez y al Competidor que deseamos
                                    mListaPuntRojo.add(punt);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) (obtenerPuntuacionCadaJuez) Error al obtener el ID del Competidor Rojo.", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        // Una vez conseguida la lista puntuaciones para el juez y el competidor deseados deberemos calcular la suma de dichas puntuaciones.
                        if(mListaPuntRojo.size() > 0){
                            switch (numJuez){ // Dependiendo del numJuez se actualizará la variable que le corresponde.
                                case 1:{
                                    for (int i = 0; i < mListaPuntRojo.size(); i++){
                                        mSumaPuntRojoSillaUno += mListaPuntRojo.get(i).getValor();
                                    }
                                    break;
                                }
                                case 2:{
                                    for (int i = 0; i < mListaPuntRojo.size(); i++){
                                        mSumaPuntRojoSillaDos += mListaPuntRojo.get(i).getValor();
                                    }
                                    break;
                                }
                                case 3:{
                                    for (int i = 0; i < mListaPuntRojo.size(); i++){
                                        mSumaPuntRojoSillaTres += mListaPuntRojo.get(i).getValor();
                                    }
                                    break;
                                }
                                case 4:{
                                    for (int i = 0; i < mListaPuntRojo.size(); i++){
                                        mSumaPuntRojoSillaCuatro += mListaPuntRojo.get(i).getValor();
                                    }
                                    break;
                                }
                                case 5:{
                                    for (int i = 0; i < mListaPuntRojo.size(); i++){
                                        mSumaPuntRojoSillaCinco += mListaPuntRojo.get(i).getValor();
                                    }
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) (obtenerPuntuacionCadaJuez) Error al calcular la suma de puntuaciones ROJO (listaPunt Vacía)", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case "Azul":{
                        mListaPuntAzul.clear();
                        for(DataSnapshot puntSnap: dataSnapshot.getChildren()){
                            Puntuaciones punt = puntSnap.getValue(Puntuaciones.class);
                            try {
                                String idComp = punt.getIdCompetidor();
                                String dni = punt.getDniJuez();
                                if((idComp.equals(idCompetidor))&&
                                        (dni.equals(dniJuez))){ // Si la puntuación corresponde al Juez y al Competidor que deseamos
                                    mListaPuntAzul.add(punt);
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) (obtenerPuntuacionCadaJuez) Error al obtener el ID del Competidor Azul.", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        // Una vez conseguida la lista puntuaciones para el juez y el competidor deseados deberemos calcular la suma de dichas puntuaciones.
                        if(mListaPuntAzul.size() > 0){
                            switch (numJuez){ // Dependiendo del numJuez se actualizará la variable que le corresponde.
                                case 1:{
                                    for (int i = 0; i < mListaPuntAzul.size(); i++){
                                        mSumaPuntAzulSillaUno += mListaPuntAzul.get(i).getValor();
                                    }
                                    break;
                                }
                                case 2:{
                                    for (int i = 0; i < mListaPuntAzul.size(); i++){
                                        mSumaPuntAzulSillaDos += mListaPuntAzul.get(i).getValor();
                                    }
                                    break;
                                }
                                case 3:{
                                    for (int i = 0; i < mListaPuntAzul.size(); i++){
                                        mSumaPuntAzulSillaTres += mListaPuntAzul.get(i).getValor();
                                    }
                                    break;
                                }
                                case 4:{
                                    for (int i = 0; i < mListaPuntAzul.size(); i++){
                                        mSumaPuntAzulSillaCuatro += mListaPuntAzul.get(i).getValor();
                                    }
                                    break;
                                }
                                case 5:{
                                    for (int i = 0; i < mListaPuntAzul.size(); i++){
                                        mSumaPuntAzulSillaCinco += mListaPuntAzul.get(i).getValor();
                                    }
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) (obtenerPuntuacionCadaJuez) Error al calcular la suma de puntuaciones AZUL (listaPunt Vacía)", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Una vez obtenida la puntuación que otorga cada juez por separado se calculará la media. Dicha media se mostrará en la intefaz de la app de Mesa.
    private void calcularPuntMediaCompetidor(String idAsalto, String idCompetidor, String lado, String idZonaCombate, String idCombate){
        // Obtener las puntuaciones para cada uno de los jueces de la zona de Combate
        // Para ello deberemos obtener la información de la Zona de Combate
        obtenerDatosZona(idZonaCombate);
        try {
            List<DatosExtraZonasCombate> lista = new ArrayList();
            lista = mZonaActual.getListaDatosExtraCombates();
            //Toast.makeText(this, "(MesaArbitraje) Tamaño de la Lista de Datos Extra de la Zona --> " + lista.size(), Toast.LENGTH_SHORT).show();
            int numArbis = 0;
            if(lista.size() > 0) {
                for (int i = 0; i < lista.size(); i++){
                    if(lista.get(i).getIdCombate().equals(idCombate)){ // Si estamos leyendo los datos extra que corresponden al combate deseado leeremos el número de arbis que tiene asignados.
                        numArbis = lista.get(i).getNumArbisAsignados();
                    }
                }
            } else {
                Toast.makeText(this, "(MesaArbitraje) La zona indicada no tiene datos extras en la BD.", Toast.LENGTH_SHORT).show();
            }
            switch (numArbis){
                case 0:{
                    Toast.makeText(this, "(MesaArbitraje) El combate actual no tiene árbitros asignados", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 1:
                case 2:{
                    Toast.makeText(this, "(MesaArbitraje) El número de árbitros asignados al combate indicado es inferior al mínimo establecido. Asigne más árbitros a dicho combate.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 3:{ // 3 jueces silla
                    switch(lado){
                        case "Rojo":{
                            // Deberemos comprobar los valores de las variables mSumaPuntRojoSillaUno, mSumaPuntRojoSillaDos y mSumaPuntRojoSillaTres para obtener la media para el ROJO
                            int total;
                            getDnisJueces(3);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezUno, idCompetidor, "Rojo", 1);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezDos, idCompetidor, "Rojo", 2);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezTres, idCompetidor, "Rojo", 3);
                            total = mSumaPuntRojoSillaUno + mSumaPuntRojoSillaDos + mSumaPuntRojoSillaTres;
                            mMediaPuntRojo = total / numArbis;
                            // Mostrar datos en UI
                            String valor = String.valueOf(mMediaPuntRojo);
                            mPuntRojoText.setText(valor);
                            break;
                        }
                        case "Azul":{
                            // Deberemos comprobar los valores de las variables mSumaPuntAzulSillaUno, mSumaPuntAzulSillaDos y mSumaPuntAzulSillaTres para obtener la media para el AZUL
                            int total;
                            getDnisJueces(3);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezUno, idCompetidor, "Azul", 1);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezDos, idCompetidor, "Azul", 2);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezTres, idCompetidor, "Azul", 3);
                            total = mSumaPuntAzulSillaUno + mSumaPuntAzulSillaDos + mSumaPuntAzulSillaTres;
                            mMediaPuntAzul = total / numArbis;
                            // Mostrar datos en la UI
                            String valor = String.valueOf(mMediaPuntAzul);
                            mPuntAzulText.setText(valor);
                            break;
                        }
                    }
                    break;
                }
                case 4:{ // 4 jueces silla
                    Toast.makeText(this, "(MesaArbitraje) El número de árbitros asignados al combate indicado es 4. El número ha de ser 3 o 5.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 5:{ // 5 jueces silla
                    switch (lado){
                        case "Rojo":{
                            getDnisJueces(5);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezUno, idCompetidor, "Rojo", 1);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezDos, idCompetidor, "Rojo", 2);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezTres, idCompetidor, "Rojo", 3);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezCuatro, idCompetidor, "Rojo", 4);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezCinco, idCompetidor, "Rojo", 5);
                            int total = mSumaPuntRojoSillaUno + mSumaPuntRojoSillaDos + mSumaPuntRojoSillaDos + mSumaPuntRojoSillaTres + mSumaPuntRojoSillaCuatro + mSumaPuntRojoSillaCinco;
                            mPuntRojo = total / numArbis;
                            break;
                        }
                        case "Azul":{
                            getDnisJueces(5);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezUno, idCompetidor, "Azul", 1);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezDos, idCompetidor, "Azul", 2);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezTres, idCompetidor, "Azul", 3);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezCuatro, idCompetidor, "Azul", 4);
                            obtenerPuntuacionCadaJuez(idAsalto, mDNIJuezCinco, idCompetidor, "Azul", 5);
                            int total = mSumaPuntAzulSillaUno + mSumaPuntAzulSillaDos + mSumaPuntAzulSillaDos + mSumaPuntAzulSillaTres + mSumaPuntAzulSillaCuatro + mSumaPuntAzulSillaCinco;
                            mPuntAzul = total / numArbis;
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "(MesaArbitraje) Error al obtener la lista de datos extra de la zona", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "(MesaArbitraje) Excepción: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    // Para poder obtener el número de árbitros asignados a la Zona de Combate
    private void obtenerDatosZona(final String idZona){
        mZonaDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate").child(mIdCamp).child(idZona);
        Query consulta = mZonaDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al localizar la Zona de Combate " + idZona, Toast.LENGTH_SHORT).show();
                } else {
                    mZonaActual = dataSnapshot.getValue(ZonasCombate.class);
                    //Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) La Zona Actual tiene el ID " + mZonaActual.getIdZona(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Para determinar el ganador de un asalto vamos a usar estos valores
    // 0 --> EMPATE
    // 1 --> GANA ROJO
    // 2 --> GANA AZUL
    private void determinarGanadorAsalto(final String idAsalto, String idRojo, String idAzul, final String idCombate){

        // Actualizar el número de asalto ganados del ganador de este asalto.
        obtenerPuntuacionFinalAsalto(idAsalto, idRojo);
        obtenerPuntuacionFinalAsalto(idAsalto, idAzul);
        // Comparar puntuaciones para decidir ganador al haber consumido el tiempo del asalto sin que se produzcan condiciones especiales
        if(mPuntFinAsaltoRojo > mPuntFinAsaltoAzul) { // Gana Rojo
            resAsalto = 1;
        } else if(mPuntFinAsaltoRojo < mPuntFinAsaltoAzul) { // Gana Azul
            resAsalto = 2;
        } else { // Empate
            // Comprobar num Penalizaciones
            if(mContadorPenRojo > mContadorPenAzul) {
                // Gana Azul
                resAsalto = 2;
            } else if(mContadorPenRojo < mContadorPenAzul) {
                // Gana Rojo
                resAsalto = 1;
            } else { // Igualados en Penalizaciones
                // Comprobar num Amonestaciones
                if(mContadorAmRojo > mContadorAmAzul){
                    // Gana Azul
                    resAsalto = 2;
                } else if(mContadorAmRojo < mContadorAmAzul){
                    // Gana Rojo
                    resAsalto = 1;
                } else { // Igualados en Amonestaciones
                    // Comprobar Peso de los Competidores
                    if(mPesoRojo > mPesoAzul){
                        // Gana Azul
                        resAsalto = 2;
                    } else if(mPesoRojo < mPesoAzul){
                        // Gana Rojo
                        resAsalto = 1;
                    } else { // Permanece el empate
                        resAsalto = 0;
                    }
                }
            }
        }

        if(resAsalto == 1){
            actualizarNumAsaltosGanados("Rojo");
        } else if(resAsalto == 2){
            actualizarNumAsaltosGanados("Azul");
        }

        // Consulta para localizar el asalto indicado por idAsalto
        Query consulta = mAsaltoDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) No se encuentra el Asalto para poder actualizar sus datos.", Toast.LENGTH_SHORT).show();
                } else {
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);                  // Obtener el asalto de la BD
                    actualizarAsalto(asalto, resAsalto, motivo);                            // Modificar los datos
                    Map<String, Object> asaltoMap = asalto.toMap();                         // Convertir el Asalto en un Map<String, Object>

                    Map<String, Object> updates = new HashMap<>();                          // Crear Map para realizar la actualización
                    updates.put("/Asaltos/" + idCombate + "/" +  idAsalto, asaltoMap);   // Especificar ruta para la actualización
                    mRootDB.updateChildren(updates);                                        // Realizar la actualización de los datos del Asalto.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // Una vez determinado el resultado del asalto deberemos actualizar sus datos en la BD.
    // Datos a actualizar
    //      Ganador
    //      Estado
    //      Puntuación Rojo
    //      Puntuación Azul
    //      Motivo
    //      Duración
    //      Descripción

    public void actualizarAsalto(Asaltos asalto, int resAsalto, String motivo){
        switch(resAsalto){
            case 0:{
                asalto.setGanador("Empate");
                break;
            }
            case 1:{
                asalto.setGanador("Rojo");
                break;
            }
            case 2:{
                asalto.setGanador("Azul");
                break;
            }
        }
        Asaltos.EstadoAsalto estado = Asaltos.EstadoAsalto.Finalizado;
        asalto.setEstado(estado);
        asalto.setPuntuacionRojo(mPuntFinAsaltoRojo);
        asalto.setPuntuacionAzul(mPuntFinAsaltoAzul);
        asalto.setMotivo(motivo);
        String duracion = String.valueOf(START_TIME_IN_MILLIS_3 - mTimeLeftInMillis);
        asalto.setDuracion(duracion);
        asalto.setDescripcion(motivo); // A falta de una mejor descripción, que se podrá editar a posteriori, se escribe el motivo del fin del asalto.
    }

    // Para determinar el ganador de un combate vamos a usar estos valores
    // 1 --> GANA ROJO
    // 2 --> GANA AZUL

    // En condiciones normales, para determinar el ganador de un combate deberemos comparar el número de asaltos ganados por ambos competidores. --> DiferenciaAsaltos

    // Si el combate no ha consumido todos sus asaltos puede ser por las siguientes razones
    // KO
    // TKO
    // Decisión Arbitral (Superioridad Manifiesta, Rendición del oponente)  --> DecArbitral
    // Decisión Médica (Lesión del oponente)                                --> DecMedica
    // SD y KC --> 3 penalizaciones en total en el combate                  --> NumPenalizaciones
    //         --> 3 cuentas en total en el combate                         --> NumCuentas
    // QD      --> 3 puntos de descuento en total (Combate a un asalto)     --> NumPtosDescuento
    //         --> 6 puntos de descuento en total (Combate a 3 asaltos)     --> ""
    // SJ      --> 8 puntos de diferencia                                   --> DiferenciaPuntos

    public void determinarGanadorCombate(final String idCombate, String idRojo, String idAzul, String motivo){
        int resCombate = 0;

        // Comprobar número de Asaltos Ganados
        if(mNumAsaltosGanadosRojo > mNumAsaltosGanadosAzul){
            // GANA ROJO
            resCombate = 1;

        } else if(mNumAsaltosGanadosRojo < mNumAsaltosGanadosAzul){
            // GANA AZUL
            resCombate = 2;
        } else if(motivo.equals("KO ROJO")){
            resCombate = 1;
        } else if(motivo.equals("KO AZUL")){
            resCombate = 2;
        } else if(motivo.equals("TKO ROJO")){
            resCombate = 1;
        } else if(motivo.equals("TKO AZUL")){
            resCombate = 2;
        } else if(motivo.equals("DecArbitral ROJO")){
            resCombate = 1;
        } else if(motivo.equals("DecArbitral AZUL")){
            resCombate = 2;
        } else if(motivo.equals("DecMedica ROJO")){
            resCombate = 1;
        } else if(motivo.equals("DecMedica AZUL")){
            resCombate = 2;
        }

        // Condiciones específicas de las distintas Modalidades
        switch (mModalidadCombate){
            case "SD":
            case "KC":{
                if((mContadorPenRojo >= 3) || (mContadorCuenRojo >= 3)){ // Penalizaciones o cuentas = 3
                    resCombate = 2;
                    if(mContadorPenRojo >= 3) motivo = "3PenalizacionesRojo";
                    if(mContadorCuenRojo >= 3) motivo = "3CuentasRojo";
                } else if((mContadorPenAzul >= 3) || (mContadorCuenAzul >= 3)){
                    resCombate = 1;
                    if(mContadorPenAzul >= 3) motivo = "3PenalizacionesAzul";
                    if(mContadorCuenAzul >= 3) motivo = "3CuentasAzul";
                }
                break;
            }
            case "QD":{
                if(mNumTotalAsaltos == 1){ // 3 puntos de descuento en combates a 1 asalto
                    if((((mContadorPenRojo == 1) && (mContadorAmRojo == 1)))
                            || (mContadorAmRojo == 3)){
                        resCombate = 2;
                        motivo = "3PuntosDescuentoRojo";
                    } else if ((((mContadorPenAzul == 1) && (mContadorAmAzul == 1)))
                            || (mContadorAmAzul == 3)){
                        resCombate = 1;
                        motivo = "3PuntosDescuentoAzul";
                    }
                } else if(mNumTotalAsaltos == 3){ // 6 puntos de descuento en combates a 3 asaltos
                    if((((mContadorPenRojo == 2) && (mContadorAmRojo == 2)))
                            || (mContadorPenRojo == 3)
                            || (mContadorAmRojo == 6)){
                        resCombate = 2;
                        motivo = "6PuntosDescuentoRojo";
                    } else if((((mContadorPenAzul == 2) && (mContadorAmAzul == 2)))
                            || (mContadorPenAzul == 3)
                            || (mContadorAmAzul == 6)){
                        resCombate = 1;
                        motivo = "6PuntosDescuentosAzul";
                    }
                }
                break;
            }
            case "SJ":{
                obtenerPuntuacion(mIdAsalto, mIdRojo);
                obtenerPuntuacion(mIdAsalto, mIdAzul);
                if(mPuntRojo - mPuntAzul >= 8){
                    resCombate = 1;
                    motivo = "DifPuntosRojo";
                } else if(mPuntAzul - mPuntRojo >= 8){
                    resCombate = 2;
                    motivo = "DifPuntosAzul";
                }
                break;
            }
        }
        // Una vez evaluado el Resultado del Combate se actualizan sus datos en la BD
        actualizarCombate(mCombateActual, resCombate, motivo);
    }

    // Si se ha terminado el combate deberemos actualiza los datos correspondientes
    // Ganador
    // Perdedor
    // Motivo
    // EstadoCombate --> Finalizado

    // resCombate --> 1 --> GANA ROJO
    //            --> 2 --> GANA AZUL
    public void actualizarCombate(final Combates combate, final int resCombate, final String motivo){
        Query consulta = mCombateDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Error al localizar el Combate a actualizar", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Ruta del Combate en la BD --> " + dataSnapshot.getRef() , Toast.LENGTH_SHORT).show();
                } else {
                    switch(resCombate){
                        case 1:{
                            // Modificar datos del Combate
                            combate.setGanador(mIdRojo);
                            combate.setPerdedor(mIdAzul);
                            combate.setMotivo(motivo);
                            Combates.EstadoCombate estado = Combates.EstadoCombate.Finalizado;
                            combate.setEstadoCombate(estado);
                            // Combate a Map<String, Object>
                            Map<String, Object> combateMap = combate.toMap();
                            // Actualizar Combate en la BD
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("/Combates/" + combate.getId(), combateMap);
                            mRootDB.updateChildren(updates);
                            Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Se ha actualizado el combate (ID --> "+ combate.getId() + " ) en la BD", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 2:{
                            // Modificar datos del Combate
                            combate.setGanador(mIdAzul);
                            combate.setPerdedor(mIdRojo);
                            combate.setMotivo(motivo);
                            Combates.EstadoCombate estado = Combates.EstadoCombate.Finalizado;
                            combate.setEstadoCombate(estado);
                            // Combate a Map<String, Object>
                            Map<String, Object> combateMap = combate.toMap();
                            // Actualizar Combate en la BD
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("/Combates/" + combate.getId(), combateMap);
                            mRootDB.updateChildren(updates);
                            Toast.makeText(MesaArbitrajeActivity.this, "(MesaArbitraje) Se ha actualizado el combate (ID --> "+ combate.getId() + " ) en la BD", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


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
