
package com.fervenzagames.apparbitraje.User_Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
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
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText mDisplayName;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mCreateBtn;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    // ProgressDialog
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(detectarTipoDispostivo() == 1){ // TABLET
            setContentView(R.layout.activity_register);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mToolbar = findViewById(R.id.register_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Crear una nueva Cuenta");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); /* Al pulsar el botón de la flecha hacia atrás volveremos al inicio. */

            mDisplayName = findViewById(R.id.reg_display_name2);
            mEmail = findViewById(R.id.reg_email2);
            mPassword = findViewById(R.id.reg_password2);
            mCreateBtn = findViewById(R.id.reg_create_btn);

        } else if(detectarTipoDispostivo() == 0){ // MÓVIL
            setContentView(R.layout.phone_register);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mToolbar = findViewById(R.id.phone_register_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); /* Al pulsar el botón de la flecha hacia atrás volveremos al inicio. */

            mDisplayName = findViewById(R.id.phone_reg_display_name2);
            mEmail = findViewById(R.id.phone_reg_email2);
            mPassword = findViewById(R.id.phone_reg_password2);
            mCreateBtn = findViewById(R.id.phone_reg_create_btn);
        }

        // Progress Dialog
        mRegProgress = new ProgressDialog(this);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registrando el nuevo usuario");
                    mRegProgress.setMessage("Por favor espere mientras se completa el registro...");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, password);
                } else {
                    // El formulario no está completo
                    Toast.makeText(RegisterActivity.this, "Rellene todos los datos antes de pulsar el botón de Registro.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void register_user(final String display_name, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            // Añadir los datos en la rama Usuarios
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uid);

                            HashMap<String, String> userMap = new HashMap();
                            userMap.put("nombre", display_name);
                            userMap.put("cargo", "Silla");
                            userMap.put("nivel", "Nivel 1");
                            userMap.put("imagen", "default");
                            userMap.put("imagen_thumb", "default");
                            userMap.put("email", email);
                            userMap.put("conectado", "true"); // Por defecto el usuario se conecta al crear la cuenta.

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        mRegProgress.dismiss();
                                        /* Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        // Para asegurarnos de que no se puede volver a la StartActivity al pulsar el botón BACK (flecha atrás) de la barra de botones en el fondo de la pantalla (ANDROID)
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish(); */
                                    }
                                }
                            });

                            // Añadir los datos en la rama Arbitros
                            mDatabase = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros").child(uid);
                            // Los datos de cada Árbitro se almacenan en una rama cuyo ID es el ID del Usuario correspondiente.
                            // Se crea un nuevo Árbitro
                            List<Combates> lista = new ArrayList<>();
                            Arbitros arbi = new Arbitros(uid, display_name, "DNI", email, password, "default", "", "", "Nivel 1", "Silla", 0, "", true,  lista);
                            // Se guardan los datos del árbitro en la BD.
                            mDatabase.setValue(arbi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        /* Para asegurarnos de que no se puede volver a la StartActivity al pulsar el botón BACK (flecha atrás) de la barra de botones en el fondo de la pantalla (ANDROID)*/
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });


                        } else {
                            mRegProgress.hide();
                            // Toast.makeText(RegisterActivity.this, "Error al registrar la nueva cuenta...", Toast.LENGTH_LONG).show();
                            // Comprobación y gestión de errores en el formulario
                            String error = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e)
                            {
                                error = "Contraseña Débil";
                            } catch (FirebaseAuthInvalidCredentialsException e)
                            {
                                error = "Email incorrecto";
                            } catch (FirebaseAuthUserCollisionException e)
                            {
                                error = "La cuenta ya existe";
                            } catch (Exception e)
                            {
                                error = "Error desconocido";
                                e.printStackTrace();
                            }

                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                        }

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
