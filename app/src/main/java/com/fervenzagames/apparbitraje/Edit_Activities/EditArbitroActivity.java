package com.fervenzagames.apparbitraje.Edit_Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditArbitroActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputEditText mNombre;
    private TextInputEditText mDNI;
    private TextInputEditText mEmail;

    private Button mCambiarPwd;

    private CircleImageView mFoto;
    private Button mCambiarFoto;

    private Spinner mNivel;
    private Spinner mCargo;
    private Spinner mCampeonato;
    private Spinner mZona;

    private DatabaseReference mArbitroDB;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_arbitro);

        mToolbar = findViewById(R.id.edit_arbitro_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Editar Árbitro");

        mNombre = findViewById(R.id.editar_arb_nombreText);
        mDNI = findViewById(R.id.editar_arb_dniText);
        mEmail = findViewById(R.id.editar_arb_emailText);

        mCambiarPwd = findViewById(R.id.editar_arb_cambiarPwdBtn);

        mFoto = findViewById(R.id.editar_arb_foto);
        mCambiarPwd = findViewById(R.id.editar_arb_cambiarImagenBtn);

        mNivel = findViewById(R.id.editar_arb_nivelSpinner);
        mCargo = findViewById(R.id.editar_arb_cargoSpinner);
        mCampeonato = findViewById(R.id.editar_arb_campSpinner);
        mZona = findViewById(R.id.editar_arb_zonaCombateSpinner);

        extras = getIntent().getExtras();
        String idArb = extras.getString("idArbitro");

        mArbitroDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros").child(idArb); // Referencia a la BD correspondiente al Árbitro deseado.

        // Toast.makeText(this, "ID Arbitro --> " + idArb, Toast.LENGTH_SHORT).show();

        mArbitroDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try { // Recuperar los datos actuales antes de editarlos
                    mNombre.setText(dataSnapshot.child("nombre").getValue().toString());
                    mDNI.setText(dataSnapshot.child("dni").getValue().toString());
                    mEmail.setText(dataSnapshot.child("email").getValue().toString());
                    Picasso.get().load(dataSnapshot.child("foto").getValue().toString()).into(mFoto);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
