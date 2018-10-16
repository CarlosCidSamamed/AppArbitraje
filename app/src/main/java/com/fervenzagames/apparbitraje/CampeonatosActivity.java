package com.fervenzagames.apparbitraje;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CampeonatosActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    //private RecyclerView mCampList;

    private ListView campListView;
    private DatabaseReference campDB;

    private List<Campeonatos> campList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campeonatos);

        campDB = FirebaseDatabase.getInstance().getReference().child("Arbitraje").child("Campeonatos");

        mToolbar = (Toolbar) findViewById(R.id.camp_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Todos los Campeonatos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campListView = (ListView) findViewById(R.id.camp_listView);
        campList = new ArrayList<>();

        /*mCampList = (RecyclerView) findViewById(R.id.camp_list);
        mCampList.setHasFixedSize(true);
        mCampList.setLayoutManager(new LinearLayoutManager(this));*/
    }

    @Override
    protected void onStart() { // To retrieve data in real time
        super.onStart();

        campDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                campList.clear();
                for(DataSnapshot campSnapshot: dataSnapshot.getChildren()){
                    Campeonatos camp = campSnapshot.getValue(Campeonatos.class);
                    campList.add(camp);
                }
                CampeonatosList adapter = new CampeonatosList(CampeonatosActivity.this, campList);
                campListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* DatabaseReference campsRef = FirebaseDatabase.getInstance().getReference().child("Arbitraje").child("Campeonatos");
        Query campsQuery = campsRef.orderByKey();

        FirebaseRecyclerOptions campsOptions = new FirebaseRecyclerOptions.Builder<Campeonatos>().setQuery(campsQuery, Campeonatos.class).build();

        final FirebaseRecyclerAdapter<Campeonatos, CampsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Campeonatos, CampsViewHolder>(campsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CampsViewHolder holder, int position, @NonNull Campeonatos model) {
                holder.setCampNombre(model.getNombre());
                holder.setCampFecha(model.getFecha());
                holder.setCampLugar(model.getLugar());
            }

            @NonNull
            @Override
            public CampsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.camp_single_layout, parent, false);

                return new CampsViewHolder(view);
            }


        };*/

        // mCampList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CampsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CampsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setCampNombre(String n){
            TextView nombre = mView.findViewById(R.id.camp_nombre);
            nombre.setText(n);
        }

        public void setCampLugar(String lg)
        {
            TextView lugar = mView.findViewById(R.id.camp_lugar);
            lugar.setText(lg);
        }

        public void setCampFecha(String f)
        {
           TextView fecha = mView.findViewById(R.id.camp_fecha);
           fecha.setText(f);
        }

    }
}
