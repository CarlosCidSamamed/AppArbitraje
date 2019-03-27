package com.fervenzagames.apparbitraje.Arbitraje_Activities;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SalaEsperaArbitroActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTexto;

    private DatabaseReference mRootDB;
    private DatabaseReference mArbiDB;

    private FirebaseAuth mAuth;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_espera_arbitro);

        mToolbar = findViewById(R.id.sala_espera_arbi_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sala de Espera");

        mTexto = findViewById(R.id.sala_espera_arbi_text);
        if(Build.VERSION.SDK_INT >= 26) {
            mTexto.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        mAuth = FirebaseAuth.getInstance();
        try{
            mUid = mAuth.getCurrentUser().getUid();
            modificarListo(mUid);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void modificarListo(String idArbi){
        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(idArbi);
        Query consulta = mArbiDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    // No se encuentra el Arbitro con ese ID
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    arbi.setListo(true);
                    Map<String, Object> arbiMap = arbi.toMap();
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("Arbitros/" + arbi.getIdArbitro(), arbiMap);
                    mRootDB.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
