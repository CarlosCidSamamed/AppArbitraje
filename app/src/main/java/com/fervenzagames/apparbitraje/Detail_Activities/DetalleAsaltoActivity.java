package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleAsaltoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mNumero;
    private CircleImageView mFotoGanador;
    private TextView mNombreGanador;
    private TextView mPuntRojo;
    private TextView mPuntAzul;
    private TextView mEstado;
    private TextView mMotivo;
    private TextView mDesc;
    private TextView mDuracion;
    private ExpandableListView mListaPuntsView;
    private ExpandableListView mListaIncsView;

    private List<Puntuaciones> listaPunts;
    private List<Incidencias> listaIncs;

    private DatabaseReference mAsaltoDB;
    private DatabaseReference mGanadorDB;
    private String mIdCombate;
    private String mIdAsalto;
    private String mIdGanador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_asalto);

        mToolbar = findViewById(R.id.detalle_asalto_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle del Asalto");

        mNumero = findViewById(R.id.detalle_asalto_num);
        mFotoGanador = findViewById(R.id.detalle_asalto_fotoGanador);
        mNombreGanador = findViewById(R.id.detalle_asalto_nombreGanador);
        mPuntRojo = findViewById(R.id.detalle_asalto_puntRojo);
        mPuntAzul = findViewById(R.id.detalle_asalto_puntAzul);
        mEstado = findViewById(R.id.detalle_asalto_estado);
        mMotivo = findViewById(R.id.detalle_asalto_motivo);
        mDesc = findViewById(R.id.detalle_asalto_desc);
        mDuracion = findViewById(R.id.detalle_asalto_duracion);
        mListaPuntsView = findViewById(R.id.detalle_asalto_listaPunts);
        mListaIncsView   = findViewById(R.id.detalle_asalto_listaIncs);

        listaPunts = new ArrayList<>();
        listaIncs = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        try {
            mIdCombate = extras.getString("idCombate");
            mIdAsalto = extras.getString("idAsalto");
            mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(mIdCombate).child(mIdAsalto);

            mGanadorDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");

            Query consulta = mAsaltoDB;
            consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Toast.makeText(DetalleAsaltoActivity.this, "No existe un Asalto con ese ID", Toast.LENGTH_SHORT).show();
                    } else {
                        Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                        //region Número
                        String num = String.valueOf(asalto.getNumAsalto());
                        num = "Número : " + num;
                        mNumero.setText(num);
                        //endregion
                        //region Estado
                        String estado = dataSnapshot.child("estado").getValue().toString();
                        Resources res = getResources();
                        switch(estado){
                            case "Pendiente":{
                                mEstado.setTextColor(res.getColor(R.color.colorAccent2));
                                break;
                            }
                            case "Finalizado":{
                                mEstado.setTextColor(res.getColor(R.color.colorVerde));
                                break;
                            }
                            case "Cancelado":{
                                mEstado.setTextColor(res.getColor(R.color.colorRojo));
                                break;
                            }
                            default:{
                                break;
                            }
                        }
                        mEstado.setText(estado);
                        //endregion
                        //region Ganador
                        if(!asalto.getGanador().equals("")){ // Si existe información sobre el ganador
                            Query consulta = mGanadorDB.child(asalto.getGanador());
                            consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()){
                                        Toast.makeText(DetalleAsaltoActivity.this, "No se encuentran los datos del Ganador del Asalto...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Competidores ganador = dataSnapshot.getValue(Competidores.class);
                                        String nombreCompleto = ganador.getNombreCompleto();
                                        String foto = ganador.getFoto();
                                        mNombreGanador.setText(nombreCompleto);
                                        Picasso.get().load(foto).into(mFotoGanador);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        //endregion
                        //region Motivo y Descripción
                        if(!asalto.getMotivo().equals("")){
                            mMotivo.setText(asalto.getMotivo());
                            if(!asalto.getDescripcion().equals("")) {
                                mDesc.setText(asalto.getDescripcion());
                            }
                        }
                        //endregion
                        //region Puntuación ROJO
                        String puntRojo = String.valueOf(asalto.getPuntuacionRojo());
                        mPuntRojo.setText(puntRojo);
                        //endregion
                        //region Puntuación AZUL
                        String puntAzul = String.valueOf(asalto.getPuntuacionAzul());
                        mPuntAzul.setText(puntAzul);
                        //endregion
                        //region Duración
                        if(!asalto.getDuracion().equals("")){
                            mDuracion.setText(asalto.getDuracion());
                        }
                        //endregion
                        //region Lista Punts
                        // Crear Adapter
                        // Asignar el adapter al View
                        //endregion
                        //region Lista Incs
                        // Crear Adapter
                        // Asignar el adapter al View
                        //endregion
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
