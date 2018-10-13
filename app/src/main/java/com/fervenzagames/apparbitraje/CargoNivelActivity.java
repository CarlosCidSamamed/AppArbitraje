package com.fervenzagames.apparbitraje;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CargoNivelActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mCargo;
    private TextInputLayout mNivel;
    private Button mGuardarBtn;

    //Firebase
    private DatabaseReference mCargoNivelDatabase;
    private FirebaseUser mCurrentUser;

    //Progress Dialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo_nivel);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_id = mCurrentUser.getUid();
        mCargoNivelDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(current_id);

        mToolbar = (Toolbar) findViewById(R.id.cargo_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Cargo y Nivel del Árbitro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCargo = (TextInputLayout) findViewById(R.id.cargo_input);
        mNivel = (TextInputLayout) findViewById(R.id.nivel_input);
        mGuardarBtn = (Button) findViewById(R.id.guardar_btn);

        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cargo = mCargo.getEditText().getText().toString();
                String nivel = mNivel.getEditText().getText().toString();

                // Progress
                mProgress = new ProgressDialog(getApplicationContext());
                mProgress.setTitle("Guardando los cambios");
                mProgress.setMessage("Espere mientras se guardan los cambios...");
                mProgress.show();

                // Si el usuario quiere modificar el Cargo y el Nivel
                if(!TextUtils.isEmpty(cargo) && (!TextUtils.isEmpty(nivel)))
                {
                    cargo = mCargo.getEditText().getText().toString();
                    nivel = mNivel.getEditText().getText().toString();

                    HashMap<String, String> cambios = new HashMap<>();
                    cambios.put("cargo", cargo);
                    cambios.put("nivel", nivel);
                    mCargoNivelDatabase.setValue(cambios).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                mProgress.dismiss();
                            }
                        }
                    });

                } else if (!TextUtils.isEmpty(cargo)) { // Solo el Cargo

                    cargo = mCargo.getEditText().getText().toString();
                    mCargoNivelDatabase.child("cargo").setValue(cargo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgress.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "¡Se ha producido un error al guardar los cambios!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else if (!TextUtils.isEmpty(nivel)) { // Solo el Nivel
                    nivel = mNivel.getEditText().getText().toString();
                    mCargoNivelDatabase.child("nivel").setValue(nivel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mProgress.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "¡Se ha producido un error al guardar los cambios!", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                }

            }
        });


    }
}

