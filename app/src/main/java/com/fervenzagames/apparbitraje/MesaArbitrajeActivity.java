package com.fervenzagames.apparbitraje;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Locale;

public class MesaArbitrajeActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 60000;    // Un minuto
    private static final long START_TIME_IN_MILLIS_2 = 120000; // Dos minutos
    private static final long START_TIME_IN_MILLIS_3 = 180000; // Tres minutos

    private Toolbar mToolbar;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa_arbitraje);

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

        // Reproducir Sonido Campana
        final MediaPlayer player = MediaPlayer.create(MesaArbitrajeActivity.this, R.raw.bell);

        mStartPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning) {
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
    }

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
/*                switch(estado){
                    case COMBATE:{
                        // Si un competidor ha ganado el combate, es decir, FIN COMBATE.
                        break;
                    }
                    case DESCANSO_ENTRE_ASALTOS:{
                        // Si debemos preparar el crono para el siguiente asalto.
                        break;
                    }
                    case DESCANSO_ENTRE_COMBATES:{
                        // Si debemos preparar el crono para comenzar un nuevo combate, es decir, PRIMER asalto.
                        break;
                    }
                }*/
                estado = Estado.DESCANSO_ENTRE_ASALTOS;
                mTimerRunning = false;
                mStartPauseBtn.setText("Iniciar");
                // reiniciarCrono(START_TIME_IN_MILLIS);
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
}
