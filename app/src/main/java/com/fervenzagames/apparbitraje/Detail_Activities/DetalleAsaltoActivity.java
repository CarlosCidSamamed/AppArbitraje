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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.PuntuacionesAdapter;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.LobbyArbitraje;
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
import java.util.HashMap;
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
    //private ExpandableListView mListaPuntsRojoView;
    private ListView mListaPuntsRojoView;
    private ListView mListaPuntsAzulView;
    //private ExpandableListView mListaIncsView;
    private ListView mListaIncsView;
    private Button mIniciarBtn;

    private List<Puntuaciones> listaPunts;
    private List<Incidencias> listaIncs;

    private List<Puntuaciones> listaPuntsRojo;
    private List<Puntuaciones> listaPuntsAzul;

    private List<String> listaTitulosPunts;
    private List<String> listaDetallesPunts;

    private List<String> listaTitulosIncs;
    private List<String> listaDetallesIncs;

    private List<String> listaIDsPunts;
    private List<String> listaIDsIncs;

    private HashMap<String, List<String>> mDetallesHashMap;

    private DatabaseReference mAsaltoDB;
    private DatabaseReference mGanadorDB;
    private String mIdCombate;
    private String mIdAsalto;
    private String mIdGanador;
    private String mIdRojo;
    private String mIdAzul;
    private String mIdCat;
    private String mIdZona;
    private String mIdCamp;

    private String mNombreMod;

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
        mListaPuntsRojoView = findViewById(R.id.detalle_asalto_listaPuntsRojo);
        mListaPuntsAzulView = findViewById(R.id.detalle_asalto_listaPuntsAzul);
        mListaIncsView   = findViewById(R.id.detalle_asalto_listaIncs);
        mIniciarBtn = findViewById(R.id.detalle_asalto_iniciarBtn);

        // Por defecto el botón de Iniciar se oculta si el estado del Asalto no es Pendiente.
        mIniciarBtn.setVisibility(View.INVISIBLE);

        listaPunts = new ArrayList<>();
        listaIncs = new ArrayList<>();

        listaPuntsRojo = new ArrayList<>();
        listaPuntsAzul = new ArrayList<>();

        listaTitulosPunts = new ArrayList<>();
        listaDetallesPunts = new ArrayList<>();
        listaTitulosIncs = new ArrayList<>();
        listaDetallesIncs = new ArrayList<>();
        listaIDsPunts = new ArrayList<>();
        listaIDsIncs = new ArrayList<>();

        mDetallesHashMap = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        try {
            mIdCat = extras.getString("idCat");
            mIdCombate = extras.getString("idCombate");
            mIdAsalto = extras.getString("idAsalto");
            mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(mIdCombate).child(mIdAsalto);

            mGanadorDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");

            urlFotoRojo = extras.getString("urlFotoRojo");
            urlFotoAzul = extras.getString("urlFotoAzul");

            mIdRojo = extras.getString("idRojo");
            mIdAzul = extras.getString("idAzul");

            mIdZona = extras.getString("idZona");
            mIdCamp = extras.getString("idCamp");

            mNombreMod = extras.getString("nombreMod");

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
                            //procesarListaPuntuaciones(listaPunts);
                            //procesarListasPuntuaciones2(listaPunts);
                            // Crear Adapter
                            //PuntuacionesExpandableListAdapter adapterPunts = new PuntuacionesExpandableListAdapter(DetalleAsaltoActivity.this, listaTitulosPunts, listaDetallesPunts);
                            //PuntuacionesExpandableListAdapter adapterPunts = new PuntuacionesExpandableListAdapter(DetalleAsaltoActivity.this,
                            //        listaTitulosPunts, mDetallesHashMap);

                            // Obtener lista Puntuaciones Rojo y Azul
                            listaPuntsRojo = getListaPuntsRojoAzul(mIdRojo, listaPunts);
                            listaPuntsAzul = getListaPuntsRojoAzul(mIdAzul, listaPunts);

                            PuntuacionesAdapter adapterPuntsRojo = new PuntuacionesAdapter(DetalleAsaltoActivity.this, listaPuntsRojo, "Rojo");
                            // Asignar el adapter al View
                            //mListaPuntsRojoView.setAdapter(adapterPunts);
                            mListaPuntsRojoView.setAdapter(adapterPuntsRojo);
                            adapterPuntsRojo.notifyDataSetChanged();

                            PuntuacionesAdapter adapterPuntsAzul = new PuntuacionesAdapter(DetalleAsaltoActivity.this, listaPuntsAzul, "Azul");
                            mListaPuntsAzulView.setAdapter(adapterPuntsAzul);
                            adapterPuntsAzul.notifyDataSetChanged();
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
                            //procesarListaIncidencias(listaIncs);
                            // Crear Adapter
                            //IncidenciasExpandableListAdapter adapterIncs = new IncidenciasExpandableListAdapter(DetalleAsaltoActivity.this,
                            //        listaTitulosIncs, listaDetallesIncs);
                            // Asignar el adapter al View
                            //mListaIncsView.setAdapter(adapterIncs);
                            //adapterIncs.notifyDataSetChanged();
                        }
                        //endregion
                        //region Click en una Puntuación
                        /*mListaPuntsRojoView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                listaPunts.clear();
                                if(listaTitulosPunts.size() > 0){
                                    String idPunt = listaIDsPunts.get(groupPosition);
                                    abrirDialogPunt(idPunt, mIdAsalto);
                                }
                                return false;
                            }
                        });*/
                        mListaPuntsRojoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Puntuaciones p = listaPuntsRojo.get(i);
                                String idPunt = p.getId();
                                abrirDialogPunt(idPunt, mIdAsalto);
                            }
                        });
                        mListaPuntsAzulView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Puntuaciones p = listaPuntsAzul.get(i);
                                String idPunt = p.getId();
                                abrirDialogPunt(idPunt, mIdAsalto);
                            }
                        });
                        //endregion
                        //region Click en una Incidencia
                        /*mListaIncsView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                listaIncs.clear();
                                if(listaTitulosIncs.size() > 0){
                                    String idInc = listaIDsIncs.get(groupPosition);
                                    abrirDialogInc(idInc, mIdAsalto);
                                }
                                return false;
                            }
                        });*/
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

    private List<Puntuaciones> getListaPuntsRojoAzul(String idComp, List<Puntuaciones> listaPunt){
        List<Puntuaciones> nuevaLista = new ArrayList<>();
        for(int i = 0; i < listaPunt.size(); i++){
            Puntuaciones p = listaPunt.get(i);
            if(p.getIdCompetidor().equals(idComp)){
                nuevaLista.add(p);
            }
        }
        return nuevaLista;
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

    private void procesarListasPuntuaciones2(List<Puntuaciones> lista){
        List<String> titulos = new ArrayList<>();
        HashMap<String, List<String>> detallesHashMap = new HashMap<>();
        List<String> listaIDs = new ArrayList<>();
        // Para cada puntuación vamos a obtener su concepto, lo pondremos como título y la lista con un elemento será el detalle.
        for(int i = 0; i < lista.size(); i++){
            String titulo = lista.get(i).getConcepto();
            titulos.add(titulo);
            listaIDs.add(lista.get(i).getId());
            List<String> detalles = obtenerDetalle(lista.get(i));
            Toast.makeText(this, "Detalle --> " + detalles.get(0), Toast.LENGTH_SHORT).show();
            //detalles.add(lista.get(i).getTipoAtaque());
            detallesHashMap.put(titulo, detalles);
        }
        listaTitulosPunts = titulos;
        listaIDsPunts = listaIDs;
        mDetallesHashMap = detallesHashMap;
    }

    private List<String> obtenerDetalle(Puntuaciones p){
        List<String> detalle = new ArrayList<>();
        detalle.add(p.getTipoAtaque());
        //Toast.makeText(this, "Detalle --> " + p.getTipoAtaque(), Toast.LENGTH_SHORT).show();
        return detalle;
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
        extras.putString("idCat", mIdCat);
        /*Intent arbitrarIntent = new Intent(DetalleAsaltoActivity.this, MesaArbitrajeActivity.class);
        arbitrarIntent.putExtras(extras);
        startActivity(arbitrarIntent);*/
        extras.putString("idZona", mIdZona);
        extras.putString("idCamp", mIdCamp);
        extras.putString("nombreMod", mNombreMod);
        Intent lobbyArbitrar = new Intent(DetalleAsaltoActivity.this, LobbyArbitraje.class);
        lobbyArbitrar.putExtras(extras);
        startActivity(lobbyArbitrar);
    }

}
