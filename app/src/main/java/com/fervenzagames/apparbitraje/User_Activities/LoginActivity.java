package com.fervenzagames.apparbitraje.User_Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.MainActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLogin_btn;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mArbitrosDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(detectarTipoDispostivo() == 1){ // TABLET
            setContentView(R.layout.activity_login);

            mToolbar = findViewById(R.id.login_toolbar);
            mLoginEmail = findViewById(R.id.login_email);
            mLoginPassword = findViewById(R.id.login_password);
            mLogin_btn = findViewById(R.id.login_btn);

        } else if(detectarTipoDispostivo() == 0){ // MÓVIL
            setContentView(R.layout.phone_login);

            mToolbar = findViewById(R.id.phone_login_toolbar);
            mLoginEmail = findViewById(R.id.phone_login_email);
            mLoginPassword = findViewById(R.id.phone_login_password);
            mLogin_btn = findViewById(R.id.phone_login_btn);
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar Sesión");

        mLoginProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Iniciando Sesión");
                    mLoginProgress.setMessage("Espere mientras se inicia sesión con sus credenciales...");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);

                } else {
                    Toast.makeText(LoginActivity.this, "Rellene todos los datos antes de pulsar el botón de inicio de sesión...", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    // Modificar el campo conectado del Árbitro en la BD. CONECTADO --> TRUE
                    // Usaremos updateChildren especificando la ruta donde queremos actualizar los datos para no modificar los demás datos.

                    /*String uid = mCurrentUser.getUid();
                    actualizarEstadoArbitro(uid, true);*/

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();

                } else {
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Compruebe sus credenciales...", Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void actualizarEstadoArbitro(String uid, final boolean estado){
        // Como parámetros se pasan:
        // 1. el UID que permite identificar al usuario y al árbitro correspondiente.
        // 2. el nuevo estado del árbitro.
        final String id = uid;

        // El proceso de actualizar usando updateChildren lo he cogido de la documentación de Firebase: https://firebase.google.com/docs/database/android/read-and-write?hl=es-419

        mArbitrosDB.addListenerForSingleValueEvent(new ValueEventListener() { //Listens once for a single event.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recuperamos los datos del árbitro
                Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                // Modificamos el estado
                arbi.setConectado(estado);
                // Actualizamos los datos del Árbitro en la BD con el nuevo estado de conexión.
                Map<String, Object> nuevoArbi = arbi.toMap();
                Map<String, Object> updates = new HashMap<>();
                updates.put("/Arbitraje/Arbitros/" + id, nuevoArbi);
                mArbitrosDB.updateChildren(updates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        //Toast.makeText(this, "Tamaño en Pulgadas de la Pantalla --> " + screenInches, Toast.LENGTH_LONG).show();

        if(screenInches < 7.0f)
        {
            tipo = 0; // MÓVIL
        } else if (screenInches >= 7.0f){
            tipo = 1; // TABLET
        }

        return tipo;
    }
}
