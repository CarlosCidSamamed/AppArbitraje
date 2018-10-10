package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        // Si el usuario no ha iniciado sesión deberemos abrir la pantalla de LOGIN
        if(currentUser == null){
            // Se crea un Intent para la página de LOGIN
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            // Se inicia ese Intent
            startActivity(startIntent);
            // Con esta línea evitamos que al pulsar el botón para retroceder se vuelva a esta actividad.
            finish();
        }
    }
}
