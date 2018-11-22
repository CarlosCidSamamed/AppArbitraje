package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.fervenzagames.apparbitraje.Adapters.ArbitrosList;
import com.fervenzagames.apparbitraje.Detail_Activities.DetalleArbitroActivity;
import com.fervenzagames.apparbitraje.Edit_Activities.EditArbitroActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.User_Activities.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArbitrosActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ListView mArbisListView;
    private Button mAddArbi;

    private DatabaseReference mArbisDB;

    private List<Arbitros> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arbitros);

        mToolbar = findViewById(R.id.arbis_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Listado de Árbitros");

        mArbisListView = findViewById(R.id.arbis_listView);
        mAddArbi = findViewById(R.id.arbis_addArbitro);
        lista = new ArrayList<>();

        mArbisDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros");

        mArbisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Arbitros arbi = lista.get(position);
                Intent detalleArbi = new Intent (ArbitrosActivity.this, DetalleArbitroActivity.class);
                detalleArbi.putExtra("idArbitro", arbi.getIdArbitro());
                startActivity(detalleArbi);
            }
        });

        mAddArbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addArbiIntent = new Intent(ArbitrosActivity.this, RegisterActivity.class);
                startActivity(addArbiIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mArbisDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                for(DataSnapshot arbiSnapshot: dataSnapshot.getChildren()){
                    Arbitros arbi = arbiSnapshot.getValue(Arbitros.class);
                    lista.add(arbi);
                }
                ArbitrosList adapter = new ArbitrosList(ArbitrosActivity.this, lista);
                mArbisListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
