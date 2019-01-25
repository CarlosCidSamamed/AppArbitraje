package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.CampeonatosExpandableListAdapter;
import com.fervenzagames.apparbitraje.Adapters.CombatesList;
import com.fervenzagames.apparbitraje.Edit_Activities.EditArbitroActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
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

public class DetalleArbitroActivity extends AppCompatActivity {

    private String mIdArbi;

    private Toolbar mToolbar;

    private CircleImageView mFoto;
    private Button mEditarBtn;

    private TextView mNombre;
    private TextView mDNI;
    private TextView mEmail;
    private TextView mNivel;
    private TextView mCargo;
    /*private TextView midCamp;
    private TextView mNombreCamp;
    private TextView mZona;*/
    // private ListView mListaCampsListView;

    private ExpandableListView mListaCampeonatosFechas;
    private ExpandableListAdapter mListaCampFechasAdapter;
    private List<String> mListaTitulos; // Nombre de los Campeonatos
    //private HashMap<String, List<String>> mListaDetalle; // Lista con las zonas de combate
    private List<String> mListaDetalle;

    private List<Combates> mListaCombates;
    private ListView mListaCombatesListView;

    private ImageView mConectado;

    private DatabaseReference mArbitroDB;
    private DatabaseReference mCampDB;
    private DatabaseReference mCombatesDB;
    private DatabaseReference mZonasDB;

