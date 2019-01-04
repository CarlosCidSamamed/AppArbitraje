package com.fervenzagames.apparbitraje.Detail_Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.Models.ZonasCombate;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetalleZonaActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNombreCamp;
    private TextView mNumZona;

    private DatabaseReference mZonaDB;
    private DatabaseReference mCampDB;

    private String mIdZona;
    private String mIdCamp;

    private String mNombre;
    private String mNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_zona);

        mToolbar = findViewById(R.id.detalle_zona_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Zona de Combate");

        mNombreCamp = findViewById(R.id.detalle_zona_nombreCamp);
        mNumZona = findViewById(R.id.detalle_zona_numZona);

        Bundle extras = getIntent().getExtras();
        try {
            mIdZona = extras.getString("idZona");
            mIdCamp = extras.getString("idCamp");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mZonaDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate").child(mIdCamp).child(mIdZona);
        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Campeonatos").child(mIdCamp);
        //Toast.makeText(this, "idCamp --> " + mIdCamp + " // idZona --> " + mIdZona, Toast.LENGTH_SHORT).show();
        mZonaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(DetalleZonaActivity.this, "No existe ninguna Zona de Combate que coincida con los datos especificados...", Toast.LENGTH_SHORT).show();
                } else {
                    ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class);
                    try {
                        mNum = String.valueOf(zona.getNumZona());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    mCampDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                Toast.makeText(DetalleZonaActivity.this, "No existe ningún Campeonato con esos datos...", Toast.LENGTH_SHORT).show();
                            } else {
                                Campeonatos camp = dataSnapshot.getValue(Campeonatos.class);
                                try {
                                    mNombre = camp.getNombre();
                                    mNombreCamp.setText(mNombre);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    String num = " Zona : " + mNum;
                    mNumZona.setText(num);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
