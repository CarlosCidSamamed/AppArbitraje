package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.DatosSumaAdapter;
import com.fervenzagames.apparbitraje.Adapters.PuntuacionesAdapter;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.LobbyArbitraje;
import com.fervenzagames.apparbitraje.Dialogs.DetalleIncidenciaDialog;
import com.fervenzagames.apparbitraje.Dialogs.DetallePuntuacionDialog;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.Models.DatosSuma;
import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.Utils.FirebaseRTDB;
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

    private DatabaseReference mCombateDB;
    private List<String> mListaIDsArbis;

    private HashMap<String, List<String>> mDetallesHashMap;

    private List<String> listaSumasPuntos;

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

    private DatabaseReference mArbiDB;
    private String mFotoArbi;
    private String mDNI;

    private DatosSuma mDatosRojo;
    private DatosSuma mDatosAzul;

    private List<DatosSuma> mListaSumasRojo;
    private List<DatosSuma> mListaSumasAzul;

    private List<String> mListaDNIsArbis;
    private List<String> mListaFotosArbis;


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

        mFotoArbi = null;
        mDNI = null;

        mDatosRojo = new DatosSuma();
        mDatosAzul = new DatosSuma();

        mListaSumasRojo = new ArrayList<>();
        mListaSumasAzul = new ArrayList<>();

        mListaDNIsArbis = new ArrayList<>();
        mListaFotosArbis = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        try {
            mIdCat = extras.getString("idCat");
            mIdCombate = extras.getString("idCombate");
            mIdAsalto = extras.getString("idAsalto");
            mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(mIdCombate).child(mIdAsalto);

            mGanadorDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");

            urlFotoRojo = extras.getString("urlFotoRojo");
            urlFotoAzul = extras.getString("urlFotoAzul");

            if(!urlFotoRojo.equals("default")){
                Picasso.get().load(urlFotoRojo).into(mFotoRojo);
            }

            if(!urlFotoAzul.equals("default")){
                Picasso.get().load(urlFotoAzul).into(mFotoAzul);
            }

            mIdRojo = extras.getString("idRojo");
            mIdAzul = extras.getString("idAzul");

            mIdZona = extras.getString("idZona");
            mIdCamp = extras.getString("idCamp");

            mNombreMod = extras.getString("nombreMod");
            mListaIDsArbis = new ArrayList<>();

            getArbis(); // Obtener la lista de Arbitros asignados a este combate.



        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void getArbis (){
        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(mIdCat).child(mIdCombate);
        Query consulta = mCombateDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("DetalleAsalto", "Error al obtener los IDs de los Jueces del Combate");
                } else {
                    Combates combate = dataSnapshot.getValue(Combates.class);
                    try {
                        mListaIDsArbis = combate.getListaIDsArbis();
                        Log.v("DetalleAsalto", "Lista de IDs Arbis tiene " + mListaIDsArbis.size() + " elementos");
                        getDnisyFotos(mListaIDsArbis);

                        /*Log.v("getArbis", "Lista de DNIs Arbis tiene " + mListaDNIsArbis.size() + " elementos");

                        if(mListaDNIsArbis.size() > 0) {
                            for (int i = 0; i < mListaDNIsArbis.size(); i++) {
                                FirebaseRTDB.getDatosSumaArbiComp(mListaDNIsArbis.get(i),
                                        mIdRojo, mIdAzul, mIdAsalto, mIdCombate,
                                        mListaFotosArbis.get(i), DetalleAsaltoActivity.this,
                                        mListaPuntsRojoView, mListaPuntsAzulView, mListaSumasRojo, mListaSumasAzul);
                                Log.v("DetalleAsaltoActivity", "Bucle For --> i : " + i);
                                Log.v("DetalleAsaltoActivity", "Dni Juez --> " + mListaDNIsArbis.get(i));
                            }
                        } else {
                            Log.v("getArbis", "La lista de DNIs está vacía.");
                        }*/

                        /*if(mListaIDsArbis.size() > 0){
                            Log.v("DetalleAsalto", "Lista de IDs Arbis tiene " + mListaIDsArbis.size() + " elementos");
                            for(int i = 0; i < mListaIDsArbis.size(); i++){
                                getDniyFoto(mListaIDsArbis.get(i));
                                FirebaseRTDB.getDatosSumaArbiComp(mDNI, mIdRojo, mIdAzul, mIdAsalto, mIdCombate, mFotoArbi,
                                        DetalleAsaltoActivity.this, mListaPuntsRojoView, mListaPuntsAzulView,
                                        mListaSumasRojo, mListaSumasAzul);
                                Log.v("DetalleAsaltoActivity", "Bucle For --> i : " + i);
                                Log.v("DetalleAsaltoActivity", "Dni Juez --> " + mDNI);
                            }
                        } else {
                            Log.v("DetalleAsaltoActivity", "Error al obtener los IDs de los Jueces asignados a este Asalto");
                        }*/
                    } catch (NullPointerException e) {
                        Log.v("DetalleAsalto", "Excepción IDs Arbis : " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getDnisyFotos(List<String> listaIDsArbis){
        for(int i = 0; i < listaIDsArbis.size(); i++){
            String idArbi = mListaIDsArbis.get(i);
            Log.v("getDnisyFotos", "idArbi para i = " + i + " es --> " + idArbi);
            mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(idArbi);
            Query consulta = mArbiDB;
            consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Log.v("getDnisyFotos", "Error al localizar al Juez");
                    } else {
                        Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                        try {
                            mListaDNIsArbis.add(arbi.getDni());
                            Log.v("DetalleAsaltoActivity", "Dni Juez --> " + arbi.getDni());
                            mListaFotosArbis.add(arbi.getFoto());

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.v("getArbis", "Lista de DNIs Arbis tiene " + mListaDNIsArbis.size() + " elementos");

                    if((mListaDNIsArbis.size() > 0) && (mListaDNIsArbis.size() == mListaIDsArbis.size())){ // Solo se mostrarán los datos cuando se haya leído toda la lista de datos.
                        for (int i = 0; i < mListaDNIsArbis.size(); i++) {
                            FirebaseRTDB.getDatosSumaArbiComp(mListaDNIsArbis.get(i),
                                    mIdRojo, mIdAzul, mIdAsalto, mIdCombate,
                                    mListaFotosArbis.get(i), DetalleAsaltoActivity.this,
                                    mListaPuntsRojoView, mListaPuntsAzulView, mListaSumasRojo, mListaSumasAzul,
                                    mPuntRojo, mPuntAzul);
                            Log.v("DetalleAsaltoActivity", "Bucle For --> i : " + i);
                            Log.v("DetalleAsaltoActivity", "Dni Juez --> " + mListaDNIsArbis.get(i));
                        }
                    } else {
                        Log.v("getArbis", "La lista de DNIs está vacía.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void getDniyFoto(String idJuez){
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(idJuez);
        Query consulta = mArbiDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("DetalleAsaltoActivity", "getDniyFoto --- Error al localizar el Juez");
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    try {
                        mDNI = arbi.getDni();
                        Log.v("getDniyFoto", "Dni Juez --> " + mDNI);
                        mFotoArbi = arbi.getFoto();
                        Log.v("DetalleAsaltoActivity", "Foto del Juez --> " + mFotoArbi);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void obtenerDNIyPuntuaciones(final String idArbi){
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(idArbi);
        Query consulta = mArbiDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("DetalleAsalto", "Error al localizar al Arbitro cuyo ID es " + idArbi);
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    try {
                        mDNI = arbi.getDni();
                        Log.v("DetalleAsalto", "obtenerDNI --- Dni Juez ---> " + mDNI);
                        // Una vez que tengo el DNI del Juez debo usarlo para localizar las puntuaciones de ese juez en el asalto correspondiente.
                        mDatosRojo = new DatosSuma("", 0, mDNI, mIdRojo, mIdAsalto, mIdCombate);
                        mDatosRojo.getmListaPunts(mIdAsalto, mIdCombate, mIdRojo);
                        Log.v("DetalleAsalto", "Tamaño de la lista de Puntuaciones de mDatosRojo --> " + mDatosRojo.getmListaPunts().size());
/*                        int sumaRojo = mDatosRojo.getSumaPuntos();
                        Log.v("DetalleAsalto", "mDatosRojo Suma Puntos ---> " + sumaRojo);*/
                        mDatosAzul = new DatosSuma("", 0, mDNI, mIdAzul, mIdAsalto, mIdCombate);
                        mDatosAzul.getmListaPunts(mIdAsalto, mIdCombate, mIdAzul);
                    } catch (Exception e) {
                        Log.v("DetalleAsalto", "Excepcion al obtener el DNI del Arbitro : " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    /*private int getSumaPuntsPorArbiyCompetidor(String dniArbi, String idComp, List<Puntuaciones> listaPunts){
        int suma = 0;
        // Recorrer la lista de Puntuaciones y añadir las puntuaciones del Competidor y del Árbitro correctos a otra List<Puntuaciones>
        // Esa nueva lista se recorrerá para obtener la SUMA de sus valores y se devolverá dicha SUMA.
        List<Puntuaciones> listaFiltrada = new ArrayList<>();
        for(int i = 0; i < listaPunts.size(); i++){
            Puntuaciones p = listaPunts.get(i);
            if(p.getIdCompetidor().equals(idComp) && (p.getDniJuez().equals(dniArbi))){
                listaFiltrada.add(p);
            }
        }
        // Recorrer la nueva lista y obtener la suma.
        for(int i = 0; i < listaFiltrada.size(); i++){
            Puntuaciones p = listaFiltrada.get(i);
            suma += p.getValor();
        }
        return suma;
    }

    // El HashMap contiene el uri de la foto del arbitro y la suma de sus puntuaciones.
    private HashMap<String, String> getListaDatosArbi(String dniArbi, String idComp, List<Puntuaciones> listaPunts){
        HashMap<String, String> map = new HashMap<>();
        // Calcular la suma de las puntuaciones para
        int suma = getSumaPuntsPorArbiyCompetidor(dniArbi, idComp, listaPunts);
        String s = String.valueOf(suma);
        // Buscar la foto del Arbi
        getFotoArbi(dniArbi);
        if(mFotoArbi == null){
            map.put(s, "");
        } else {
            map.put(s, mFotoArbi);
        }
        return map;
    }*/

    private void cargarDatosArbisListView (){

    }

    private String getFotoArbi (final String dniArbi) {
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        Query consulta = mArbiDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("DetalleAsaltoActivity", "Error al localizar los Arbitros en la BD");
                } else {
                    for(DataSnapshot arbiSnap: dataSnapshot.getChildren()){
                        Arbitros arbi = arbiSnap.getValue(Arbitros.class);
                        try {
                            if(arbi.getDni().equals(dniArbi)){
                                mFotoArbi = arbi.getFoto();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.v("DetalleAsalto", "URL a la foto del Juez : " + mFotoArbi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mFotoArbi;
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
