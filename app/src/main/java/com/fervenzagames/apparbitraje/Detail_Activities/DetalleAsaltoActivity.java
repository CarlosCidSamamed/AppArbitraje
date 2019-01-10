package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.IncidenciasExpandableListAdapter;
import com.fervenzagames.apparbitraje.Adapters.PuntuacionesExpandableListAdapter;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.MesaArbitrajeActivity;
import com.fervenzagames.apparbitraje.Dialogs.DetalleIncidenciaDialog;
import com.fervenzagames.apparbitraje.Dialogs.DetallePuntuacionDialog;
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
    private CircleImageView mFotoRojo;
    private String urlFotoRojo;
    private TextView mPuntRojo;
    private CircleImageView mFotoAzul;
    private String urlFotoAzul;
    private TextView mPuntAzul;
    private TextView mEstado;
    private TextView mMotivo;
    private TextView mDesc;
    private TextView mDuracion;
    private ExpandableListView mListaPuntsView;
    private ExpandableListView mListaIncsView;
    private Button mIniciarBtn;

    private List<Puntuaciones> listaPunts;
    private List<Incidencias> listaIncs;

    private List<String> listaTitulosPunts;
    private List<String> listaDetallesPunts;

    private List<String> listaTitulosIncs;
    private List<String> listaDetallesIncs;

    private List<String> listaIDsPunts;
    private List<String> listaIDsIncs;

    private DatabaseReference mAsaltoDB;
    private DatabaseReference mGanadorDB;
    private String mIdCombate;
    private String mIdAsalto;
    private String mIdGanador;
    private String mIdRojo;
    private String mIdAzul;

    private DatabaseReference mPuntsDB;
    private DatabaseReference mIncsDB;


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
        mFotoRojo = findViewById(R.id.detalle_asalto_fotoRojo);
        mPuntRojo = findViewById(R.id.detalle_asalto_puntRojo);
        mFotoAzul = findViewById(R.id.detalle_asalto_fotoAzul);
        mPuntAzul = findViewById(R.id.detalle_asalto_puntAzul);
        mEstado = findViewById(R.id.detalle_asalto_estado);
        mMotivo = findViewById(R.id.detalle_asalto_motivo);
        mDesc = findViewById(R.id.detalle_asalto_desc);
        mDuracion = findViewById(R.id.detalle_asalto_duracion);
        mListaPuntsView = findViewById(R.id.detalle_asalto_listaPunts);
        mListaIncsView   = findViewById(R.id.detalle_asalto_listaIncs);
        mIniciarBtn = findViewById(R.id.detalle_asalto_iniciarBtn);

        // Por defecto el botón de Iniciar se oculta si el estado del Asalto no es Pendiente.
        mIniciarBtn.setVisibility(View.INVISIBLE);

        listaPunts = new ArrayList<>();
        listaIncs = new ArrayList<>();

        listaTitulosPunts = new ArrayList<>();
        listaDetallesPunts = new ArrayList<>();
        listaTitulosIncs = new ArrayList<>();
        listaDetallesIncs = new ArrayList<>();
        listaIDsPunts = new ArrayList<>();
        listaIDsIncs = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        try {
            mIdCombate = extras.getString("idCombate");
            mIdAsalto = extras.getString("idAsalto");
            mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(mIdCombate).child(mIdAsalto);

            mGanadorDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");

            urlFotoRojo = extras.getString("urlFotoRojo");
            urlFotoAzul = extras.getString("urlFotoAzul");

            mIdRojo = extras.getString("idRojo");
            mIdAzul = extras.getString("idAzul");

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
                                // Mostrar el botón de Iniciar el Asalto
                                mIniciarBtn.setVisibility(View.VISIBLE);
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
                        //region Puntuación y Foto ROJO
                        String puntRojo = String.valueOf(asalto.getPuntuacionRojo());
                        mPuntRojo.setText(puntRojo);
                        Picasso.get().load(urlFotoRojo).into(mFotoRojo);
                        //endregion
                        //region Puntuación y Foto AZUL
                        String puntAzul = String.valueOf(asalto.getPuntuacionAzul());
                        mPuntAzul.setText(puntAzul);
                        Picasso.get().load(urlFotoAzul).into(mFotoAzul);
                        //endregion
                        //region Duración
                        if(!asalto.getDuracion().equals("")){
                            mDuracion.setText(asalto.getDuracion());
                        }
                        //endregion
                        //region Lista Punts
                        // Obtener la lista de Puntuaciones para este Asalto
                        listaPunts.clear();
                        listaPunts = asalto.getListaPuntuaciones();
                        if(listaPunts == null){
                            Toast.makeText(DetalleAsaltoActivity.this, "No existen Puntuaciones para este Asalto", Toast.LENGTH_SHORT).show();
                        } else {
                            // Procesar lista para obtener Headers y Children para el ExpandableListView
                            procesarListaPuntuaciones(listaPunts);
                            // Crear Adapter
                            PuntuacionesExpandableListAdapter adapterPunts = new PuntuacionesExpandableListAdapter(DetalleAsaltoActivity.this, listaTitulosPunts, listaDetallesPunts);
                            // Asignar el adapter al View
                            mListaPuntsView.setAdapter(adapterPunts);
                            adapterPunts.notifyDataSetChanged();
                        }
                        //endregion
                        //region Lista Incs
                        // Obtener la lista de Incidencias para este Asalto
                        listaIncs.clear();
                        listaIncs = asalto.getListaIncidencias();
                        if(listaIncs == null)
                        {
                            Toast.makeText(DetalleAsaltoActivity.this, "No existen Incidencias para este Asalto", Toast.LENGTH_SHORT).show();
                        } else {
                            // Procesar lista para obtener Headers y Children para el ExpandableListView
                            procesarListaIncidencias(listaIncs);
                            // Crear Adapter
                            IncidenciasExpandableListAdapter adapterIncs = new IncidenciasExpandableListAdapter(DetalleAsaltoActivity.this, listaTitulosIncs, listaDetallesIncs);
                            // Asignar el adapter al View
                            mListaIncsView.setAdapter(adapterIncs);
                            adapterIncs.notifyDataSetChanged();
                        }
                        //endregion
                        //region Click en una Puntuación
                        mListaPuntsView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                listaPunts.clear();
                                if(listaTitulosPunts.size() > 0){
                                    String idPunt = listaIDsPunts.get(groupPosition);
                                    abrirDialogPunt(idPunt, mIdAsalto);
                                }
                                return false;
                            }
                        });
                        //endregion
                        //region Click en una Incidencia
                        mListaIncsView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                listaIncs.clear();
                                if(listaTitulosIncs.size() > 0){
                                    String idInc = listaIDsIncs.get(groupPosition);
                                    abrirDialogInc(idInc, mIdAsalto);
                                }
                                return false;
                            }
                        });
                        //endregion
                        //region Click en mIniciarBtn
                        mIniciarBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Abrir Dialog de Confirmación
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetalleAsaltoActivity.this);
                                builder.setMessage("¿Desea comenzar el arbitraje de este Asalto?")
                                        .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(DetalleAsaltoActivity.this, "Se ha confirmado el Inicio del Asalto ", Toast.LENGTH_SHORT).show();
                                                cargarDatosArbitrajeAsalto(mIdAsalto);
                                            }
                                        })
                                        .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(DetalleAsaltoActivity.this, "Se ha cancelado el Inicio del Asalto", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
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

    private void procesarListaPuntuaciones(List<Puntuaciones> lista){
        List<String> titulos = new ArrayList<>();
        List<String> detalles = new ArrayList<>();
        List<String> listaIDs = new ArrayList<>();
        for(int i = 0; i < lista.size(); i++){
            titulos.add(lista.get(i).getConcepto());
            detalles.add(lista.get(i).getTipoAtaque());
            listaIDs.add(lista.get(i).getId());
        }
        listaTitulosPunts = titulos;
        listaDetallesPunts = detalles;
        listaIDsPunts = listaIDs;
        Toast.makeText(this, "Tamaño de la lista de Puntuaciones para este Asalto : " + lista.size(), Toast.LENGTH_SHORT).show();
    }

    private void procesarListaIncidencias(List<Incidencias> lista){
        List<String> titulos = new ArrayList<>();
        List<String> detalles = new ArrayList<>();
        List<String> listaIDs = new ArrayList<>();
        for(int i = 0; i < lista.size(); i++){
            titulos.add(lista.get(i).getTipo());
            detalles.add(lista.get(i).getDescripcion());
            listaIDs.add(lista.get(i).getId());
        }
        listaTitulosIncs = titulos;
        listaDetallesIncs = detalles;
        listaIDsIncs = listaIDs;
        Toast.makeText(this, "Tamaño de la lista de Incidencias para este Asalto : " + lista.size(), Toast.LENGTH_SHORT).show();
    }

    private void abrirDialogPunt(String idPunt, String idAsalto){
        DetallePuntuacionDialog dialog = new DetallePuntuacionDialog();
        Bundle extras = new Bundle();
        extras.putString("idPunt", idPunt);
        extras.putString("idAsalto", idAsalto);
        dialog.setArguments(extras);
        dialog.show(getSupportFragmentManager(), "detalle puntuacion dialog");
    }

    private void abrirDialogInc(String idInc, String idAsalto){
        DetalleIncidenciaDialog dialog = new DetalleIncidenciaDialog();
        Bundle extras = new Bundle();
        extras.putString("idInc", idInc);
        extras.putString("idAsalto", idAsalto);
        dialog.setArguments(extras);
        dialog.show(getSupportFragmentManager(), "detalle incidencia dialog");
    }

    // Este método se encargará de cargar los datos del asalto para poder iniciar el arbitraje del asalto indicado mediante el ID.
    private void cargarDatosArbitrajeAsalto(String idAsalto){
        Bundle extras = new Bundle();
        extras.putString("idComb", mIdCombate);
        extras.putString("idAsalto", idAsalto);
        extras.putString("idRojo", mIdRojo);
        extras.putString("idAzul", mIdAzul);
        Intent arbitrarIntent = new Intent(DetalleAsaltoActivity.this, MesaArbitrajeActivity.class);
        arbitrarIntent.putExtras(extras);
        startActivity(arbitrarIntent);
    }

}
