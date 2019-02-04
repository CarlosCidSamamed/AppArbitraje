package com.fervenzagames.apparbitraje.Arbitraje_Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.ArbitrosMiniList;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.DatosExtraZonasCombate;
import com.fervenzagames.apparbitraje.Models.ZonasCombate;
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

public class LobbyArbitraje extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private Button mEnviarBtn;
    private Button mIniciarBtn;

    private ImageView mConectado;
    private CircleImageView mFoto;
    private TextView mNombre;

    private DatabaseReference mArbisDB;
    private DatabaseReference mZonaDB;
    private List<Arbitros> mLista;
    private List<String> mListaIDsArbis;
    private List<DatosExtraZonasCombate> mListaDatosExtraZona;
    private String mIdCamp;
    private String mIdCat;
    private String mIdCombate;
    private String mIdZona;
    private DatosExtraZonasCombate mDatosCombate;

    private int mContadorOK; // Lleva la cuenta de los árbitros que han confirmado su disponibilidad para arbitrar una vez enviada y recibida la notificación.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_arbitraje);

        mToolbar = findViewById(R.id.lobby_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lobby Arbitraje");
        mListView = findViewById(R.id.lobby_arbitraje_lista);
        mEnviarBtn = findViewById(R.id.lobyy_arbitraje_enviarBtn);
        mIniciarBtn = findViewById(R.id.lobby_arbitraje_iniciarBtn);

        mConectado = findViewById(R.id.arb_single_mini_estado);
        mFoto = findViewById(R.id.arb_single_mini_foto);
        mNombre = findViewById(R.id.arb_single_mini_nombre);

        mArbisDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        mZonaDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate");
        mLista = new ArrayList<>();
        mListaIDsArbis = new ArrayList<>();
        mListaDatosExtraZona = new ArrayList<>();

        mContadorOK = 0;

        Bundle extras = getIntent().getExtras();
        mIdCamp = extras.getString("idCamp");
        mIdCat = extras.getString("idCat");
        mIdCombate = extras.getString("idComb");
        mIdZona = extras.getString("idZona");


        recuperarDispArbitros(mIdCamp, mIdZona, mIdCombate);


    }

    // Recuperar el estado de disponibilidad de los árbitros de la zona de combate indicada para el combate correcto.
    private void recuperarDispArbitros(String idCamp, final String idZona, final String idCombate){
        // Obtener el id del campeonato al que pertenece la zona indicada
        Query consultaZona = mZonaDB.child(idCamp).child(idZona);
        consultaZona.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al buscar la Zona de Combate indicada. " + idZona, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) ID ZONA --> " + mIdZona, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mLista.clear();
                        ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class);
                        //mIdCamp = zona.getIdCamp();
                        mListaDatosExtraZona = zona.getListaDatosExtraCombates();
                        // Debemos localizar el combate correcto dentro de esa lista
                        for(int i = 0; i < mListaDatosExtraZona.size(); i++){
                            DatosExtraZonasCombate datos = mListaDatosExtraZona.get(i);
                            /*Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Leyendo datos EXTRA i = " + i, Toast.LENGTH_SHORT).show();
                            Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) datos.getIdCombate " + datos.getIdCombate(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LobbyArbitraje.this, "(Lobbyrbitraje) idCombate (parámetro) " + idCombate, Toast.LENGTH_SHORT).show();*/
                            if(datos.getIdCombate().equals(idCombate)){
                                mDatosCombate = datos;
                            }
                        }
                        int numArbisAsignados = mDatosCombate.getNumArbisAsignados();
                        if( numArbisAsignados > 0 ){
                            // Una vez localizados los datos del combate deseado deberemos recorrer la lista de árbitros asignados,
                            // si los hubiera, para poder comprobar su estado de disponiblidad
                            mListaIDsArbis = mDatosCombate.getListaIDsArbis();
                            for(int i = 0; i < mListaIDsArbis.size(); i++){
                                //Toast.makeText(LobbyArbitraje.this, "mListaIDsArbis( " + i + " ) " + mListaIDsArbis.get(i), Toast.LENGTH_SHORT).show();
                                recuperarDisponibilidadArbitro(mListaIDsArbis.get(i));
                            }
                            // Mostrar lista con los datos.
                            ArbitrosMiniList adapter = new ArbitrosMiniList(LobbyArbitraje.this, mLista);
                            adapter.setDropDownViewResource(R.layout.arbitro_single_layout_mini);
                            mListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) No existen Árbitros asignados a la zona y combate indicados.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NullPointerException e) {
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al cargar los Datos Extra de la Zona de Combate", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Tamaño lista datos extra -->> " + mListaDatosExtraZona.size(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Recuperar el estado de disponibilidad del árbitro indicado
    private void recuperarDisponibilidadArbitro(final String idArbitro){
        Query consultaArbis = mArbisDB.child(idArbitro);
        consultaArbis.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al localizar al Árbitro en la BD. (idArbitro => " + idArbitro + " )", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Ruta consulta Árbitro --> " + dataSnapshot.getRef(), Toast.LENGTH_SHORT).show();
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    //Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Nombre Árbitro --> " + arbi.getNombre(), Toast.LENGTH_SHORT).show();
                    mLista.add(arbi); // Añadir el Árbitro a la lista que se mostrará en pantalla.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
