package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.User_Activities.LoginActivity;
import com.fervenzagames.apparbitraje.User_Activities.RegisterActivity;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Detectar el tipo de dispositivo para cargar el layout correspondiente
        if(detectarTipoDispostivo() == 1){ // TABLET
            setContentView(R.layout.activity_start);

            mRegBtn = (Button) findViewById(R.id.start_reg_btn);
            mLoginBtn = (Button) findViewById(R.id.start_login_btn);
        } else if(detectarTipoDispostivo() == 0) { // MÓVIL
            setContentView(R.layout.phone_start);

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
}
