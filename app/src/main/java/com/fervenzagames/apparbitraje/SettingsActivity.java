package com.fervenzagames.apparbitraje;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout
    private CircleImageView mImagen;
    private TextView mNombre;
    private TextView mCargo;
    private TextView mNivel;
    private Button mImagenBtn;
    private Button mDatosBtn;

    //Progress Dialog
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImagen = (CircleImageView) findViewById(R.id.settings_image);
        mNombre = (TextView) findViewById(R.id.settings_nombre);
        mCargo = (TextView) findViewById(R.id.settings_cargo);
        mNivel = (TextView) findViewById(R.id.settings_nivel);
        mImagenBtn = (Button) findViewById(R.id.settings_change_image);
        mDatosBtn = (Button) findViewById(R.id.settings_change_data);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String nivel = dataSnapshot.child("nivel").getValue().toString();
                String cargo = dataSnapshot.child("cargo").getValue().toString();
                String imagen = dataSnapshot.child("imagen").getValue().toString();
                String imagen_thumb = dataSnapshot.child("imagen_thumb").getValue().toString();

                mNombre.setText(nombre);
                mNivel.setText(nivel);
                mCargo.setText(cargo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Al pulsar el botón para cambiar IMAGEN
        mImagenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // Al pulsar el botón para cambiar DATOS
        mDatosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent datosIntent = new Intent(SettingsActivity.this, CargoNivelActivity.class);
                startActivity(datosIntent);
            }
        });
    }
}