    private List<Campeonatos> mListaCamps;

    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_arbitro);

        mToolbar = findViewById(R.id.arb_detalle_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Árbitro");

        Intent intent = getIntent();
        mIdArbi = intent.getStringExtra("idArbitro");

        mArbitroDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros").child(mIdArbi); // Referencia al árbitro deseado.
        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos"); // Lista de Campeonatos.
        mCombatesDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Combates");
        mZonasDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("ZonasCombate");

        mListaCamps = new ArrayList<>();

        mFoto = findViewById(R.id.arb_detalle_foto);
        mNombre = findViewById(R.id.arb_detalle_nombre);
        mDNI = findViewById(R.id.arb_detalle_dni);
        mEmail = findViewById(R.id.arb_detalle_email);
        mNivel = findViewById(R.id.arb_detalle_nivel);
        mCargo = findViewById(R.id.arb_detalle_cargo);
        // mZona = findViewById(R.id.arb_detalle_zonaCombate);
        // mListaCampsListView = findViewById(R.id.arb_detalle_listaCampeonatosListView);

        mListaCampeonatosFechas = findViewById(R.id.arb_detalle_listaCampsZonas);
        mListaTitulos = new ArrayList<>();
        //mListaDetalle = new HashMap<>();
        mListaDetalle = new ArrayList<>();

        mListaCombates = new ArrayList<>();
        mListaCombatesListView = findViewById(R.id.arb_detalle_listaCombates);

        mConectado = findViewById(R.id.arb_detalle_conectado);
/*        midCamp = findViewById(R.id.arb_detalle_idCamp);
        mNombreCamp = findViewById(R.id.arb_detalle_nombreCamp);*/

        extras = new Bundle();

        mArbitroDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                try {

                    String nombre = arbi.getNombre();
                    String dni = arbi.getDni();
                    String email = arbi.getEmail();
                    String foto = arbi.getFoto();
                    String nivel = arbi.getNivel();
                    String cargo = arbi.getCargo();
                    String idCamp = arbi.getIdCamp();
                    String zona = String.valueOf(arbi.getZonaCombate());
                    String conectado = String.valueOf(arbi.getConectado());

                    extras.putString("idArbitro", arbi.getIdArbitro());

                    mNombre.setText(nombre);
                    if(foto.equals("default")){
                        Picasso.get().load(R.drawable.default_avatar).into(mFoto); // Por defecto default_avatar.
                    } else {
                        Picasso.get().load(foto).into(mFoto);
                    }
                    mDNI.setText(dni);
                    mEmail.setText(email);
                    mNivel.setText(nivel);
                    mCargo.setText(cargo);
                    String idC = "ID Campeonato" + " : " + idCamp;
                    // midCamp.setText(idC);


                    /*// Zona de combate
                    String z = "Zona de Combate" + " : " + zona;
                    mZona.setText(z);*/

                    // Toast.makeText(DetalleArbitroActivity.this, "Valor de CONECTADO --> " + conectado, Toast.LENGTH_SHORT).show();

                    // Estado de conexión
                    if(conectado.equals("true")){
                        mConectado.setImageResource(R.drawable.punto_verde);
                    } else if(conectado.equals("false")){
                        mConectado.setImageResource(R.drawable.punto_rojo);
                    }

                    // Recorrer la lista de Campeonatos en los que ha participado este árbitro para recuperar la info de dichos campeonatos y mostrarla
                    // en el spinner.

                    List<String> lista = arbi.getListaCamps();

                    for(int i = 0; i < lista.size(); i++){
                        Query consulta = mCampDB.orderByChild("idCamp");

                        final String id = lista.get(i);

                        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mListaTitulos.clear();
                                for(DataSnapshot campSnapshot:dataSnapshot.getChildren()){
                                    Campeonatos camp = campSnapshot.getValue(Campeonatos.class);
                                    try {
                                        if(camp.getIdCamp().equals(id)){
                                            mListaTitulos.add(camp.getNombre()); // Obtener cabecera (header)
                                            //mListaDetalle = camp.getHashMapZonasCombate(camp); // Obtener detalle (detail)
                                            mListaDetalle.add(camp.getFecha());
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // ArrayAdapter adapter = new CampeonatosMiniList(DetalleArbitroActivity.this, mListaCamps);
                                // adapter.setDropDownViewResource(R.layout.camp_single_layout);
                                // mListaCampsListView.setAdapter(adapter);

                                // Pasarle los datos al ExpandableListView
                                mListaCampFechasAdapter = new CampeonatosExpandableListAdapter(DetalleArbitroActivity.this, mListaTitulos, mListaDetalle);
                                mListaCampeonatosFechas.setAdapter(mListaCampFechasAdapter);
                                // Expansión de un grupo
                                mListaCampeonatosFechas.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                    @Override
                                    public void onGroupExpand(int groupPosition) {
                                        Toast.makeText(DetalleArbitroActivity.this,
                                                "Se ha expandido el grupo de " + mListaTitulos.get(groupPosition),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                // Colapso de un grupo
                                mListaCampeonatosFechas.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                                    @Override
                                    public void onGroupCollapse(int groupPosition) {
                                        Toast.makeText(DetalleArbitroActivity.this,
                                                "Se ha colapsado el grupo de " + mListaTitulos.get(groupPosition),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                /*// Click en uno de los hijos.
                                mListaCampeonatosFechas.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                    @Override
                                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                        mListaCombates.clear(); // Borrar la lista anterior de Combates
                                        Toast.makeText(DetalleArbitroActivity.this,
                                                mListaTitulos.get(groupPosition) + " - > " +
                                                        mListaDetalle.get(childPosition),
                                                Toast.LENGTH_SHORT).show();
                                        // Añadir llamada a método que cargue los combates correspondientes al campeonato y a la zona de combate correspondientes.
                                        // El Campeonato lo obtenemos de groupPosition --> mListaTitulos.get(groupPosition) nos devuelve el nombre del Campeonato.
                                        // A partir del nombre podemos buscar en la BD y obtener el resto de datos.
                                        // La Zona de Combate la obtenemos de childPosition --> mListaDetalle.get(mListaTitulos.get(groupPosition)).get(childPosition)
                                        // nos devuelve el número de la zona de combate. A partir de ese número y el id del Campeonato podemos buscar el resto de datos
                                        // de la zona de combate.
                                        String idCamp = mListaTitulos.get(groupPosition);
                                        String idZona = mListaDetalle.get(childPosition);
                                        List<String> listaIDs = buscarIDsCombates(idCamp, idZona); // Localizar los IDs.
                                        buscarCombates(listaIDs);   // Localizar los Combates correspondientes a los IDs. En este método se actualiza mListaCombates.
                                        // Ahora debemos mostrar los datos de mListaCombates en mListaCombatesListView.
                                        ArrayAdapter adapter = new CombatesList(DetalleArbitroActivity.this, mListaCombates);
                                        adapter.setDropDownViewResource(R.layout.combate_single_layout);
                                        mListaCombatesListView.setAdapter(adapter);
                                        return false;
                                    }
                                });*/
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mEditarBtn = findViewById(R.id.arb_detalle_editarBtn);
        mEditarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editarArbIntent = new Intent(DetalleArbitroActivity.this, EditArbitroActivity.class);
                editarArbIntent.putExtras(extras);
                startActivity(editarArbIntent);
            }
        });

/*        mListaCampsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el campeonato que se ha pulsado
                Campeonatos camp = mListaCamps.get(position);
                // El id de ese Campeonato
                String idCamp = camp.getIdCamp();
                // Y buscar sus combates ordenados por modalidad.
                Query consulta = mCombatesDB;
            }
        });*/

    }

    // Este método localizará los IDs de los combates que pertenecen al Campeonato y a la Zona de Combate especificadas
    // y los devolverá en una lista
    public List<String> buscarIDsCombates(String idCamp, String idZona){

        List<String> lista = new ArrayList<>();

        Query consulta = mZonasDB.orderByChild("idZona").equalTo(idZona);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class); // Obtenemos los datos de la Zona deseada

                try {
                    List<DatosExtraZonasCombate> datosZonas = zona.getListaDatosExtraCombates();
                    // Obtenemos los datos de los Combates asignados a esa Zona.

                    List<String> listaIDsCombates = new ArrayList<>();
                    for(int i = 0; i < datosZonas.size(); i++){                    // Cargamos los IDs de los Combates de la Zona en una List.
                        String idCombate = datosZonas.get(i).getIdCombate();
                        listaIDsCombates.add(idCombate);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return lista;

    }

    // Búsqueda en la BD de los Combates
    public void buscarCombates(List<String> listaIDs){

        for(int i = 0; i < listaIDs.size(); i++){
            final String idCombate = listaIDs.get(i);
            Query consulta = mCombatesDB.orderByChild("id");
            consulta.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot combateSnapshot: dataSnapshot.getChildren()){
                        Combates combate = combateSnapshot.getValue(Combates.class);
                        if(combate.getId().equals(idCombate)){
                            mListaCombates.add(combate); // Añade el Combate correspondiente al ID que se ha leído de la lista de IDs.
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
