
package com.fervenzagames.apparbitraje.User_Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.MainActivity;
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

import java.util.HashMap;

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
        setContentView(R.layout.activity_register);

        // Set toolbar
        mToolbar = (Toolbar)  findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Crear una nueva Cuenta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); /* Al pulsar el botón de la flecha hacia atrás volveremos al inicio. */

        // Progress Dialog
        mRegProgress = new ProgressDialog(this);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputEditText) findViewById(R.id.reg_display_name2);
        mEmail = (TextInputEditText) findViewById(R.id.reg_email2);
        mPassword = (TextInputEditText) findViewById(R.id.reg_password2);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);

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

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uid);

                            HashMap<String, String> userMap = new HashMap();
                            userMap.put("nombre", display_name);
                            userMap.put("cargo", "Silla");
                            userMap.put("nivel", "Nivel 1");
                            userMap.put("imagen", "default");
                            userMap.put("imagen_thumb", "default");

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        mRegProgress.dismiss();
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
}
