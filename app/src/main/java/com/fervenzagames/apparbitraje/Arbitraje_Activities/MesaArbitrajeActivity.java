package com.fervenzagames.apparbitraje.Arbitraje_Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.CampeonatosActivity;
import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.User_Activities.SettingsActivity;
import com.fervenzagames.apparbitraje.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

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

    //region Atributos UI ROJO
    private ImageView mFotoRojo;
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
    private ImageView mFotoAzul;
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

        mToolbar = (Toolbar) findViewById(R.id.mesa_arbitraje_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SD -85 M ABS (Campeonato X)");

        mCrono = (TextView) findViewById(R.id.crono);
        mStartPauseBtn = (Button) findViewById(R.id.pausa_btn);
        mStartPauseBtn.setText("Iniciar");
        mResetBtn = (Button) findViewById(R.id.reset_btn);

        mFinAsalto = (Button) findViewById(R.id.terminar_asalto);
        mFinCombate = (Button) findViewById(R.id.terminar_combate);

        estado = Estado.DESCANSO_ENTRE_COMBATES;
        mResetBtn.setVisibility(View.INVISIBLE);

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

        //region UI ROJO
        //endregion

        //region UI AZUL
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

    //endregion

    //region Guardar Datos en la BD

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
