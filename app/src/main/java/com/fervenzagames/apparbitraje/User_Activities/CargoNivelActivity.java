package com.fervenzagames.apparbitraje.User_Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CargoNivelActivity extends AppCompatActivity {

    private Toolbar mToolbar;
/*    private TextInputEditText mCargo;
    private TextInputEditText mNivel;*/

    private Spinner mNivelSpinner;
    private Spinner mCargoSpinner;

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
        getSupportActionBar().setTitle("Nivel y Cargo del Árbitro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String nivel_actual = getIntent().getStringExtra("nivel_val");
        String cargo_actual = getIntent().getStringExtra("cargo_val");

        mNivelSpinner = (Spinner) findViewById(R.id.nivel_spinner);
        mCargoSpinner = (Spinner) findViewById(R.id.cargo_spinner);

        // Por defecto se cargan las tareas del Nivel 1
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CargoNivelActivity.this, R.array.tareasNivelUno, android.R.layout.simple_spinner_item);
        // Una vez creado el Adapter para cada uno de los Arrays de Strings con las Tareas debo usar ese adapter.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCargoSpinner.setAdapter(adapter);

        mNivelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:{
                        ArrayAdapter<CharSequence> adapterUno = ArrayAdapter.createFromResource(CargoNivelActivity.this, R.array.tareasNivelUno, android.R.layout.simple_spinner_item);
                        adapterUno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCargoSpinner.setAdapter(adapterUno);
                        break;
                    }
                    case 1:{
                        ArrayAdapter<CharSequence> adapterDos = ArrayAdapter.createFromResource(CargoNivelActivity.this, R.array.tareasNivelDos, android.R.layout.simple_spinner_item);
                        adapterDos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCargoSpinner.setAdapter(adapterDos);
                        break;
                    }
                    case 2:{
                        ArrayAdapter<CharSequence> adapterTres = ArrayAdapter.createFromResource(CargoNivelActivity.this, R.array.tareasNivelTres, android.R.layout.simple_spinner_item);
                        adapterTres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mCargoSpinner.setAdapter(adapterTres);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



/*        mCargo = (TextInputEditText) findViewById(R.id.cargo_input1);
        mNivel = (TextInputEditText) findViewById(R.id.nivel_input1);*/
        mGuardarBtn = (Button) findViewById(R.id.guardar_btn);

/*
        mNivel.setText(nivel_actual);
        mCargo.setText(cargo_actual);
*/


        mGuardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*                String cargo = mCargo.getText().toString();
                String nivel = mNivel.getText().toString();*/

                // Progress
                mProgress = new ProgressDialog(CargoNivelActivity.this);
                mProgress.setTitle("Guardando los cambios");
                mProgress.setMessage("Espere mientras se guardan los cambios...");
                mProgress.show();

                // Para poder guardar los valores de los spinners los almacenaremos en Strings y de ahí a la BD.
                String nivel = mNivelSpinner.getSelectedItem().toString();
                String cargo = mCargoSpinner.getSelectedItem().toString();

                mCargoNivelDatabase.child("nivel").setValue(nivel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "¡Se ha producido un error al guardar los cambios!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                mCargoNivelDatabase.child("cargo").setValue(cargo).addOnCompleteListener(new OnCompleteListener<Void>() {
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

/*

                // Si el usuario quiere modificar el Cargo y el Nivel
                if(!TextUtils.isEmpty(cargo) && (!TextUtils.isEmpty(nivel)))
                {
                    cargo = mCargo.getText().toString();
                    nivel = mNivel.getText().toString();

                    mCargoNivelDatabase.child("nivel").setValue(nivel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgress.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "¡Se ha producido un error al guardar los cambios!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    mCargoNivelDatabase.child("cargo").setValue(cargo).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                } else if (!TextUtils.isEmpty(cargo)) { // Solo el Cargo

                    cargo = mCargo.getText().toString();
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
                    nivel = mNivel.getText().toString();
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

*/
            }
        });


    }
}

