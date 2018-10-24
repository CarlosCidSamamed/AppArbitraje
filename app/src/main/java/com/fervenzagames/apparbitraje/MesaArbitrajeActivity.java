package com.fervenzagames.apparbitraje;

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

    private Button mStartPauseBtn;
    private Button mResetBtn;



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

        mStartPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning) {
                    pausarCrono();
                } else {
                    iniciarCrono();
                }
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarCrono();
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
                mTimerRunning = false;
                mStartPauseBtn.setText("Iniciar");
            }
        }.start(); // Iniciar el CountDownTimer que hemos creado.

        mTimerRunning = true;
        mStartPauseBtn.setText("Pausar");
    }

    public void pausarCrono (){ // PAUSE
        mCountdownTimer.cancel();
        mTimerRunning = false;
        mStartPauseBtn.setText("Reanudar");
    }

    public void reiniciarCrono (){ // RESET
        mCountdownTimer.cancel();
        mTimerRunning = false;
        mTimeLeftInMillis = START_TIME_IN_MILLIS_2;
        mStartPauseBtn.setText("Iniciar");
        actualizarCrono();
    }

    public void actualizarCrono() { // Actualizar el TextView con el crono
        int minutos = (int) mTimeLeftInMillis / 1000 / 60;
        int segundos = (int) mTimeLeftInMillis / 1000 % 60;

        String tiempoFormateado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);

        mCrono.setText(tiempoFormateado);
    }
}
